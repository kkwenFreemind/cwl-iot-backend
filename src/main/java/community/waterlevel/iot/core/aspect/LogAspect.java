package community.waterlevel.iot.core.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
import community.waterlevel.iot.common.enums.LogModuleEnum;
import community.waterlevel.iot.common.util.IPUtils;
import community.waterlevel.iot.common.annotation.Log;
import community.waterlevel.iot.core.security.util.SecurityUtils;
import community.waterlevel.iot.system.model.entity.LogJpa;
import community.waterlevel.iot.system.service.SystemLogJpaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Aspect for operation logging using AOP.
 * Intercepts methods annotated with {@code @Log}, records execution details,
 * parameters, results, exceptions,
 * and user/device information, and persists logs for auditing and monitoring.
 * Supports request/response logging, performance timing, and user agent
 * analysis.
 *
 * @author Ray.Hao
 * @since 2024/6/25
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    /**
     * Service for persisting system logs.
     */
    private final SystemLogJpaService systemLogService;

    /**
     * HTTP servlet request for extracting request details.
     */
    private final HttpServletRequest request;

    /**
     * Pointcut for methods annotated with
     * {@link com.youlai.boot.LogJpa.annotation.Log}.
     */
    @Pointcut("@annotation(com.youlai.boot.common.annotation.Log)")
    public void logPointcut() {
    }

    /**
     * Around advice that executes after the annotated method is called.
     * <p>
     * Captures execution time, handles exceptions, and delegates log persistence.
     * </p>
     *
     * @param joinPoint     the join point representing the intercepted method
     * @param logAnnotation the {@link Log} annotation instance
     * @return the result of the method execution
     * @throws Throwable if the intercepted method throws any exception
     */
    @Around("logPointcut() && @annotation(logAnnotation)")
    public Object doAround(ProceedingJoinPoint joinPoint, Log logAnnotation) throws Throwable {
        // Obtain user ID before method execution to avoid context loss
        Long userId = SecurityUtils.getUserId();

        TimeInterval timer = DateUtil.timer();
        Object result = null;
        Exception exception = null;

        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long executionTime = timer.interval(); // Execution duration
            this.saveLog(joinPoint, exception, result, logAnnotation, executionTime, userId);
        }
        return result;
    }

    /**
     * Persists a system log entry with detailed request and response information.
     *
     * @param joinPoint     the join point representing the intercepted method
     * @param e             the exception thrown by the method, if any
     * @param jsonResult    the response result object
     * @param logAnnotation the {@link Log} annotation instance
     * @param executionTime the execution duration in milliseconds
     * @param userId        the ID of the user performing the operation
     */
    private void saveLog(final JoinPoint joinPoint, final Exception e, Object jsonResult, Log logAnnotation,
            long executionTime, Long userId) {
        String requestURI = request.getRequestURI();
        // 建立日誌記錄
        LogJpa log = new LogJpa();
        log.setExecutionTime(executionTime);
        if (logAnnotation == null && e != null) {
            log.setModule(LogModuleEnum.EXCEPTION.getModuleName());
            log.setContent("An exception occurred in the system");
            this.setRequestParameters(joinPoint, log);
            log.setResponseContent(JSONUtil.toJsonStr(e.getStackTrace()));
        } else if (logAnnotation != null) {
            log.setModule(logAnnotation.module().getModuleName());
            log.setContent(logAnnotation.value());
            // 請求引數
            if (logAnnotation.params()) {
                this.setRequestParameters(joinPoint, log);
            }
            // 響應結果
            if (logAnnotation.result() && jsonResult != null) {
                log.setResponseContent(JSONUtil.toJsonStr(jsonResult));
            }
        }
        log.setRequestUri(requestURI);
        log.setCreateBy(userId);
        String ipAddr = IPUtils.getIpAddr(request);
        if (StrUtil.isNotBlank(ipAddr)) {
            log.setIp(ipAddr);
            String region = IPUtils.getRegion(ipAddr);
            if (StrUtil.isNotBlank(region)) {
                String[] regionArray = region.split("\\|");
                if (regionArray.length > 2) {
                    log.setProvince(regionArray[2]);
                    log.setCity(regionArray[3]);
                }
            }
        }

        String userAgentString = request.getHeader("User-Agent");
        UserAgent userAgent = resolveUserAgent(userAgentString);
        if (Objects.nonNull(userAgent)) {
            log.setOs(userAgent.getOs().getName());
            log.setBrowser(userAgent.getBrowser().getName());
            log.setBrowserVersion(userAgent.getBrowser().getVersion(userAgentString));
        }
        systemLogService.saveLog(log);
    }

    /**
     * Sets request parameters into the log entity based on the HTTP method and join
     * point arguments.
     *
     * @param joinPoint the join point representing the intercepted method
     * @param log       the log entity to populate
     */
    private void setRequestParameters(JoinPoint joinPoint, LogJpa log) {
        String requestMethod = request.getMethod();
        log.setRequestMethod(requestMethod);
        // remove aliyun.oss.HttpMethod;
        if ("GET".equalsIgnoreCase(requestMethod) || "PUT".equalsIgnoreCase(requestMethod)
                || "POST".equalsIgnoreCase(requestMethod)) {
            String params = convertArgumentsToString(joinPoint.getArgs());
            log.setRequestParams(StrUtil.sub(params, 0, 65535));
        } else {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                Map<?, ?> paramsMap = (Map<?, ?>) attributes.getRequest()
                        .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                log.setRequestParams(StrUtil.sub(paramsMap.toString(), 0, 65535));
            } else {
                log.setRequestParams("");
            }
        }
    }

    /**
     * Converts an array of method arguments to a string, filtering out file uploads
     * and servlet objects.
     *
     * @param paramsArray the array of method arguments
     * @return a string representation of the arguments
     */
    private String convertArgumentsToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null) {
            for (Object param : paramsArray) {
                if (!shouldFilterObject(param)) {
                    params.append(JSONUtil.toJsonStr(param)).append(" ");
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * Determines whether the given object should be filtered out from logging
     * (e.g., file uploads, servlet objects).
     *
     * @param obj the object to check
     * @return {@code true} if the object should be filtered; {@code false}
     *         otherwise
     */
    private boolean shouldFilterObject(Object obj) {
        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            return MultipartFile.class.isAssignableFrom(clazz.getComponentType());
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection<?> collection = (Collection<?>) obj;
            return collection.stream().anyMatch(item -> item instanceof MultipartFile);
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map<?, ?> map = (Map<?, ?>) obj;
            return map.values().stream().anyMatch(value -> value instanceof MultipartFile);
        }
        return obj instanceof MultipartFile || obj instanceof HttpServletRequest || obj instanceof HttpServletResponse;
    }

    /**
     * Parses the User-Agent string to extract client browser and OS information.
     *
     * @param userAgentString the User-Agent string from the HTTP request header
     * @return a {@link UserAgent} object containing parsed client information, or
     *         {@code null} if blank
     */
    public UserAgent resolveUserAgent(String userAgentString) {
        if (StrUtil.isBlank(userAgentString)) {
            return null;
        }
        // String userAgentStringMD5 = DigestUtil.md5Hex(userAgentString);
        UserAgent userAgent = UserAgentUtil.parse(userAgentString);
        return userAgent;
    }

}
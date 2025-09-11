package community.waterlevel.iot.core.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import community.waterlevel.iot.common.annotation.RepeatSubmit;
import community.waterlevel.iot.common.constant.RedisConstants;
import community.waterlevel.iot.common.constant.SecurityConstants;
import community.waterlevel.iot.common.exception.BusinessException;
import community.waterlevel.iot.common.result.ResultCode;
import community.waterlevel.iot.common.util.IPUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

/**
 * Aspect for preventing duplicate submissions using distributed locks.
 * Intercepts methods annotated with {@code @RepeatSubmit} and enforces a lock based on user and request identity.
 * Ensures idempotency for sensitive operations by rejecting repeated requests within a specified timeout.
 *
 * @author Ray.Hao
 * @since 2.3.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RepeatSubmitAspect {

    /**
     * Redisson client for distributed locking.
     */
    private final RedissonClient redissonClient;

    /**
     * Pointcut for methods annotated with
     * {@link community.waterlevel.iot.common.annotation.RepeatSubmit}.
     *
     * @param repeatSubmit the annotation instance
     */
    @Pointcut("@annotation(repeatSubmit)")
    public void repeatSubmitPointCut(RepeatSubmit repeatSubmit) {
    }

    /**
     * Around advice that handles duplicate submission prevention logic.
     * <p>
     * Attempts to acquire a distributed lock for the current user and request. If
     * the lock cannot be acquired,
     * a {@link BusinessException} is thrown to indicate a duplicate request.
     * </p>
     *
     * @param pjp          the join point representing the intercepted method
     * @param repeatSubmit the {@link RepeatSubmit} annotation instance
     * @return the result of the method execution
     * @throws Throwable if the intercepted method throws any exception
     */
    @Around(value = "repeatSubmitPointCut(repeatSubmit)", argNames = "pjp,repeatSubmit")
    public Object handleRepeatSubmit(ProceedingJoinPoint pjp, RepeatSubmit repeatSubmit) throws Throwable {
        String lockKey = buildLockKey();

        int expire = repeatSubmit.expire();
        RLock lock = redissonClient.getLock(lockKey);

        boolean locked = lock.tryLock(0, expire, TimeUnit.SECONDS);
        if (!locked) {
            throw new BusinessException(ResultCode.USER_DUPLICATE_REQUEST);
        }
        return pjp.proceed();
    }

    /**
     * Builds the distributed lock key for duplicate submission prevention.
     * <p>
     * The key is composed of the user's unique identifier and the request's unique
     * identifier (method and URI).
     * </p>
     *
     * @return the lock key string
     */
    private String buildLockKey() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String userIdentifier = getUserIdentifier(request);
        String requestIdentifier = StrUtil.join(":", request.getMethod(), request.getRequestURI());
        return StrUtil.format(RedisConstants.Lock.RESUBMIT, userIdentifier, requestIdentifier);
    }

    /**
     * Retrieves the unique user identifier for duplicate submission detection.
     * <p>
     * 1. Attempts to extract the token from the request header and uses its SHA-256
     * hash as the identifier.<br>
     * 2. If the token is absent, falls back to using the client IP address.
     * </p>
     *
     * @param request the HTTP servlet request
     * @return the unique user identifier string
     */
    private String getUserIdentifier(HttpServletRequest request) {

        String userIdentifier;

        String tokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StrUtil.isNotBlank(tokenHeader) && tokenHeader.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)) {
            String rawToken = tokenHeader.substring(SecurityConstants.BEARER_TOKEN_PREFIX.length());
            userIdentifier = DigestUtil.sha256Hex(rawToken);
        } else {
            userIdentifier = IPUtils.getIpAddr(request);
        }
        return userIdentifier;
    }

}


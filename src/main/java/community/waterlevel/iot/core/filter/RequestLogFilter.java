package community.waterlevel.iot.core.filter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import community.waterlevel.iot.common.util.IPUtils;

/**
 * Servlet filter for logging HTTP request details.
 * Logs the client IP address and request URI before processing each request.
 * Extends {@link CommonsRequestLoggingFilter} for integration with Spring's logging infrastructure.
 *
 * @author haoxr
 * @since 2023/03/03
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Configuration
@Slf4j
public class RequestLogFilter extends CommonsRequestLoggingFilter {

    /**
     * Determines whether the request should be logged based on the logger's level.
     *
     * @param request the HTTP servlet request
     * @return {@code true} if logging is enabled at INFO level; {@code false}
     *         otherwise
     */
    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return this.logger.isInfoEnabled();
    }

    /**
     * Logs request details before the request is processed.
     *
     * @param request the HTTP servlet request
     * @param message the log message
     */
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        String requestURI = request.getRequestURI();
        String ip = IPUtils.getIpAddr(request);
        log.info("request,ip:{}, uri: {}", ip, requestURI);
        super.beforeRequest(request, message);
    }

    /**
     * Invoked after the request has been processed. Delegates to the superclass
     * implementation.
     *
     * @param request the HTTP servlet request
     * @param message the log message
     */
    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        super.afterRequest(request, message);
    }

}
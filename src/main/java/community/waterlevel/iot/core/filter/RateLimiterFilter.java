package community.waterlevel.iot.core.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import community.waterlevel.iot.common.constant.RedisConstants;
import community.waterlevel.iot.common.constant.SystemConstants;
import community.waterlevel.iot.common.result.ResultCode;
import community.waterlevel.iot.common.util.IPUtils;
import community.waterlevel.iot.common.util.ResponseUtils;
import community.waterlevel.iot.system.service.SystemConfigService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Servlet filter for IP-based rate limiting.
 * Restricts the number of requests from a single IP address within a specified time window,
 * using Redis for request counting and system configuration for dynamic threshold control.
 * Returns a standardized error response when the rate limit is exceeded.
 *
 * @author Theo
 * @since 2024/08/10 14:38
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Slf4j
public class RateLimiterFilter extends OncePerRequestFilter {

    /**
     * Redis template for accessing rate limiting counters.
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Service for retrieving system configuration values.
     */
    private final SystemConfigService configService;

    /**
     * Default IP rate limit threshold (requests per second).
     */
    private static final long DEFAULT_IP_LIMIT = 10L;

    /**
     * Constructs a new {@code RateLimiterFilter} with the given Redis template and
     * config service.
     *
     * @param redisTemplate the Redis template for rate limiting
     * @param configService the system configuration service
     */
    public RateLimiterFilter(RedisTemplate<String, Object> redisTemplate, SystemConfigService configService) {
        this.redisTemplate = redisTemplate;
        this.configService = configService;
    }

    /**
     * Determines whether the given IP address has exceeded the allowed request
     * rate.
     * <p>
     * By default, limits each IP to 10 requests per second. The threshold can be
     * adjusted via system configuration.
     * If no threshold is configured, rate limiting is skipped.
     * </p>
     *
     * @param ip the IP address to check
     * @return {@code true} if the IP is rate limited; {@code false} otherwise
     */
    public boolean rateLimit(String ip) {

        String key = StrUtil.format(RedisConstants.RateLimiter.IP, ip);

        Long count = redisTemplate.opsForValue().increment(key);
        if (count == null || count == 1) {
            redisTemplate.expire(key, 1, TimeUnit.SECONDS);
        }

        String systemConfig = configService.getConfigValue(SystemConstants.SYSTEM_CONFIG_IP_QPS_LIMIT_KEY);
        if (StrUtil.isBlank(systemConfig)) {
            log.warn("The system does not configure the current limit threshold, skip the current limit");
            return false;
        }

        long limit = Convert.toLong(systemConfig, DEFAULT_IP_LIMIT);
        return count != null && count > limit;
    }

    /**
     * Executes the IP rate limiting logic for each incoming request.
     * <p>
     * If the IP exceeds the allowed request rate, a rate limit error response is
     * returned and the filter chain is not continued.
     * Otherwise, the request proceeds through the filter chain.
     * </p>
     *
     * @param request     the HTTP servlet request
     * @param response    the HTTP servlet response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {
        // Get the request's IP address
        String ip = IPUtils.getIpAddr(request);

        // Check if the IP is rate limited
        if (rateLimit(ip)) {
            // Return rate limit error response
            ResponseUtils.writeErrMsg(response, ResultCode.REQUEST_CONCURRENCY_LIMIT_EXCEEDED);
            return;
        }

        // Not rate limited, continue with the filter chain
        filterChain.doFilter(request, response);
    }
}


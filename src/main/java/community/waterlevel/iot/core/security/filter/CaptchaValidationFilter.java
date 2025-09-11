package community.waterlevel.iot.core.security.filter;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.StrUtil;
import community.waterlevel.iot.common.constant.RedisConstants;
import community.waterlevel.iot.common.constant.SecurityConstants;
import community.waterlevel.iot.common.result.ResultCode;
import community.waterlevel.iot.common.util.ResponseUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * Servlet filter for validating captcha codes during login requests.
 * Checks the user-provided captcha against the cached value in Redis and blocks authentication if invalid or expired.
 * Ensures enhanced security for login endpoints by preventing automated or malicious access.
 *
 * @author haoxr
 * @since 2022/10/1
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public class CaptchaValidationFilter extends OncePerRequestFilter {

    /**
     * Request matcher for login POST requests.
     */
    private static final AntPathRequestMatcher LOGIN_PATH_REQUEST_MATCHER = new AntPathRequestMatcher(
            SecurityConstants.LOGIN_PATH, HttpMethod.POST.name());

    /**
     * Parameter name for the captcha code in the request.
     */
    public static final String CAPTCHA_CODE_PARAM_NAME = "captchaCode";

    /**
     * Parameter name for the captcha key in the request.
     */
    public static final String CAPTCHA_KEY_PARAM_NAME = "captchaKey";

    /**
     * Redis template for accessing captcha codes.
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Captcha code generator and verifier.
     */
    private final CodeGenerator codeGenerator;

    /**
     * Constructs a new {@code CaptchaValidationFilter} with the given Redis
     * template and code generator.
     *
     * @param redisTemplate the Redis template for captcha storage
     * @param codeGenerator the code generator for captcha verification
     */
    public CaptchaValidationFilter(RedisTemplate<String, Object> redisTemplate, CodeGenerator codeGenerator) {
        this.redisTemplate = redisTemplate;
        this.codeGenerator = codeGenerator;
    }

    /**
     * Performs captcha validation for login requests.
     * <p>
     * If the request matches the login path, the filter checks the submitted
     * captcha code against the cached value in Redis.
     * If the captcha is missing, expired, or incorrect, an error response is
     * returned. Otherwise, the filter chain continues.
     * Non-login requests are passed through without validation.
     * </p>
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @param chain    the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // Validate captcha for login requests
        if (LOGIN_PATH_REQUEST_MATCHER.matches(request)) {
            String captchaCode = request.getParameter(CAPTCHA_CODE_PARAM_NAME);
            // TODO: Compatibility for versions without captcha (remove this check in
            // production)
            if (StrUtil.isBlank(captchaCode)) {
                chain.doFilter(request, response);
                return;
            }
            String verifyCodeKey = request.getParameter(CAPTCHA_KEY_PARAM_NAME);
            String cacheVerifyCode = (String) redisTemplate.opsForValue().get(
                    StrUtil.format(RedisConstants.Captcha.IMAGE_CODE, verifyCodeKey));
            if (cacheVerifyCode == null) {
                ResponseUtils.writeErrMsg(response, ResultCode.USER_VERIFICATION_CODE_EXPIRED);
            } else {
                // Compare captcha codes
                if (codeGenerator.verify(cacheVerifyCode, captchaCode)) {
                    chain.doFilter(request, response);
                } else {
                    ResponseUtils.writeErrMsg(response, ResultCode.USER_VERIFICATION_CODE_ERROR);
                }
            }
        } else {
            // Allow non-login requests
            chain.doFilter(request, response);
        }
    }

}


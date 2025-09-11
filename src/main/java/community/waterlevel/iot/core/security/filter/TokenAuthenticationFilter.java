package community.waterlevel.iot.core.security.filter;

import cn.hutool.core.util.StrUtil;
import community.waterlevel.iot.common.constant.SecurityConstants;
import community.waterlevel.iot.common.result.ResultCode;
import community.waterlevel.iot.common.util.ResponseUtils;
import community.waterlevel.iot.core.security.token.TokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter for validating and processing authentication tokens.
 * Checks the validity of Bearer tokens in HTTP requests, sets the authentication context for valid tokens,
 * and blocks requests with invalid or expired tokens.
 * Ensures secure, stateless authentication for protected endpoints.
 *
 * @author wangtao
 * @since 2025/3/6 16:50
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Token manager for validating and parsing authentication tokens.
     */
    private final TokenManager tokenManager;

    /**
     * Constructs a new {@code TokenAuthenticationFilter} with the given token
     * manager.
     *
     * @param tokenManager the token manager for token validation and parsing
     */
    public TokenAuthenticationFilter(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    /**
     * Validates the Bearer token in the Authorization header, including signature
     * and expiration checks.
     * <p>
     * If the token is valid, it is parsed into an
     * {@link org.springframework.security.core.Authentication} object
     * and set in the Spring Security context. If the token is invalid or an error
     * occurs, an error response is returned
     * and the security context is cleared. Otherwise, the filter chain continues.
     * </p>
     *
     * @param request     the HTTP servlet request
     * @param response    the HTTP servlet response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            if (StrUtil.isNotBlank(authorizationHeader)
                    && authorizationHeader.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)) {

                // Remove Bearer prefix to get the raw token
                String rawToken = authorizationHeader.substring(SecurityConstants.BEARER_TOKEN_PREFIX.length());

                // Validate the token (signature and expiration)
                boolean isValidToken = tokenManager.validateToken(rawToken);
                if (!isValidToken) {
                    ResponseUtils.writeErrMsg(response, ResultCode.ACCESS_TOKEN_INVALID);
                    return;
                }

                // Parse the token into a Spring Security Authentication object
                Authentication authentication = tokenManager.parseToken(rawToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Ensure security context is cleared to prevent context residue
            SecurityContextHolder.clearContext();
            ResponseUtils.writeErrMsg(response, ResultCode.ACCESS_TOKEN_INVALID);
            return;
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}


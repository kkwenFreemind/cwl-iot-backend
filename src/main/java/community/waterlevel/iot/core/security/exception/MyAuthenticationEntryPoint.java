package community.waterlevel.iot.core.security.exception;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import community.waterlevel.iot.common.result.ResultCode;
import community.waterlevel.iot.common.util.ResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Custom authentication entry point for handling authentication failures in Spring Security.
 * Sends standardized JSON error responses for invalid credentials, missing or invalid tokens,
 * and other authentication-related exceptions.
 *
 * @author Ray.Hao
 * @since 2.0.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Entry point for handling authentication failures.
     * <p>
     * Returns a standardized error response based on the type of authentication
     * exception encountered.
     * </p>
     *
     * @param request       the HTTP servlet request that triggered the exception
     *                      (can be used to access headers, parameters, etc.)
     * @param response      the HTTP servlet response (used to write error
     *                      information)
     * @param authException the authentication exception containing the specific
     *                      failure reason
     * @throws IOException      if an input or output error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        if (authException instanceof BadCredentialsException) {
            // Username or password is incorrect
            ResponseUtils.writeErrMsg(response, ResultCode.USER_PASSWORD_ERROR);
        } else if (authException instanceof InsufficientAuthenticationException) {
            // Missing Authorization header, invalid token format, expired token, or
            // signature verification failed
            ResponseUtils.writeErrMsg(response, ResultCode.ACCESS_TOKEN_INVALID);
        } else {
            // Other unhandled authentication exceptions (e.g., account locked, account
            // disabled, etc.)
            ResponseUtils.writeErrMsg(response, ResultCode.USER_LOGIN_EXCEPTION, authException.getMessage());
        }
    }
}






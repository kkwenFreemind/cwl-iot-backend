package community.waterlevel.iot.core.security.exception;

import community.waterlevel.iot.common.result.ResultCode;
import community.waterlevel.iot.common.util.ResponseUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom handler for access denied (403 Forbidden) errors in Spring Security.
 * Sends a standardized JSON error response when a user attempts to access a resource without sufficient permissions.
 *
 * @author Ray.Hao
 * @since 2.0.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public class MyAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Handles access denied exceptions by returning an unauthorized access error
     * response.
     *
     * @param request               the HTTP servlet request
     * @param response              the HTTP servlet response
     * @param accessDeniedException the exception indicating access is denied
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) {
        ResponseUtils.writeErrMsg(response, ResultCode.ACCESS_UNAUTHORIZED);
    }

}

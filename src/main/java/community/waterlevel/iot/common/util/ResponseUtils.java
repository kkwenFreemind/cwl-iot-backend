package community.waterlevel.iot.common.util;

import cn.hutool.json.JSONUtil;
import community.waterlevel.iot.common.result.Result;
import community.waterlevel.iot.common.result.ResultCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for writing standardized JSON error responses to HTTP clients.
 * Provides methods to send error messages and set appropriate HTTP status
 * codes,
 * typically used in filters or exception handlers.
 *
 * @author Ray.Hao
 * @since 2.0.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Slf4j
public class ResponseUtils {

    /**
     * Writes an error message to the HTTP response using the specified result code.
     * <p>
     * This method is intended for use in filters or exception handlers to return a
     * standardized JSON error response.
     * The HTTP status code is determined by the result code.
     *
     * @param response   the {@link HttpServletResponse} to write to
     * @param resultCode the {@link ResultCode} representing the error
     */
    public static void writeErrMsg(HttpServletResponse response, ResultCode resultCode) {
        int status = getHttpStatus(resultCode);

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter writer = response.getWriter()) {
            String jsonResponse = JSONUtil.toJsonStr(Result.failed(resultCode));
            writer.print(jsonResponse);
            writer.flush(); 
        } catch (IOException e) {
            log.error("Response exception handling failed", e);
        }
    }

    /**
     * Writes an error message with a custom message to the HTTP response using the
     * specified result code.
     * <p>
     * This method is intended for use in filters or exception handlers to return a
     * standardized JSON error response with a custom message.
     * The HTTP status code is determined by the result code.
     *
     * @param response   the {@link HttpServletResponse} to write to
     * @param resultCode the {@link ResultCode} representing the error
     * @param message    the custom error message to include in the response
     */
    public static void writeErrMsg(HttpServletResponse response, ResultCode resultCode, String message) {
        int status = getHttpStatus(resultCode);

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (PrintWriter writer = response.getWriter()) {
            String jsonResponse = JSONUtil.toJsonStr(Result.failed(resultCode, message));
            writer.print(jsonResponse);
            writer.flush(); 
        } catch (IOException e) {
            log.error("Response exception handling failed", e);
        }
    }

    /**
     * Returns the appropriate HTTP status code for the given result code.
     * <p>
     * Maps authentication-related result codes to {@code 401 Unauthorized}, and all
     * others to {@code 400 Bad Request}.
     *
     * @param resultCode the {@link ResultCode} to evaluate
     * @return the corresponding HTTP status code
     */
    private static int getHttpStatus(ResultCode resultCode) {
        return switch (resultCode) {
            case ACCESS_UNAUTHORIZED, ACCESS_TOKEN_INVALID, REFRESH_TOKEN_INVALID -> HttpStatus.UNAUTHORIZED.value();
            default -> HttpStatus.BAD_REQUEST.value();
        };
    }

}

package community.waterlevel.iot.common.exception;

import lombok.Getter;
import org.slf4j.helpers.MessageFormatter;

import community.waterlevel.iot.common.result.IResultCode;

/**
 * Custom exception class for business logic errors.
 * Used to represent application-specific exceptions with support for error codes,
 * formatted messages, and underlying causes.
 * Facilitates standardized error handling and response in the service layer.
 *
 * @author Ray
 * @since 2022/7/31
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * Result code representing the business error.
     */
    public IResultCode resultCode;

    /**
     * Constructs a BusinessException with a result code.
     *
     * @param errorCode the result code representing the business error
     */
    public BusinessException(IResultCode errorCode) {
        super(errorCode.getMsg());
        this.resultCode = errorCode;
    }

    /**
     * Constructs a BusinessException with a result code and custom message.
     *
     * @param errorCode the result code representing the business error
     * @param message   the custom error message
     */
    public BusinessException(IResultCode errorCode, String message) {
        super(message);
        this.resultCode = errorCode;
    }

    /**
     * Constructs a BusinessException with a message and cause.
     *
     * @param message the error message
     * @param cause   the cause of the exception
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a BusinessException with a cause.
     *
     * @param cause the cause of the exception
     */
    public BusinessException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a BusinessException with a formatted message and arguments.
     *
     * @param message the error message pattern
     * @param args    arguments to be formatted into the message
     */
    public BusinessException(String message, Object... args) {
        super(formatMessage(message, args));
    }

    /**
     * Formats the error message with arguments using SLF4J MessageFormatter.
     *
     * @param message the error message pattern
     * @param args    arguments to be formatted into the message
     * @return formatted error message
     */
    private static String formatMessage(String message, Object... args) {
        return MessageFormatter.arrayFormat(message, args).getMessage();
    }
}

package community.waterlevel.iot.common.exception;

import cn.hutool.core.util.StrUtil;
import community.waterlevel.iot.common.result.Result;
import community.waterlevel.iot.common.result.ResultCode;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.sql.SQLSyntaxErrorException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * Centralizes the handling of various exceptions thrown by controllers and services,
 * providing standardized error responses and logging.
 * Improves API robustness, user experience, and maintainability by managing validation,
 * business, SQL, and unexpected exceptions in a unified way.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles BindException for validation errors on binding request parameters.
     *
     * @param e the BindException
     * @return Result with user request parameter error and details
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(BindException e) {
        log.error("BindException:{}", e.getMessage());
        String msg = e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("；"));
        return Result.failed(ResultCode.USER_REQUEST_PARAMETER_ERROR, msg);
    }

    /**
     * Handles ConstraintViolationException for validation errors on method
     * parameters.
     *
     * @param e the ConstraintViolationException
     * @return Result with invalid user input error and details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(ConstraintViolationException e) {
        log.error("ConstraintViolationException:{}", e.getMessage());
        String msg = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("；"));
        return Result.failed(ResultCode.INVALID_USER_INPUT, msg);
    }

    /**
     * Handles MethodArgumentNotValidException for validation errors on request
     * body.
     *
     * @param e the MethodArgumentNotValidException
     * @return Result with invalid user input error and details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException:{}", e.getMessage());
        String msg = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("；"));
        return Result.failed(ResultCode.INVALID_USER_INPUT, msg);
    }

    /**
     * Handles NoHandlerFoundException for 404 not found errors.
     *
     * @param e the NoHandlerFoundException
     * @return Result indicating the interface does not exist
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public <T> Result<T> processException(NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        return Result.failed(ResultCode.INTERFACE_NOT_EXIST);
    }

    /**
     * Handles MissingServletRequestParameterException for missing required
     * parameters.
     *
     * @param e the MissingServletRequestParameterException
     * @return Result indicating required parameter is missing
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return Result.failed(ResultCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY);
    }

    /**
     * Handles MethodArgumentTypeMismatchException for parameter type mismatches.
     *
     * @param e the MethodArgumentTypeMismatchException
     * @return Result indicating parameter format mismatch
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage(), e);
        return Result.failed(ResultCode.PARAMETER_FORMAT_MISMATCH, "Type error");
    }

    /**
     * Handles ServletException for servlet-related errors.
     *
     * @param e the ServletException
     * @return Result with error message
     */
    @ExceptionHandler(ServletException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(ServletException e) {
        log.error(e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    /**
     * Handles IllegalArgumentException for invalid arguments.
     *
     * @param e the IllegalArgumentException
     * @return Result with error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal argument exception: {}", e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    /**
     * Handles JsonProcessingException for JSON parsing errors.
     *
     * @param e the JsonProcessingException
     * @return Result with error message
     */
    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> handleJsonProcessingException(JsonProcessingException e) {
        log.error("JSON processing exception: {}", e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    /**
     * Handles HttpMessageNotReadableException for unreadable HTTP request bodies.
     *
     * @param e the HttpMessageNotReadableException
     * @return Result with error message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        String errorMessage = "Request body cannot be empty";
        Throwable cause = e.getCause();
        if (cause != null) {
            errorMessage = convertMessage(cause);
        }
        return Result.failed(errorMessage);
    }

    /**
     * Handles TypeMismatchException for type conversion errors.
     *
     * @param e the TypeMismatchException
     * @return Result with error message
     */
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(TypeMismatchException e) {
        log.error(e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    /**
     * Handles BadSqlGrammarException for SQL syntax errors and database access
     * denial.
     *
     * @param e the BadSqlGrammarException
     * @return Result with error message or database access denied code
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public <T> Result<T> handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error(e.getMessage(), e);
        String errorMsg = e.getMessage();
        if (StrUtil.isNotBlank(errorMsg) && errorMsg.contains("denied to user")) {
            return Result.failed(ResultCode.DATABASE_ACCESS_DENIED);
        } else {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * Handles SQLSyntaxErrorException for SQL syntax errors.
     *
     * @param e the SQLSyntaxErrorException
     * @return Result with error message
     */
    @ExceptionHandler(SQLSyntaxErrorException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public <T> Result<T> processSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        log.error(e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    /**
     * Handles custom BusinessException for business logic errors.
     *
     * @param e the BusinessException
     * @return Result with error code and message
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> handleBizException(BusinessException e) {
        log.error("Business exception", e);
        if (e.getResultCode() != null) {
            return Result.failed(e.getResultCode(), e.getMessage());
        }
        return Result.failed(e.getMessage());
    }

    /**
     * Handles all uncaught exceptions. Re-throws Spring Security exceptions for
     * custom handling.
     *
     * @param e the Exception
     * @return Result with localized error message
     * @throws Exception if the exception is a Spring Security exception
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> handleException(Exception e) throws Exception {
        // Re-throw Spring Security exceptions for custom handling
        if (e instanceof AccessDeniedException
                || e instanceof AuthenticationException) {
            throw e;
        }
        log.error("Unknown exception", e);
        return Result.failed(e.getLocalizedMessage());
    }

    /**
     * Converts a Throwable to a user-friendly error message for type errors in
     * request bodies.
     *
     * @param throwable the cause of the error
     * @return formatted error message
     */
    private String convertMessage(Throwable throwable) {
        String error = throwable.toString();
        String regulation = "\\[\"(.*?)\"]+";
        Pattern pattern = Pattern.compile(regulation);
        Matcher matcher = pattern.matcher(error);
        String group = "";
        if (matcher.find()) {
            String matchString = matcher.group();
            matchString = matchString.replace("[", "").replace("]", "");
            matchString = "%s field type error".formatted(matchString.replaceAll("\"", ""));
            group += matchString;
        }
        return group;
    }
}
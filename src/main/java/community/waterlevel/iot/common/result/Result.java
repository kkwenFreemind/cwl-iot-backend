package community.waterlevel.iot.common.result;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * Standardized response wrapper for API results.
 * Encapsulates response code, message, and data payload.
 * Provides static factory methods for success and failure responses to ensure
 * consistency across the application.
 *
 * @param <T> the type of the response data
 *
 * @author Ray
 * @since 2022/1/30
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 **/
@Data
public class Result<T> implements Serializable {

    /**
     * Response code indicating the result status of the operation.
     */
    private String code;

    /**
     * The data payload returned by the operation, if any.
     */
    private T data;

    /**
     * Message providing additional information about the result, such as error
     * details or success confirmation.
     */
    private String msg;

    /**
     * Returns a successful result with no data.
     *
     * @param <T> the type of the data
     * @return a Result instance representing success
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * Returns a successful result with the specified data.
     *
     * @param data the data to include in the result
     * @param <T>  the type of the data
     * @return a Result instance representing success with data
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setData(data);
        return result;
    }

    /**
     * Returns a failed result with a default system error code and message.
     *
     * @param <T> the type of the data
     * @return a Result instance representing failure
     */
    public static <T> Result<T> failed() {
        return result(ResultCode.SYSTEM_ERROR.getCode(), ResultCode.SYSTEM_ERROR.getMsg(), null);
    }

    /**
     * Returns a failed result with a custom message and default system error code.
     *
     * @param msg the custom error message
     * @param <T> the type of the data
     * @return a Result instance representing failure
     */
    public static <T> Result<T> failed(String msg) {
        return result(ResultCode.SYSTEM_ERROR.getCode(), msg, null);
    }

    /**
     * Returns a success or failure result based on the provided status.
     *
     * @param status true for success, false for failure
     * @param <T>    the type of the data
     * @return a Result instance representing success or failure
     */
    public static <T> Result<T> judge(boolean status) {
        if (status) {
            return success();
        } else {
            return failed();
        }
    }

    /**
     * Returns a failed result with the specified result code and its associated
     * message.
     *
     * @param resultCode the result code representing the failure
     * @param <T>        the type of the data
     * @return a Result instance representing failure
     */
    public static <T> Result<T> failed(IResultCode resultCode) {
        return result(resultCode.getCode(), resultCode.getMsg(), null);
    }

    /**
     * Returns a failed result with the specified result code and a custom message.
     * If the custom message is blank, the default message from the result code is
     * used.
     *
     * @param resultCode the result code representing the failure
     * @param msg        the custom error message
     * @param <T>        the type of the data
     * @return a Result instance representing failure
     */
    public static <T> Result<T> failed(IResultCode resultCode, String msg) {
        return result(resultCode.getCode(), StrUtil.isNotBlank(msg) ? msg : resultCode.getMsg(), null);
    }

    /**
     * Constructs a result with the specified result code and data.
     *
     * @param resultCode the result code
     * @param data       the data to include in the result
     * @param <T>        the type of the data
     * @return a Result instance
     */
    private static <T> Result<T> result(IResultCode resultCode, T data) {
        return result(resultCode.getCode(), resultCode.getMsg(), data);
    }

    /**
     * Constructs a result with the specified code, message, and data.
     *
     * @param code the response code
     * @param msg  the response message
     * @param data the data to include in the result
     * @param <T>  the type of the data
     * @return a Result instance
     */
    private static <T> Result<T> result(String code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setData(data);
        result.setMsg(msg);
        return result;
    }

    /**
     * Determines whether the given result represents a successful operation.
     *
     * @param result the result to check
     * @return true if the result is not null and has a success code, false
     *         otherwise
     */
    public static boolean isSuccess(Result<?> result) {
        return result != null && ResultCode.SUCCESS.getCode().equals(result.getCode());
    }
}


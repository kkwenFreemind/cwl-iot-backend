package community.waterlevel.iot.common.result;

/**
 * Interface for standardized response codes and messages.
 * Implemented by enums or classes that define API result codes and their descriptions.
 *
 * @author Ray.Hao
 * @since 1.0.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 **/
public interface IResultCode {

    String getCode();

    String getMsg();

}

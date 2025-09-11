package community.waterlevel.iot.auth.enums;

/**
 * Enumeration of supported EasyCaptcha verification code types.
 * Defines the available captcha styles for user authentication and bot prevention.
 *
 * @author haoxr
 * @since 2.5.1
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
public enum CaptchaTypeEnum {

    /**
     * Circle interference captcha
     */
    CIRCLE,
    /**
     * GIF captcha
     */
    GIF,
    /**
     * Line interference captcha
     */
    LINE,
    /**
     * Shear (distorted) interference captcha
     */
    SHEAR
}

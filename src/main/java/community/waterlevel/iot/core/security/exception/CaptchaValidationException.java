package community.waterlevel.iot.core.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception thrown when captcha validation fails during authentication.
 * Used to indicate invalid or expired captcha codes in security workflows.
 *
 * @author Ray.Hao
 * @since 2025/3/1
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public class CaptchaValidationException extends AuthenticationException {
    /**
     * Constructs a new {@code CaptchaValidationException} with the specified detail
     * message.
     *
     * @param msg the detail message describing the captcha validation failure
     */
    public CaptchaValidationException(String msg) {
        super(msg);
    }
}
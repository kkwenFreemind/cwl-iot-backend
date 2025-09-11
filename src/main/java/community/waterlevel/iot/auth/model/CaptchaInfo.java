package community.waterlevel.iot.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * Captcha information for verification process.
 * Contains the captcha key and the Base64-encoded image string.
 *
 * @author Ray。Hao
 * @since 2023/03/24
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Schema(description = "Verification code information")
@Data
@Builder
public class CaptchaInfo {

    /**
     * The cache key for the verification code.
     */
    @Schema(description = "Verification code cache Key")
    private String captchaKey;

    /**
     * The Base64-encoded string of the verification code image.
     */
    @Schema(description = "Verification code image Base64 string")
    private String captchaBase64;

}

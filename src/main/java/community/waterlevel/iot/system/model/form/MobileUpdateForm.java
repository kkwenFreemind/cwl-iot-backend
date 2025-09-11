package community.waterlevel.iot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Mobile phone update form object for user contact modification operations.
 * Handles secure mobile number changes with verification code validation
 * to ensure account security and prevent unauthorized contact information modifications.
 *
 * @author Ray.Hao
 * @since 2024/8/19
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "MobileUpdate Form")
@Data
public class MobileUpdateForm {
  /**
     * New mobile phone number for account update.
     * The target mobile number that will replace the current user's mobile contact.
     */
    @Schema(description = "mobile")
    @NotBlank(message = "mobile Not Blank")
    private String mobile;

    /**
     * Verification code for mobile change security.
     * Validation code sent to the new mobile number to confirm ownership.
     */
    @Schema(description = "Verification code")
    @NotBlank(message = "Verification code NotBlank")
    private String code;
}

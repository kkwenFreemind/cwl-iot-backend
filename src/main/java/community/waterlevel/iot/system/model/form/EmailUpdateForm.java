package community.waterlevel.iot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Email update form object for user email modification operations.
 * Handles secure email address changes with verification code validation
 * to ensure account security and prevent unauthorized email modifications.
 *
 * @author Ray.Hao
 * @since 2024/8/19
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "EmailUpdate Form")
@Data
public class EmailUpdateForm {

    /**
     * New email address for account update.
     * The target email address that will replace the current user's email.
     */
    @Schema(description = "email")
    @NotBlank(message = "NotBlank")
    private String email;

    /**
     * Verification code for email change security.
     * Validation code sent to the new email address to confirm ownership.
     */
    @Schema(description = "Verification code")
    @NotBlank(message = "NotBlank")
    private String code;

}

package community.waterlevel.iot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Password update form object for secure password change operations.
 * Handles user password modifications with current password verification
 * and confirmation validation to ensure account security and prevent
 * unauthorized changes.
 *
 * @author Ray.Hao
 * @since 2024/8/13
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "PasswordUpdate Form")
@Data
public class PasswordUpdateForm {

    @Schema(description = "old Password")
    private String oldPassword;

    @Schema(description = "new Password")
    private String newPassword;

    @Schema(description = "confirm Password")
    private String confirmPassword;
}

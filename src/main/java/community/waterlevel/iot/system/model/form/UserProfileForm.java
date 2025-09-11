package community.waterlevel.iot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Form object representing user profile information for personal center
 * features.
 * <p>
 * Encapsulates user ID, username, nickname, avatar URL, gender, mobile, and
 * email.
 * Used for data transfer between client and server when updating or displaying
 * user profile details.
 * </p>
 *
 * @author Ray.Hao
 * @since 2024/8/13
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "UserProfile Form")
@Data
public class UserProfileForm {
    /**
     * User primary key identifier for profile updates.
     * Used for identifying which user profile is being modified.
     */
    @Schema(description = "id")
    private Long id;

    /**
     * System login username for identification display.
     * Read-only field showing the user's login identifier (typically non-editable).
     */
    @Schema(description = "username")
    private String username;

    /**
     * User display nickname for personalization.
     * Editable friendly name shown throughout the application interface.
     */
    @Schema(description = "nickname")
    private String nickname;

    /**
     * Profile avatar image URL for visual representation.
     * User's profile picture that can be updated through the personal center.
     */
    @Schema(description = "avatar")
    private String avatar;

    /**
     * User gender classification for profile completeness.
     * Personal demographic information for user profile customization.
     */
    @Schema(description = "gender")
    private Integer gender;

    /**
     * Primary contact mobile phone number.
     * Personal contact information that can be updated by the user.
     */
    @Schema(description = "mobile")
    private String mobile;

    /**
     * Primary email address for communication.
     * Personal email contact that can be modified through profile settings.
     */
    @Schema(description = "email")
    private String email;
}

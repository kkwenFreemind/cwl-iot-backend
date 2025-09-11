package community.waterlevel.iot.system.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * UserBO is a business object representing user data for business logic and
 * data transfer purposes.
 * <p>
 * This class encapsulates user attributes such as ID, username, nickname,
 * contact information, status, department, roles, and creation time.
 * Used for transferring user data between service and presentation layers in
 * the IoT backend.
 *
 * @author haoxr
 * @since 2022/6/10
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
public class UserBO {

    /**
     * Unique user identifier for database reference.
     * Primary key used for user identification and relationship mapping.
     */
    private Long id;

    /**
     * System login username for authentication.
     * Unique identifier used for user account access and login procedures.
     */
    private String username;

    /**
     * User display name or friendly nickname.
     * Human-readable name shown throughout the application interface.
     */
    private String nickname;

    /**
     * Primary contact mobile phone number.
     * Used for communication, notifications, and two-factor authentication.
     */
    private String mobile;

    /**
     * User gender classification for demographic data.
     * 1 = Male, 2 = Female for user profile and statistical purposes.
     */
    private Integer gender;

    /**
     * Profile avatar image URL for visual identification.
     * Link to user's profile picture for display in interfaces.
     */
    private String avatar;

    /**
     * Primary email address for communication and recovery.
     * Used for system notifications, account recovery, and official communications.
     */
    private String email;

    /**
     * Account activation status for access control.
     * 1 = Enabled (active account), 0 = Disabled (suspended account)
     */
    private Integer status;

    /**
     * Department name for organizational context.
     * Human-readable department affiliation for organizational structure display.
     */
    private String deptName;

    /**
     * Assigned role names for permission context.
     * Comma-separated list of role names for authorization and UI customization.
     */
    private String roleNames;

    /**
     * Account creation timestamp for audit tracking.
     * Records when the user account was initially created in the system.
     */
    private LocalDateTime createTime;
}

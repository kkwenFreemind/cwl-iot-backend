package community.waterlevel.iot.system.model.entity;

import community.waterlevel.iot.common.base.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * System user JPA entity for user account management and authentication.
 * Supports comprehensive user profile management with department association,
 * status control, and third-party integration capabilities.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Entity
@Table(name = "sys_user")
@SQLDelete(sql = "UPDATE sys_user SET is_deleted = 1 WHERE id = ?")
@SQLRestriction("is_deleted = 0")
@Getter
@Setter
@Comment("Uset Table")
public class UserJpa extends BaseJpaEntity {

    /**
     * Unique username for system login authentication.
     * Primary identifier for user account access.
     */
    @Column(name = "username", length = 64, unique = true)
    @Comment("username")
    private String username;

    /**
     * Display name or friendly nickname for user interface.
     * Human-readable name shown throughout the application.
     */
    @Column(name = "nickname", length = 64)
    @Comment("nickname")
    private String nickname;

    /**
     * User gender classification.
     * 1 = Male, 2 = Female, 0 = Prefer not to disclose
     */
    @Column(name = "gender")
    @Comment("gender")
    private Integer gender;

    /**
     * Encrypted password for authentication security.
     * Stored as hashed value using secure password encoding.
     */
    @Column(name = "password", length = 128)
    @Comment("password")
    private String password;

    /**
     * Department association for organizational structure.
     * Foreign key reference to user's assigned department.
     */
    @Column(name = "dept_id")
    @Comment("deptId")
    private Long deptId;

    /**
     * Profile avatar image URL or path.
     * User's profile picture for visual identification.
     */
    @Column(name = "avatar", length = 500)
    @Comment("avatar")
    private String avatar;

    /**
     * Primary contact mobile phone number.
     * Used for communication and two-factor authentication.
     */
    @Column(name = "mobile", length = 20)
    @Comment("mobile")
    private String mobile;

    /**
     * Account activation status control.
     * 1 = Active (user can login), 0 = Disabled (account suspended)
     */
    @Column(name = "status")
    @Comment("status")
    private Integer status = 1;

    /**
     * Primary email address for notifications and recovery.
     * Used for system communications and account recovery.
     */
    @Column(name = "email", length = 128)
    @Comment("email")
    private String email;

    /**
     * WeChat integration OpenID for third-party authentication.
     * Enables WeChat login and social media integration.
     */
    @Column(name = "openid", length = 128)
    @Comment("openid")
    private String openid;

}

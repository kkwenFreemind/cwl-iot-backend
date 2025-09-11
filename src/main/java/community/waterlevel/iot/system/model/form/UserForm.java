package community.waterlevel.iot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * User form object for comprehensive user account management.
 * Handles user creation and updates with validation support for profile
 * information,
 * organizational assignment, and role-based access control configuration.
 *
 * @author haoxr
 * @since 2022/4/12 11:04
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "User Form")
@Data
public class UserForm {

    @Schema(description = "User ID")
    private Long id;

    @Schema(description = "username")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Schema(description = "nickname")
    @NotBlank(message = "Nickname cannot be empty")
    private String nickname;

    @Schema(description = "mobile")
    private String mobile;

    @Schema(description = "gender")
    private Integer gender;

    @Schema(description = "ser Avatar")
    private String avatar;

    @Schema(description = "email")
    private String email;

    @Schema(description = "User status (1: normal; 0: disabled)")
    @Range(min = 0, max = 1, message = "Incorrect user status")
    private Integer status;

    @Schema(description = "deptId")
    private Long deptId;

    @Schema(description = "Role ID collection")
    @NotEmpty(message = "User role cannot be empty")
    private List<Long> roleIds;

    @Schema(description = "openId")
    private String openId;

}

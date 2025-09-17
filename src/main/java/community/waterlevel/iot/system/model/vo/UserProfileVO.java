package community.waterlevel.iot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * View object representing user profile information for personal center
 * features.
 * <p>
 * Encapsulates user ID, username, nickname, avatar, gender, mobile, email,
 * department, roles, and creation time.
 * Used to transfer user profile data from the backend to the client for display
 * and update operations.
 * </p>
 *
 * @author Ray
 * @since 2024/8/13
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "UserProfile VO")
@Data
public class UserProfileVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "username")
    private String username;

    @Schema(description = "nickname")
    private String nickname;

    @Schema(description = "avatar")
    private String avatar;

    @Schema(description = "gender")
    private Integer gender;

    @Schema(description = "mobile")
    private String mobile;

    @Schema(description = "email")
    private String email;

    @Schema(description = "deptName")
    private String deptName;

    @Schema(description = "deptId")
    private Long deptId;

    @Schema(description = "roleNames")
    private String roleNames;

    @Schema(description = "createTime")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

}

package community.waterlevel.iot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * View object representing a paginated user record for API responses.
 * <p>
 * Encapsulates user ID, username, nickname, mobile, gender, avatar, email,
 * status, department, roles, and creation time.
 * Used to transfer paginated user data from the backend to the client in user
 * management features.
 * </p>
 *
 * @author haoxr
 * @since 2022/1/15 9:41
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "UserPage VO")
@Data
public class UserPageVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "username")
    private String username;

    @Schema(description = "nickname")
    private String nickname;

    @Schema(description = "mobile")
    private String mobile;

    @Schema(description = "gender")
    private Integer gender;

    @Schema(description = "avatar")
    private String avatar;

    @Schema(description = "email")
    private String email;

    @Schema(description = "User status (1: enabled; 0: disabled)")
    private Integer status;

    @Schema(description = "deptName")
    private String deptName;

    @Schema(description = "Use commas (,) to separate multiple role names.")
    private String roleNames;

    @Schema(description = "createTime")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm")
    private LocalDateTime createTime;

}

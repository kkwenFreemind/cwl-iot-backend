package community.waterlevel.iot.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

/**
 * CurrentUserDTO is a data transfer object representing the currently logged-in
 * user.
 * <p>
 * This class encapsulates user identity, profile, roles, and permissions, and
 * is used for authentication and authorization context in the IoT backend.
 *
 * @author haoxr
 * @since 2022/1/14
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "The currently logged-in user object")
@Data
public class CurrentUserDTO {

    @Schema(description = "userId")
    private Long userId;

    @Schema(description = "username")
    private String username;

    @Schema(description = "nickname")
    private String nickname;

    @Schema(description = "avatar")
    private String avatar;

    @Schema(description = "User role encoding collection")
    private Set<String> roles;

    @Schema(description = "User permission identifier collection")
    private Set<String> perms;

}

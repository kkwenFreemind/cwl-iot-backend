package community.waterlevel.iot.core.security.model;

import lombok.Data;
import java.util.Set;

/**
 * Model representing user authentication credentials and profile information.
 * Includes user ID, username, password, status, department, roles, and data
 * access scope.
 * Used for authentication, authorization, and user context management in
 * security modules.
 *
 * @author Ray.Hao
 * @since 2022/10/22
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
public class UserAuthCredentials {

    /**
     * Unique identifier of the user.
     */
    private Long userId;

    /**
     * Username for authentication.
     */
    private String username;

    /**
     * User's display nickname.
     */
    private String nickname;

    /**
     * Department ID to which the user belongs.
     */
    private Long deptId;

    /**
     * User's password for authentication.
     */
    private String password;

    /**
     * Account status (1: enabled; 0: disabled).
     */
    private Integer status;

    /**
     * Set of roles assigned to the user.
     */
    private Set<String> roles;

    /**
     * Data access scope, used to control the level of data the user can access.
     *
     * @see DataScopeEnum
     */
    private Integer dataScope;

}

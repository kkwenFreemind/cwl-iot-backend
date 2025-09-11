package community.waterlevel.iot.core.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Model representing information about an online user.
 * Includes user ID, username, department ID, data access scope, and assigned
 * roles.
 * Used for session management, monitoring, and access control in security
 * modules.
 *
 * @author wangtao
 * @since 2025/2/27 10:31
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineUser {

    /**
     * Unique identifier of the user.
     */
    private Long userId;

    /**
     * Username of the online user.
     */
    private String username;

    /**
     * Department ID to which the user belongs.
     */
    private Long deptId;

    /**
     * Data access scope for the user.
     * <p>
     * Defines the range of data the user can access, such as all data,
     * department-only, or custom scope.
     * </p>
     */
    private Integer dataScope;

    /**
     * Set of role permissions assigned to the user.
     */
    private Set<String> roles;

}
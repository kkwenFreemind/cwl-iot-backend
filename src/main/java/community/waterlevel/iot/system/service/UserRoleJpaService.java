package community.waterlevel.iot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import community.waterlevel.iot.system.model.entity.UserRoleJpa;

import java.util.List;

/**
 * Service interface for managing user-role relationships and assignments.
 * <p>
 * Provides methods for assigning roles to users, checking user assignments, and
 * retrieving role and user IDs by association.
 * Used in user and role management features to support permission assignment
 * and relationship maintenance.
 * </p>
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface UserRoleJpaService extends IService<UserRoleJpa> {

    /**
     * Assigns a list of role IDs to a specific user.
     *
     * @param userId  the ID of the user
     * @param roleIds the list of role IDs to assign
     */
    void saveUserRoles(Long userId, List<Long> roleIds);

    /**
     * Checks if a role has any assigned users.
     *
     * @param roleId the ID of the role
     * @return true if the role has assigned users, false otherwise
     */
    boolean hasAssignedUsers(Long roleId);

    /**
     * Retrieves a list of role IDs assigned to a specific user.
     *
     * @param userId the ID of the user
     * @return a list of role IDs
     */
    List<Long> getRoleIdsByUserId(Long userId);

    /**
     * Retrieves a list of user IDs assigned to a specific role.
     *
     * @param roleId the ID of the role
     * @return a list of user IDs
     */
    List<Long> getUserIdsByRoleId(Long roleId);

    /**
     * Deletes all user-role assignments for a specific user.
     *
     * @param userId the ID of the user
     */
    void deleteByUserId(Long userId);

    /**
     * Deletes all user-role assignments for a specific role.
     *
     * @param roleId the ID of the role
     */
    void deleteByRoleId(Long roleId);
}

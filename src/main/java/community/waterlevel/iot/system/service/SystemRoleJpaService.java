package community.waterlevel.iot.system.service;

import community.waterlevel.iot.common.model.Option;

import java.util.List;
import java.util.Set;

/**
 * Unified service interface for managing role data and operations with support
 * for multiple persistence strategies.
 * <p>
 * Provides methods for listing role options, retrieving and assigning menu IDs
 * to roles, checking data scope, and refreshing role-permission caches.
 * Used in role management features to support CRUD, permission assignment, and
 * data access control.
 * </p>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface SystemRoleJpaService {

    /**
     * Retrieves a list of role options for selection components.
     *
     * @return a list of role options with IDs
     */
    List<Option<Long>> listRoleOptions();

    /**
     * Retrieves a list of menu IDs assigned to a specific role.
     *
     * @param roleId the ID of the role
     * @return a list of menu IDs
     */
    List<Long> getRoleMenuIds(Long roleId);

    /**
     * Assigns a list of menu IDs to a specific role.
     *
     * @param roleId  the ID of the role
     * @param menuIds the list of menu IDs to assign
     */
    void assignMenusToRole(Long roleId, List<Long> menuIds);

    /**
     * Retrieves the maximum data scope value among a set of roles.
     *
     * @param roles the set of role codes
     * @return the maximum data scope value
     */
    Integer getMaximumDataScope(Set<String> roles);

    /**
     * Refreshes the role-permission cache for a specific role code.
     *
     * @param roleCode the role code to refresh
     */
    void refreshRolePermsCache(String roleCode);

    /**
     * Refreshes the role-permission cache when a role code changes.
     *
     * @param oldRoleCode the old role code
     * @param newRoleCode the new role code
     */
    void refreshRolePermsCache(String oldRoleCode, String newRoleCode);
}

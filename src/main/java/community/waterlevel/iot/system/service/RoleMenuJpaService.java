package community.waterlevel.iot.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import community.waterlevel.iot.system.model.entity.RoleMenuJpa;

import java.util.List;
import java.util.Set;

/**
 * Service interface for managing role-menu relationships and permissions.
 * <p>
 * Provides methods for listing menu IDs by role, refreshing role-permission caches, and retrieving permissions by role codes.
 * Used in role and menu management features to support permission assignment and cache management.
 * </p>
 *
 * @author haoxr
 * @since 2.5.0
 * 
  * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface RoleMenuJpaService extends IService<RoleMenuJpa> {


    /**
     * Retrieves a list of menu IDs assigned to a specific role.
     *
     * @param roleId the ID of the role
     * @return a list of menu IDs
     */
    List<Long> listMenuIdsByRoleId(Long roleId);


    /**
     * Refreshes the role-permission cache for all roles.
     */
    void refreshRolePermsCache();

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


    /**
     * Retrieves a set of permissions assigned to a set of role codes.
     *
     * @param roles the set of role codes
     * @return a set of permission strings
     */
    Set<String> getRolePermsByRoleCodes(Set<String> roles);
}

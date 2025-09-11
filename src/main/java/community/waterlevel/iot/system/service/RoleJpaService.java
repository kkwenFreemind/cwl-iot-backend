package community.waterlevel.iot.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.system.model.form.RoleForm;
import community.waterlevel.iot.system.model.query.RolePageQuery;
import community.waterlevel.iot.system.model.vo.RolePageVO;
import community.waterlevel.iot.system.model.entity.RoleJpa;
import java.util.List;
import java.util.Set;

/**
 * Service interface for managing role data and operations.
 * <p>
 * Provides methods for paginated queries, listing, retrieving, saving,
 * updating, deleting, assigning menus, and checking data scope for roles.
 * Used in role management features to support CRUD, permission assignment, and
 * data access control.
 * </p>
 *
 * @author haoxr
 * @since 2022/6/3
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface RoleJpaService extends IService<RoleJpa> {

    /**
     * Retrieves a paginated list of role records based on query parameters.
     *
     * @param queryParams the parameters for pagination and filtering
     * @return a paginated result of role view objects
     */
    Page<RolePageVO> getRolePage(RolePageQuery queryParams);

    /**
     * Retrieves a list of role options for selection components.
     *
     * @return a list of role options with IDs
     */
    List<Option<Long>> listRoleOptions();

    /**
     * Saves a new role record.
     *
     * @param roleForm the form object containing role data to save
     * @return true if the save operation was successful, false otherwise
     */
    boolean saveRole(RoleForm roleForm);

    /**
     * Retrieves the form data for a specific role by its ID.
     *
     * @param roleId the ID of the role
     * @return the form object containing role data
     */
    RoleForm getRoleForm(Long roleId);

    /**
     * Updates the status of a role by its ID.
     *
     * @param roleId the ID of the role
     * @param status the new status value
     * @return true if the update operation was successful, false otherwise
     */
    boolean updateRoleStatus(Long roleId, Integer status);

    /**
     * Deletes one or more roles by their IDs.
     *
     * @param ids a comma-separated string of role IDs to delete
     */
    void deleteRoles(String ids);

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

}

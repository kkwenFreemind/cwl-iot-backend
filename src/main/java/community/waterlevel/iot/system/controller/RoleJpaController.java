package community.waterlevel.iot.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import community.waterlevel.iot.common.annotation.Log;
import community.waterlevel.iot.common.annotation.RepeatSubmit;
import community.waterlevel.iot.common.enums.LogModuleEnum;
import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.common.result.PageResult;
import community.waterlevel.iot.common.result.Result;
import community.waterlevel.iot.system.model.form.RoleForm;
import community.waterlevel.iot.system.model.query.RolePageQuery;
import community.waterlevel.iot.system.model.vo.RolePageVO;
import community.waterlevel.iot.system.service.RoleJpaService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

/**
 * RoleJpaController is a REST controller that provides endpoints for managing
 * user roles and their permissions.
 * <p>
 * It exposes APIs for listing, creating, updating, deleting, and retrieving
 * role data, as well as assigning menus and updating role status. The
 * controller delegates business logic to the {@link RoleJpaService} and returns
 * standardized API responses.
 * <p>
 * Designed for use in the IoT backend's administration interface to manage user
 * roles, access control, and permissions.
 *
 * @author Ray.Hao
 * @since 2022/10/16
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Tag(name = "03.Role Controller")
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleJpaController {

    private final RoleJpaService roleService;

    /**
     * Retrieves a paginated list of roles based on query parameters.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return PageResult containing role page data
     */
    @Operation(summary = "Retrieves a paginated list of roles based on query parameters.")
    @GetMapping("/page")
    @Log(value = "Retrieves a paginated list of roles based on query parameters.", module = LogModuleEnum.ROLE)
    public PageResult<RolePageVO> getRolePage(
            RolePageQuery queryParams) {
        Page<RolePageVO> result = roleService.getRolePage(queryParams);
        return PageResult.success(result);
    }

    /**
     * Retrieves a list of role options for dropdown selection.
     *
     * @return Result containing a list of role options
     */
    @Operation(summary = "Retrieves a list of role options for dropdown selection.")
    @GetMapping("/options")
    public Result<List<Option<Long>>> listRoleOptions() {
        List<Option<Long>> list = roleService.listRoleOptions();
        return Result.success(list);
    }

    /**
     * Creates a new role in the system.
     *
     * @param roleForm the role form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Creates a new role in the system.")
    @PostMapping
    @RepeatSubmit
    public Result<?> addRole(@Valid @RequestBody RoleForm roleForm) {
        boolean result = roleService.saveRole(roleForm);
        return Result.judge(result);
    }

    /**
     * Retrieves the form data for a specific role by role ID.
     *
     * @param roleId the role ID
     * @return Result containing the role form data
     */
    @Operation(summary = "Retrieves the form data for a specific role by role ID.")
    @GetMapping("/{roleId}/form")
    public Result<RoleForm> getRoleForm(
            @Parameter(description = "roleId") @PathVariable Long roleId) {
        RoleForm roleForm = roleService.getRoleForm(roleId);
        return Result.success(roleForm);
    }

    /**
     * Updates an existing role by role ID.
     *
     * @param roleForm the role form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Updates an existing role by role ID.")
    @PutMapping(value = "/{id}")
    public Result<?> updateRole(@Valid @RequestBody RoleForm roleForm) {
        boolean result = roleService.saveRole(roleForm);
        return Result.judge(result);
    }

    /**
     * Deletes one or more roles by IDs.
     *
     * @param ids the role IDs, separated by commas
     * @return Result indicating success
     */
    @Operation(summary = "Deletes one or more roles by IDs.")
    @DeleteMapping("/{ids}")
    public Result<Void> deleteRoles(
            @Parameter(description = "the role IDs, separated by commas") @PathVariable String ids) {
        roleService.deleteRoles(ids);
        return Result.success();
    }

    /**
     * Updates the status of a role (enable/disable).
     *
     * @param roleId the role ID
     * @param status the role status (1: enabled, 0: disabled)
     * @return Result indicating success or failure
     */
    @Operation(summary = "Updates the status of a role (enable/disable)")
    @PutMapping(value = "/{roleId}/status")
    public Result<?> updateRoleStatus(
            @Parameter(description = "roleId") @PathVariable Long roleId,
            @Parameter(description = "Status (1: enabled; 0: disabled)") @RequestParam Integer status) {
        boolean result = roleService.updateRoleStatus(roleId, status);
        return Result.judge(result);
    }

    /**
     * Retrieves the list of menu IDs assigned to a specific role.
     *
     * @param roleId the role ID
     * @return Result containing the list of menu IDs
     */
    @Operation(summary = "Retrieves the list of menu IDs assigned to a specific role.")
    @GetMapping("/{roleId}/menuIds")
    public Result<List<Long>> getRoleMenuIds(
            @Parameter(description = "roleId") @PathVariable Long roleId) {
        List<Long> menuIds = roleService.getRoleMenuIds(roleId);
        return Result.success(menuIds);
    }

    /**
     * Assigns a list of menu IDs to a specific role.
     *
     * @param roleId  the role ID
     * @param menuIds the list of menu IDs to assign
     * @return Result indicating success
     */
    @Operation(summary = "Assigns a list of menu IDs to a specific role.")
    @PutMapping("/{roleId}/menus")
    public Result<Void> assignMenusToRole(
            @PathVariable Long roleId,
            @RequestBody List<Long> menuIds) {
        roleService.assignMenusToRole(roleId, menuIds);
        return Result.success();
    }
}

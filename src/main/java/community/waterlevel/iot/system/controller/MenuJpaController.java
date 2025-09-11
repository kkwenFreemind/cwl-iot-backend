package community.waterlevel.iot.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import community.waterlevel.iot.common.annotation.Log;
import community.waterlevel.iot.common.annotation.RepeatSubmit;
import community.waterlevel.iot.common.enums.LogModuleEnum;
import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.common.result.Result;
import community.waterlevel.iot.system.model.form.MenuForm;
import community.waterlevel.iot.system.model.query.MenuQuery;
import community.waterlevel.iot.system.model.vo.MenuVO;
import community.waterlevel.iot.system.model.vo.RouteVO;
import community.waterlevel.iot.system.service.MenuJpaService;
import community.waterlevel.iot.system.service.SystemMenuJpaService;
import java.util.List;


/**
 * MenuController is a REST controller that provides endpoints for managing
 * application menus and navigation structures.
 * <p>
 * It exposes APIs for listing, creating, updating, deleting, and retrieving
 * menu data, including support for dropdown options, route lists, and
 * visibility toggling. The controller delegates business logic to the
 * {@link MenuJpaService} and returns standardized API responses.
 * <p>
 * Designed for use in the IoT backend's administration interface to manage
 * user-facing navigation and permissions.
 *
 * @author Ray.Hao
 * @since 2020/11/06
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Tag(name = "04.Menu Controller")
@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
@Slf4j
public class MenuJpaController {

    private final SystemMenuJpaService systemMenuService;

    /**
     * Retrieves a list of system menus based on query parameters.
     *
     * @param queryParams the menu query parameters
     * @return Result containing the list of menu value objects
     */
    @Operation(summary = "Retrieves a list of system menus based on query parameters")
    @GetMapping
    public Result<List<MenuVO>> listMenus(MenuQuery queryParams) {
        List<MenuVO> menuList = systemMenuService.listMenus(queryParams);
        return Result.success(menuList);
    }

    /**
     * Retrieves menu dropdown options, optionally filtering for parent menus only.
     *
     * @param onlyParent whether to query only parent menus
     * @return Result containing the list of menu options
     */
    @Operation(summary = "Retrieves menu dropdown options, optionally filtering for parent menus only.")
    @GetMapping("/options")
    public Result<List<Option<Long>>> listMenuOptions(
            @Parameter(description = "Whether to query only the parent menu") @RequestParam(defaultValue = "false") boolean onlyParent
    ) {
        List<Option<Long>> menuOptions = systemMenuService.listMenuOptions(onlyParent);
        return Result.success(menuOptions);
    }

    /**
     * Retrieves the list of routes available to the current user.
     *
     * @return Result containing the list of route value objects
     */
    @Operation(summary = "Retrieves the list of routes available to the current user.")
    @GetMapping("/routes")
    public Result<List<RouteVO>> getCurrentUserRoutes() {
        List<RouteVO> routeList = systemMenuService.getCurrentUserRoutes();
        return Result.success(routeList);
    }

    /**
     * Retrieves the form data for a specific menu by ID.
     *
     * @param id the menu ID
     * @return Result containing the menu form data
     */
    @Operation(summary = "Retrieves the form data for a specific menu by ID.")
    @GetMapping("/{id}/form")
    public Result<MenuForm> getMenuForm(
            @Parameter(description = "菜單ID") @PathVariable Long id
    ) {
        MenuForm menu = systemMenuService.getMenuForm(id);
        return Result.success(menu);
    }

    /**
     * Creates a new menu.
     *
     * @param menuForm the menu form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Creates a new menu.")
    @PostMapping
    @RepeatSubmit
    @Log(value = "Creates a new menu.", module = LogModuleEnum.MENU)
    public Result<?> addMenu(@RequestBody MenuForm menuForm) {
        boolean result = systemMenuService.saveMenu(menuForm);
        return Result.judge(result);
    }

    /**
     * Updates an existing menu by ID.
     *
     * @param id the menu ID
     * @param menuForm the menu form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Updates an existing menu by ID.")
    @PutMapping(value = "/{id}")
    @Log(value = " Updates an existing menu by ID.", module = LogModuleEnum.MENU)
    public Result<?> updateMenu(
            @Parameter(description = "menuId") @PathVariable Long id,
            @RequestBody MenuForm menuForm
    ) {
        menuForm.setId(id);
        boolean result = systemMenuService.saveMenu(menuForm);
        return Result.judge(result);
    }

    /**
     * Deletes a menu by ID.
     *
     * @param id the menu ID
     * @return Result indicating success or failure
     */
    @Operation(summary = "Deletes a menu by ID.")
    @DeleteMapping("/{id}")
    @Log(value = "Deletes a menu by ID.", module = LogModuleEnum.MENU)
    public Result<?> deleteMenu(
            @Parameter(description = "menuId") @PathVariable Long id
    ) {
        boolean result = systemMenuService.deleteMenu(id);
        return Result.judge(result);
    }

    /**
     * Updates the visibility status of a menu.
     *
     * @param menuId the menu ID
     * @param visible the visibility status (1: visible, 0: hidden)
     * @return Result indicating success or failure
     */
    @Operation(summary = "Updates the visibility status of a menu.")
    @PatchMapping("/{menuId}")
    @Log(value = "Updates the visibility status of a menu.", module = LogModuleEnum.MENU)
    public Result<?> updateMenuVisible(
            @Parameter(description = "menuId") @PathVariable Long menuId,
            @Parameter(description = "Display status (1: display 0: hide)") @RequestParam Integer visible
    ) {
        boolean result = systemMenuService.updateMenuVisible(menuId, visible);
        return Result.judge(result);
    }

}

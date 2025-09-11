package community.waterlevel.iot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import community.waterlevel.iot.common.model.KeyValue;

import java.util.List;

/**
 * Menu form object for navigation structure management.
 * Handles menu item creation and updates with comprehensive routing configuration,
 * permission control, and Vue.js frontend integration support for dynamic menu systems.
 *
 * @author Ray.Hao
 * @since 2024/06/23
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */

@Schema(description = "MenuForm")
@Data
public class MenuForm {

    /**
     * Menu item primary key identifier for updates.
     * Used for modifying existing menu items, null for new menu creation.
     */
    @Schema(description = "id")
    private Long id;

    /**
     * Parent menu identifier for hierarchical navigation structure.
     * Links this menu item to its parent in the navigation tree hierarchy.
     */
    @Schema(description = "parentId")
    private Long parentId;

    /**
     * Menu display name for user interface navigation.
     * Human-readable name shown in navigation menus and breadcrumbs.
     */
    @Schema(description = "name")
    private String name;

    /**
     * Menu type classification for different navigation behaviors.
     * 1 = Menu (navigable), 2 = Directory (grouping), 3 = External link, 4 = Button (action)
     */
    @Schema(description = "type")
    private Integer type;

    /**
     * Vue.js route name for frontend routing identification.
     * Unique identifier used by Vue Router for programmatic navigation.
     */
    @Schema(description = "routeName")
    private String routeName;

    /**
     * URL path for route matching and navigation.
     * The path pattern used by Vue Router for route matching and browser URL.
     */
    @Schema(description = "routePath")
    private String routePath;

    /**
     * Vue component file path for dynamic component loading.
     * Relative path to the Vue component file (without .vue extension).
     */
    @Schema(description = "component")
    private String component;

    /**
     * Permission identifier for access control validation.
     * Security token required to access this menu item and its associated functionality.
     */
    @Schema(description = "perm")
    private String perm;

    /**
     * Menu visibility control for dynamic menu rendering.
     * 1 = Visible (shown in menu), 0 = Hidden (accessible but not displayed)
     */
    @Schema(description = "Display status (1: display; 0: hide)")
    @Range(max = 1, min = 0, message = "Incorrect display status")
    private Integer visible;

    /**
     * Display order for menu item positioning.
     * Lower values appear first in navigation menus and menu lists.
     */
    @Schema(description = "sort")
    private Integer sort;

    /**
     * Icon identifier for visual menu representation.
     * Icon name or class used for displaying menu item icons in the interface.
     */
    @Schema(description = "icon")
    private String icon;

    /**
     * Default redirect path for menu navigation control.
     * Target path for automatic redirection when accessing this menu item.
     */
    @Schema(description = "redirect")
    private String redirect;

    /**
     * Page caching control for performance optimization.
     * 1 = Enable caching (keep component alive), 0 = Disable caching
     */
    @Schema(description = "keepAlive", example = "1")
    private Integer keepAlive;

    /**
     * Single child directory display control for navigation UX.
     * 1 = Always show directory even with single child, 0 = Auto-collapse single child
     */
    @Schema(description = "alwaysShow", example = "1")
    private Integer alwaysShow;

    /**
     * Route parameter collection for dynamic routing.
     * Key-value pairs passed as parameters to the route for dynamic content loading.
     */
    @Schema(description = "params")
    private List<KeyValue> params;

}

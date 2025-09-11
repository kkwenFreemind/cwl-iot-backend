package community.waterlevel.iot.system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * System menu JPA entity class for managing hierarchical menu structure and
 * permissions.
 * Supports Vue.js router integration with route definitions, component mapping,
 * and permission control.
 * Designed for building dynamic navigation menus and role-based access control
 * systems.
 *
 * @author Ray.Hao
 * @since 2023/3/6
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Entity
@Table(name = "sys_menu")
@Getter
@Setter
public class MenuJpa {

    /**
     * Primary key for the menu item.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Parent menu ID for hierarchical structure.
     * References the ID of the parent menu in the tree structure.
     */
    @Column(name = "parent_id", nullable = false)
    private Long parentId;

    /**
     * Tree path containing all parent IDs separated by commas.
     * Used for efficient tree queries and maintaining hierarchy relationships.
     */
    @Column(name = "tree_path")
    private String treePath;

    /**
     * Display name of the menu item.
     * Human-readable name shown in the navigation interface.
     */
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    /**
     * Menu type classification.
     * 1 = Menu, 2 = Directory, 3 = External Link, 4 = Button Permission
     */
    @Column(name = "type", nullable = false)
    private Integer type;

    /**
     * Vue Router route name.
     * The name property defined in Vue Router configuration for programmatic
     * navigation.
     */
    @Column(name = "route_name")
    private String routeName;

    /**
     * Vue Router route path.
     * The URL path defined in Vue Router for accessing the component.
     */
    @Column(name = "route_path", length = 128)
    private String routePath;

    /**
     * Vue component path for rendering.
     * Full path to the Vue component file (excluding .vue extension).
     */
    @Column(name = "component", length = 128)
    private String component;

    /**
     * Permission identifier for access control.
     * Used by security framework to determine user access rights.
     */
    @Column(name = "perm", length = 128)
    private String perm;

    /**
     * Directory display behavior when having single child route.
     * 1 = Always show directory, 0 = Hide directory if only one child. Default is
     * 0.
     */
    @Column(name = "always_show")
    private Integer alwaysShow = 0;

    /**
     * Page caching configuration for menu routes.
     * 1 = Enable keep-alive caching, 0 = Disable caching. Default is 0.
     */
    @Column(name = "keep_alive")
    private Integer keepAlive = 0;

    /**
     * Visibility status of the menu item.
     * 1 = Visible in navigation, 0 = Hidden from navigation. Default is 1.
     */
    @Column(name = "visible")
    private Integer visible = 1;

    /**
     * Display sort order within the same level.
     * Lower values appear first in the navigation menu. Default is 0.
     */
    @Column(name = "sort")
    private Integer sort = 0;

    /**
     * Icon identifier for the menu item.
     * Used to display appropriate icon in the navigation interface.
     */
    @Column(name = "icon", length = 64)
    private String icon;

    /**
     * Redirect path for route navigation.
     * Specifies where to redirect when this menu item is accessed.
     */
    @Column(name = "redirect", length = 128)
    private String redirect;

    /**
     * Timestamp when the menu item was created.
     * Used for audit trail and data management purposes.
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * Timestamp when the menu item was last updated.
     * Used for tracking modifications and version control.
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * Route parameters for dynamic routing.
     * Additional parameters passed to the Vue Router for component initialization.
     */
    @Column(name = "params")
    private String params;

    // Note: Intentionally avoiding ManyToMany relationship with RoleJpa to prevent
    // circular dependencies
    // Role-menu associations are handled through Repository query methods using
    // sys_role_menu junction table
}

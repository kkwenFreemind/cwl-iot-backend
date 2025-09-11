package community.waterlevel.iot.system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Role-menu association JPA entity for managing role-based menu permissions.
 * Maps the many-to-many relationship between roles and menu items using
 * composite keys
 * to enable flexible permission assignment and menu access control.
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Entity
@Table(name = "sys_role_menu")
@Getter
@Setter
public class RoleMenuJpa {

    /**
     * Composite primary key combining role and menu identifiers.
     * Ensures unique role-menu associations in the permission mapping table.
     */
    @EmbeddedId
    private RoleMenuId id;

    /**
     * Role identifier for query optimization.
     * Read-only field extracted from composite key for convenient querying.
     */
    @Column(name = "role_id", insertable = false, updatable = false)
    private Long roleId;

    /**
     * Menu identifier for query optimization.
     * Read-only field extracted from composite key for convenient querying.
     */
    @Column(name = "menu_id", insertable = false, updatable = false)
    private Long menuId;

    /**
     * Default constructor for JPA entity initialization.
     * Creates a new instance with an empty composite key.
     */
    public RoleMenuJpa() {
        this.id = new RoleMenuId();
    }

    /**
     * Parameterized constructor for creating role-menu associations.
     * Initializes the entity with role and menu identifiers for permission mapping.
     *
     * @param roleId the role identifier to associate with the menu
     * @param menuId the menu identifier to associate with the role
     */
    public RoleMenuJpa(Long roleId, Long menuId) {
        this.id = new RoleMenuId(roleId, menuId);
        this.roleId = roleId;
        this.menuId = menuId;
    }
}

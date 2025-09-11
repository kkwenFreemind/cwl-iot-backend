package community.waterlevel.iot.system.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for role-menu association mapping.
 * Implements the many-to-many relationship between roles and menu permissions
 * using JPA embedded ID pattern for optimal database performance.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RoleMenuId implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Foreign key reference to the role entity.
     * Links to sys_role.id for role identification.
     */
    @Column(name = "role_id")
    private Long roleId;

    /**
     * Foreign key reference to the menu entity.
     * Links to sys_menu.id for menu permission identification.
     */
    @Column(name = "menu_id")
    private Long menuId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RoleMenuId that = (RoleMenuId) o;
        return Objects.equals(roleId, that.roleId) && Objects.equals(menuId, that.menuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, menuId);
    }
}

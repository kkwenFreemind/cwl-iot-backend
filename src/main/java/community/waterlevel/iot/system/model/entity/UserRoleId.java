package community.waterlevel.iot.system.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for user-role association mapping.
 * Implements the many-to-many relationship between users and roles
 * using JPA embedded ID pattern for efficient role-based access control.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserRoleId implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Foreign key reference to the user entity.
     * Links to sys_user.id for user identification in role assignment.
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Foreign key reference to the role entity.
     * Links to sys_role.id for role identification in user permissions.
     */
    @Column(name = "role_id")
    private Long roleId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleId that = (UserRoleId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }
}

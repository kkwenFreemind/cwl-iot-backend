package community.waterlevel.iot.system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * User-role association JPA entity for role-based access control management.
 * Maps the many-to-many relationship between users and roles using composite
 * keys
 * to enable flexible permission assignment and authorization control.
 * 
 * @author Rya.Hao
 * @since 2022/12/17
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Entity
@Table(name = "sys_user_role")
@Getter
@Setter
public class UserRoleJpa {

    /**
     * Composite primary key combining user and role identifiers.
     * Ensures unique user-role associations in the mapping table.
     */
    @EmbeddedId
    private UserRoleId id;

    /**
     * User identifier for query optimization.
     * Read-only field extracted from composite key for convenient querying.
     */
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    /**
     * Role identifier for query optimization.
     * Read-only field extracted from composite key for convenient querying.
     */
    @Column(name = "role_id", insertable = false, updatable = false)
    private Long roleId;

    public UserRoleJpa() {
        this.id = new UserRoleId();
    }

    public UserRoleJpa(Long userId, Long roleId) {
        this.id = new UserRoleId(userId, roleId);
        this.userId = userId;
        this.roleId = roleId;
    }
}


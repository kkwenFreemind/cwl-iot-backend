package community.waterlevel.iot.system.model.entity;

import community.waterlevel.iot.common.base.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * System role JPA entity for managing user access control and permissions.
 * Supports role-based authorization with hierarchical data permission scoping.
 * Includes soft delete functionality for audit trail preservation.
 *
 * @author Ray.Hao
 * @since 2024/6/23
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Entity
@Table(name = "sys_role")
@Getter
@Setter
@SQLDelete(sql = "UPDATE sys_role SET is_deleted = 1 WHERE id = ?")
@SQLRestriction("is_deleted = 0")
public class RoleJpa extends BaseJpaEntity {

    /**
     * Role display name for user interface presentation.
     * Human-readable name shown in role management screens.
     */
    @Column(name = "name", nullable = false, length = 64, unique = true)
    private String name;

    /**
     * Unique role code for system identification.
     * Used programmatically for role-based access control logic.
     */
    @Column(name = "code", nullable = false, length = 32, unique = true)
    private String code;

    /**
     * Display order for role listing.
     * Lower values appear first in role selection interfaces.
     */
    @Column(name = "sort")
    private Integer sort;

    /**
     * Role activation status.
     * 1 = Active (role can be assigned), 0 = Disabled (role inactive)
     */
    @Column(name = "status")
    private Integer status = 1;

    /**
     * Data permission scope level for access control.
     * Defines the breadth of data access this role provides to users.
     */
    @Column(name = "data_scope")
    private Integer dataScope;
    
}

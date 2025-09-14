package community.waterlevel.iot.system.model.entity;

import community.waterlevel.iot.common.annotation.DataPermission;
import community.waterlevel.iot.common.base.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Department JPA entity class for managing organizational department structure.
 * Supports hierarchical tree structure with parent-child relationships and soft
 * delete functionality.
 *
 * @author Ray.Hao
 * @since 2024/06/23
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Getter
@Setter
@Entity
@Table(name = "sys_dept")
@SQLDelete(sql = "UPDATE sys_dept SET is_deleted = 1 WHERE id = ?")
@SQLRestriction("is_deleted = 0")
@DataPermission(deptAlias = "d")
public class DeptJpa extends BaseJpaEntity {

    /**
     * Department name for display purposes.
     * The human-readable name of the department.
     */
    @Column(name = "name")
    private String name;

    /**
     * Department code for identification.
     * Unique identifier code used for system integration and reporting.
     */
    @Column(name = "code")
    private String code;

    /**
     * Parent department ID for hierarchical structure.
     * References the ID of the parent department in the tree structure.
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * Tree path containing all parent IDs.
     * Comma-separated path from root to current node for efficient tree queries.
     */
    @Column(name = "tree_path")
    private String treePath;

    /**
     * Display sort order within the same level.
     * Lower values appear first in the display order.
     */
    @Column(name = "sort")
    private Integer sort;

    /**
     * Department status indicator.
     * 1 = Normal/Active, 0 = Disabled/Inactive
     */
    @Column(name = "status")
    private Integer status;

    /**
     * Center latitude coordinate for geospatial positioning.
     * Used for frontend display and simple location services.
     */
    @Column(name = "center_latitude")
    private Double centerLatitude;

    /**
     * Center longitude coordinate for geospatial positioning.
     * Used for frontend display and simple location services.
     */
    @Column(name = "center_longitude")
    private Double centerLongitude;

    /**
     * PostGIS spatial geometry column for spatial operations.
     * Automatically synchronized from latitude/longitude coordinates by database trigger.
     * Used for spatial queries and indexing.
     * This field is read-only and managed by the database.
     */
    @Column(name = "center_geom", columnDefinition = "GEOGRAPHY(Point, 4326)", insertable = false, updatable = false)
    private String centerGeom;
}

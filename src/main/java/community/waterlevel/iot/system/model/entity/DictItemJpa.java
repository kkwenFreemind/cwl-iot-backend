package community.waterlevel.iot.system.model.entity;

import community.waterlevel.iot.common.annotation.DataPermission;
import community.waterlevel.iot.common.base.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Dictionary item JPA entity class for managing system dictionary data items.
 * Each item represents a selectable option within a dictionary category with
 * label-value pairs.
 * Supports data permission control and soft delete functionality.
 *
 * @author Ray.Hao
 * @since 2022/12/17
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Entity
@Table(name = "sys_dict_item")
@SQLDelete(sql = "UPDATE sys_dict_item SET is_deleted = 1 WHERE id = ?")
@SQLRestriction("is_deleted = 0")
@Data
@EqualsAndHashCode(callSuper = true)
@DataPermission(deptAlias = "d", userAlias = "u")
public class DictItemJpa extends BaseJpaEntity {

    /**
     * Dictionary code that this item belongs to.
     * Links this item to a specific dictionary category for grouping related
     * options.
     */
    @Column(name = "dict_code", length = 100, nullable = false)
    private String dictCode;

    /**
     * Display label for the dictionary item.
     * Human-readable text shown to users in the interface.
     */
    @Column(name = "label", length = 200, nullable = false)
    private String label;

    /**
     * Actual value of the dictionary item.
     * The programmatic value used by the system when this option is selected.
     */
    @Column(name = "value", length = 200, nullable = false)
    private String value;

    /**
     * Sort order for displaying dictionary items.
     * Lower values appear first in the list. Default value is 0.
     */
    @Column(name = "sort")
    private Integer sort = 0;

    /**
     * Status of the dictionary item.
     * 1 = Normal/Active, 0 = Disabled/Inactive. Default value is 1.
     */
    @Column(name = "status")
    private Integer status = 1;

    /**
     * Optional remarks or description for the dictionary item.
     * Additional information about the item's purpose or usage.
     */
    @Column(name = "remark", length = 500)
    private String remark;

    /**
     * Tag type for UI styling purposes.
     * Used to determine the visual appearance of the item in the interface.
     */
    @Column(name = "tag_type", length = 50)
    private String tagType;

    /**
     * Logical delete flag for soft delete functionality.
     * 0 = Not deleted, 1 = Deleted. Default value is 0.
     */
    @Column(name = "is_deleted")
    private Integer isDeleted = 0;
}

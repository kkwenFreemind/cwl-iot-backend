package community.waterlevel.iot.system.model.entity;

import community.waterlevel.iot.common.annotation.DataPermission;
import community.waterlevel.iot.common.base.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Dictionary JPA entity class for managing system dictionary categories.
 * Represents dictionary definitions that group related dictionary items
 * together.
 * Supports data permission control and soft delete functionality.
 * 
 *
 * @author Ray.Hao
 * @since 2022/12/17
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Entity
@Table(name = "sys_dict")
@SQLDelete(sql = "UPDATE sys_dict SET is_deleted = 1 WHERE id = ?")
@SQLRestriction("is_deleted = 0")
@Data
@EqualsAndHashCode(callSuper = true)
@DataPermission(deptAlias = "d", userAlias = "u")
public class DictJpa extends BaseJpaEntity {

    /**
     * Unique dictionary code for identification.
     * Used as a key to group related dictionary items and for programmatic access.
     */
    @Column(name = "dict_code", length = 100, nullable = false)
    private String dictCode;

    /**
     * Display name of the dictionary category.
     * Human-readable name shown to users for identifying the dictionary purpose.
     */
    @Column(name = "name", length = 200, nullable = false)
    private String name;

    /**
     * Status of the dictionary category.
     * 1 = Enabled/Active, 0 = Disabled/Inactive. Default value is 1.
     */
    @Column(name = "status")
    private Integer status = 1;

    /**
     * Optional remarks or description for the dictionary.
     * Additional information about the dictionary's purpose or usage guidelines.
     */
    @Column(name = "remark", length = 500)
    private String remark;

    /**
     * Logical delete flag for soft delete functionality.
     * 0 = Not deleted, 1 = Deleted. Default value is 0.
     */
    @Column(name = "is_deleted")
    private Integer isDeleted = 0;
}

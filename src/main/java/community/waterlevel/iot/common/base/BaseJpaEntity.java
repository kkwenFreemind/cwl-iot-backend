package community.waterlevel.iot.common.base;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Abstract base class for JPA entities, providing common fields and auditing support.
 * Includes primary key, creation and update timestamps, creator/updater IDs, and logical deletion flag.
 * Intended to be extended by all entity classes to ensure consistency and reduce boilerplate.
 *
 * @author Ray
 * @since 2024/6/23
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class BaseJpaEntity implements Serializable {

    /**
     * Serialization version UID for Serializable interface.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Primary key ID (auto-increment).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Entity creation timestamp. Automatically set on insert.
     * Format: yyyy-MM-dd HH:mm:ss
     * Null values are excluded from JSON output.
     */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * Entity update timestamp. Automatically set on update.
     * Format: yyyy-MM-dd HH:mm:ss
     * Null values are excluded from JSON output.
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * ID of the user who created the entity.
     */
    @Column(name = "create_by")
    private Long createBy;

    /**
     * ID of the user who last updated the entity.
     */
    @Column(name = "update_by")
    private Long updateBy;

    /**
     * Logical deletion flag (0 = not deleted, 1 = deleted).
     */
    @Column(name = "is_deleted", nullable = false)
    private Integer isDeleted = 0;

}

package community.waterlevel.iot.system.model.entity;

import community.waterlevel.iot.common.base.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * ConfigJpa is a JPA entity representing a system configuration setting.
 * <p>
 * It maps to the <code>sys_config</code> table and stores configuration name,
 * key, value, and optional remarks.
 * This entity supports soft deletion and is used for dynamic application
 * configuration management in the IoT backend.
 *
 * @author Theo
 * @since 2024-07-29 11:17:26
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Entity
@Table(name = "sys_config")
@SQLDelete(sql = "UPDATE sys_config SET is_deleted = 1 WHERE id = ?")
@SQLRestriction("is_deleted = 0")
@Getter
@Setter
@Comment("Config Table")
public class ConfigJpa extends BaseJpaEntity {

    /**
     * Configuration name for display purposes.
     * Used to identify the configuration setting in the UI.
     */
    @Column(name = "config_name", length = 100)
    @Comment("Configuration name")
    private String configName;

    /**
     * Unique configuration key for programmatic access.
     * Used as the identifier when retrieving configuration values in code.
     */
    @Column(name = "config_key", length = 100, unique = true)
    @Comment("Unique configuration key")
    private String configKey;

    /**
     * Configuration value stored as string.
     * Can contain various data types serialized as string format.
     */
    @Column(name = "config_value", length = 500)
    @Comment("Configuration value")
    private String configValue;

    /**
     * Optional description or remarks about the configuration.
     * Used to provide additional context about the configuration's purpose.
     */
    @Column(name = "remark", length = 500)
    @Comment("Optional description or remarks")
    private String remark;

}

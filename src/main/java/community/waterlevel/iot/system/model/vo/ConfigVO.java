package community.waterlevel.iot.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * View object representing system configuration data for API responses.
 * <p>
 * Encapsulates configuration name, key, value, and remarks. Used to transfer configuration data
 * from the backend to the client in system configuration management features.
 * </p>
 *
 * @author Theo
 * @since 2024-07-30 14:49
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Config VO")
public class ConfigVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "configName")
    private String configName;

    @Schema(description = "configKey")
    private String configKey;

    @Schema(description = "configValue")
    private String configValue;

    @Schema(description = "remark")
    private String remark;
}

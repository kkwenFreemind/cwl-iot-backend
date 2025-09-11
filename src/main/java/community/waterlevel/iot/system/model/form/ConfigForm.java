package community.waterlevel.iot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Form object representing system configuration data for create and update
 * operations.
 * <p>
 * Encapsulates configuration name, key, value, and optional remarks. Used for
 * data transfer
 * between client and server in system configuration management features.
 * </p>
 *
 * @author Theo
 * @since 2024-07-29 11:17:26
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
@Schema(description = "System Configuration Form Entity")
public class ConfigForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @NotBlank(message = "NotBlank")
    @Schema(description = "configName")
    private String configName;

    @NotBlank(message = "NotBlank")
    @Schema(description = "configKey")
    private String configKey;

    @NotBlank(message = "NotBlank")
    @Schema(description = "configValue")
    private String configValue;

    @Schema(description = "remark")
    private String remark;
}

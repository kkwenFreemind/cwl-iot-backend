package community.waterlevel.iot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * Form object representing dictionary data for create and update operations.
 * <p>
 * Encapsulates dictionary name, code, status, and remarks. Used for data
 * transfer
 * between client and server in dictionary management features.
 * </p>
 *
 * @author Ray Hao
 * @since 2.9.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Schema(description = "Dict Form")
@Data
public class DictForm {

    /**
     * Dictionary primary key identifier for updates.
     * Used for modifying existing dictionary categories, null for new creation.
     */
    @Schema(description = "id", example = "1")
    private Long id;

    /**
     * Human-readable dictionary category name.
     * Descriptive name displayed in administrative interfaces and documentation.
     */
    @Schema(description = "name")
    private String name;

    /**
     * Unique dictionary code for programmatic access.
     * System identifier used to retrieve dictionary items and validate data.
     */
    @Schema(description = "dictCode")
    @NotBlank(message = "NotBlank")
    private String dictCode;

    /**
     * Additional description or usage notes for the dictionary category.
     * Optional field providing context about the dictionary's purpose and usage.
     */
    @Schema(description = "remark")
    private String remark;

    /**
     * Dictionary activation status for operational control.
     * 1 = Enabled (dictionary active), 0 = Disabled (dictionary inactive)
     */
    @Schema(description = "Dictionary status (1-enabled, 0-disabled）", example = "1")
    @Range(min = 0, max = 1, message = "The dictionary state is incorrect")
    private Integer status;

}

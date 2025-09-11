package community.waterlevel.iot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

/**
 * Form object representing department data for create and update operations.
 * <p>
 * Encapsulates department name, code, parent ID, status, and sort order. Used
 * for data transfer
 * between client and server in department management features.
 * </p>
 * 
 * @author youlai
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "Department form object")
@Getter
@Setter
public class DeptForm {

    /**
     * Department primary key identifier for updates.
     * Used for modifying existing departments, null for new department creation.
     */
    @Schema(description = "ID", example = "1001")
    private Long id;

    /**
     * Department display name for organizational identification.
     * Human-readable name shown throughout the application and organizational
     * charts.
     */
    @Schema(description = "Dept Name")
    private String name;

    /**
     * Unique department code for system identification.
     * Alphanumeric identifier used for programmatic department reference and
     * integration.
     */
    @Schema(description = "Dept Code")
    private String code;

    /**
     * Parent department identifier for hierarchical structure.
     * Links this department to its parent in the organizational tree structure.
     */
    @Schema(description = "parentId")
    @NotNull(message = "NotNull")
    private Long parentId;

    /**
     * Department activation status for operational control.
     * 1 = Active (department operational), 0 = Disabled (department inactive)
     */
    @Schema(description = "Status (1: enabled; 0: disabled)", example = "1")
    @Range(min = 0, max = 1, message = "Incorrect status value")
    private Integer status;

    /**
     * Display order for department listing and hierarchy.
     * Lower values appear first in department lists and organizational displays.
     */
    @Schema(description = "Sort", example = "1")
    private Integer sort;

}

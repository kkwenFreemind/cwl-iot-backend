package community.waterlevel.iot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Form object representing dictionary item data for create and update
 * operations.
 * <p>
 * Encapsulates dictionary code, value, label, status, sort order, and tag type.
 * Used for data transfer
 * between client and server in dictionary item management features.
 * </p>
 *
 * @author Ray Hao
 * @since 2.9.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "DictItem Form")
@Data
public class DictItemForm {

    /**
     * Dictionary item primary key identifier for updates.
     * Used for modifying existing dictionary items, null for new item creation.
     */
    @Schema(description = "id")
    private Long id;

    /**
     * Parent dictionary category code for item classification.
     * Links this item to its parent dictionary category for proper grouping.
     */
    @Schema(description = "dictCode")
    private String dictCode;

    /**
     * Dictionary item value for programmatic use.
     * The actual value stored in the database and used in application logic.
     */
    @Schema(description = "value")
    private String value;

    /**
     * Human-readable display label for user interfaces.
     * The text shown to users in dropdowns, selections, and display contexts.
     */
    @Schema(description = "label")
    private String label;

    /**
     * Display order for item listing within the dictionary category.
     * Lower values appear first in dropdown lists and selection interfaces.
     */
    @Schema(description = "sort")
    private Integer sort;

    /**
     * Dictionary item activation status for availability control.
     * 0 = Disabled (item hidden), 1 = Enabled (item available for selection)
     */
    @Schema(description = "Status (0: disabled, 1: enabled)")
    private Integer status;

    /**
     * Visual styling tag type for UI presentation enhancement.
     * Defines the display style (color, badge type) for better visual
     * categorization.
     */
    @Schema(description = "tagType")
    private String tagType;

}

package community.waterlevel.iot.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * View object representing a paginated dictionary item record for API
 * responses.
 * <p>
 * Encapsulates dictionary item ID, code, label, value, sort order, and status.
 * Used to transfer
 * paginated dictionary item data from the backend to the client in dictionary
 * management features.
 * </p>
 *
 * @author Ray.Hao
 * @since 0.0.1
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "DictItemPage VO")
@Getter
@Setter
public class DictItemPageVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "dictCode")
    private String dictCode;

    @Schema(description = "label")
    private String label;

    @Schema(description = "value")
    private String value;

    @Schema(description = "sort")
    private Integer sort;

    @Schema(description = "Status (1: enabled; 0: disabled）")
    private Integer status;

}

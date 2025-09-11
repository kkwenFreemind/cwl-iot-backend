package community.waterlevel.iot.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Value object representing a dictionary item's value, label, and tag type for API responses.
 * <p>
 * Used to transfer dictionary item options from the backend to the client, typically for dropdowns or selection lists.
 * </p>
 *
 * @author Ray.Hao
 * @since 0.0.1
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "DictItemOption VO")
@Getter
@Setter
public class DictItemOptionVO {

    @Schema(description = "value")
    private String value;

    @Schema(description = "label")
    private String label;

    @Schema(description = "tagType")
    private String tagType;

}

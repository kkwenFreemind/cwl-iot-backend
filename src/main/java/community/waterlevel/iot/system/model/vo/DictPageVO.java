package community.waterlevel.iot.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


/**
 * View object representing a paginated dictionary record for API responses.
 * <p>
 * Encapsulates dictionary ID, name, code, and status. Used to transfer
 * paginated dictionary data
 * from the backend to the client in dictionary management features.
 * </p>
 *
 * @author Ray
 * @since 0.0.1
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "DictPage VO")
@Getter
@Setter
public class DictPageVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "name")
    private String name;

    @Schema(description = "dictCode")
    private String dictCode;

    @Schema(description = "Status (1: enabled; 0: disabled)")
    private Integer status;

}

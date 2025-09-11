package community.waterlevel.iot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * View object representing a paginated role record for API responses.
 * <p>
 * Encapsulates role ID, name, code, status, sort order, and timestamps. Used to
 * transfer
 * paginated role data from the backend to the client in role management
 * features.
 * </p>
 * 
 * @author youlaitech
 * @since 2024-08-27 10:31
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "Role page view object")
@Data
public class RolePageVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "name")
    private String name;

    @Schema(description = "code")
    private String code;

    @Schema(description = "status")
    private Integer status;

    @Schema(description = "sort")
    private Integer sort;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

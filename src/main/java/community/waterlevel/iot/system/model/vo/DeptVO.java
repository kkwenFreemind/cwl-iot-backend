package community.waterlevel.iot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * View object representing department data for API responses.
 * <p>
 * Encapsulates department ID, parent ID, name, code, status, sort order, child
 * departments, and timestamps.
 * Used to transfer department data from the backend to the client in department
 * management features.
 * </p>
 * 
 * @author youlaitech
 * @since 2024-08-27 10:31
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
@Schema(description = "Dept VO")
public class DeptVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "parentId")
    private Long parentId;

    @Schema(description = "name")
    private String name;

    @Schema(description = "code")
    private String code;

    @Schema(description = "sort")
    private Integer sort;

    @Schema(description = "Status (1: enabled; 0: disabled)")
    private Integer status;

    @Schema(description = "children")
    private List<DeptVO> children;

    @Schema(description = "create Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;

    @Schema(description = "update Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updateTime;

}

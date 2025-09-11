package community.waterlevel.iot.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Query object for searching and filtering department records.
 * <p>
 * Supports keyword-based search on department name and filtering by status.
 * Used for paginated or filtered queries in department management features.
 * </p>
 *
 * @author haoxr
 * @since 2022/6/11
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description ="Dept Query")
@Data
public class DeptQuery {

    @Schema(description="keywords(Department Name)")
    private String keywords;

    @Schema(description="Status (1->normal; 0->disabled)")
    private Integer status;

}

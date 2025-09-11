package community.waterlevel.iot.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Query object for searching and filtering menu records.
 * <p>
 * Supports keyword-based search on menu name and filtering by status (visible/hidden).
 * Used for paginated or filtered queries in menu management features.
 * </p>
 *
 * @author haoxr
 * @since 2022/10/28
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11 
 */
@Schema(description ="Menu query object")
@Data
public class MenuQuery {

    @Schema(description="Keyword (menu name)")
    private String keywords;

    @Schema(description="Status (1->show; 0->hide)")
    private Integer status;

}

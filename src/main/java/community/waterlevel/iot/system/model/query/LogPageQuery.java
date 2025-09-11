package community.waterlevel.iot.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

import community.waterlevel.iot.common.base.BasePageQuery;

/**
 * Page query object for searching and filtering log records.
 * <p>
 * Supports keyword-based search on log content, request path, method, region, browser, and OS.
 * Also allows filtering by operation time range. Inherits pagination features from the base class.
 * Used for paginated queries in log management features.
 * </p>
 *
 * @author Ray
 * @since 2.10.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11 
 */
@Schema(description = "Log paging query object")
@Getter
@Setter
public class LogPageQuery extends BasePageQuery {

    @Schema(description="Keywords (log content/request path/request method/region/browser/terminal system)")
    private String keywords;

    @Schema(description="create Time")
    List<String> createTime;

}

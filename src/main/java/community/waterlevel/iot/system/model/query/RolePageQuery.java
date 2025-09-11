package community.waterlevel.iot.system.model.query;

import com.fasterxml.jackson.annotation.JsonFormat;

import community.waterlevel.iot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Page query object for searching and filtering role records.
 * <p>
 * Supports keyword-based search on role name or code, and filtering by start and end date.
 * Inherits pagination features from the base class. Used for paginated queries in role management features.
 * </p>
 *
 * @author Ray
 * @since 2022/6/3
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "Role paging query object")
@Getter
@Setter
public class RolePageQuery extends BasePageQuery {

    @Schema(description="Keywords (role name/role code)")
    private String keywords;

    @Schema(description="start Date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate;

    @Schema(description="end Date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;
}

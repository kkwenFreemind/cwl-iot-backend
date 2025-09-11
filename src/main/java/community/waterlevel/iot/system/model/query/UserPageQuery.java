package community.waterlevel.iot.system.model.query;

import cn.hutool.db.sql.Direction;
import community.waterlevel.iot.common.annotation.ValidField;
import community.waterlevel.iot.common.base.BasePageQuery;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Page query object for searching and filtering user records.
 * <p>
 * Supports keyword-based search on username, nickname, or mobile, filtering by status, department, roles,
 * creation time range, and sorting by specified fields and direction. Inherits pagination features from the base class.
 * Used for paginated queries in user management features.
 * </p>
 *
 * @author haoxr
 * @since 2022/1/14
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "User paging query object")
public class UserPageQuery extends BasePageQuery {

    @Schema(description = "Keywords (username/nickname/mobile phone number)")
    private String keywords;

    @Schema(description = "User Status")
    private Integer status;

    @Schema(description = "deptId")
    private Long deptId;

    @Schema(description = "roleIds")
    private List<Long> roleIds;

    @Schema(description = "create Time")
    private List<String> createTime;

    @Schema(description = "Sort fields")
    @ValidField(allowedValues = {"create_time", "update_time"})
    private String field;

    @Schema(description = "Sorting method (positive order: ASC; reverse order: DESC)")
    private Direction direction;

    @JsonIgnore
    @Schema(hidden = true)
    private Boolean isRoot;

}

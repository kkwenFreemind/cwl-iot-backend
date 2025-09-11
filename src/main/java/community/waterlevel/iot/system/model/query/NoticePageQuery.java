package community.waterlevel.iot.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

import community.waterlevel.iot.common.base.BasePageQuery;

/**
 * Page query object for searching and filtering notice/announcement records.
 * <p>
 * Supports search by title, publish status, publish time range, user ID, and
 * read status.
 * Inherits pagination features from the base class. Used for paginated queries
 * in notice management features.
 * </p>
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Notice and announcement query object")
public class NoticePageQuery extends BasePageQuery {

    @Schema(description = "Notification Title")
    private String title;

    @Schema(description = "Release status (0 - not released 1 - released 1 - withdrawn)")
    private Integer publishStatus;

    @Schema(description = "publishTime")
    private List<String> publishTime;

    @Schema(description = "userId")
    private Long userId;

    @Schema(description = "Has it been read? (0 - unread 1 - read)")
    private Integer isRead;

}

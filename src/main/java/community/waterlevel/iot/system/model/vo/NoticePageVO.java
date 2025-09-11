package community.waterlevel.iot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * View object representing a paginated notice/announcement record for API
 * responses.
 * <p>
 * Encapsulates notice ID, title, status, type, publisher, level, publish time,
 * read status, target type, and timestamps.
 * Used to transfer paginated notice data from the backend to the client in
 * notice management features.
 * </p>
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Getter
@Setter
@Schema(description = "NoticePage VO")
public class NoticePageVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    private Long id;

    @Schema(description = "title")
    private String title;

    @Schema(description = "publishStatus")
    private Integer publishStatus;

    @Schema(description = "type")
    private Integer type;

    @Schema(description = "publisherName")
    private String publisherName;

    @Schema(description = "level")
    private String level;

    @Schema(description = "publish Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime publishTime;

    @Schema(description = "isRead")
    private Integer isRead;

    @Schema(description = "targetType")
    private Integer targetType;

    @Schema(description = "create Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;

    @Schema(description = "revoke Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime revokeTime;
}

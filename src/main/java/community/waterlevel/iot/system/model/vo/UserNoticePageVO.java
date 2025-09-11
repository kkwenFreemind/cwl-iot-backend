package community.waterlevel.iot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * View object representing a user notice for API responses.
 * <p>
 * Encapsulates notice ID, title, type, level, publisher, publish time, and read
 * status.
 * Used to transfer user notice data from the backend to the client in
 * notification features.
 * </p>
 *
 * @author Theo
 * @since 2024-08-28 16:56
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
@Schema(description = "UserNoticePage VO")
public class UserNoticePageVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "title")
    private String title;

    @Schema(description = "type")
    private Integer type;

    @Schema(description = "level")
    private String level;

    @Schema(description = "publisherName")
    private String publisherName;

    @Schema(description = "publishTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime publishTime;

    @Schema(description = "isRead")
    private Integer isRead;

}

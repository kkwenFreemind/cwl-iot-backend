package community.waterlevel.iot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * View object representing detailed notice/announcement information for API responses.
 * <p>
 * Encapsulates notice ID, title, content, type, publisher, priority, publish status, and publish time.
 * Used to transfer detailed notice data from the backend to the client in notice management and reading features.
 * </p>
 *
 * @author Theo
 * @since 2024-9-8 01:25:06
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
public class NoticeDetailVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "title")
    private String title;

    @Schema(description = "content")
    private String content;

    @Schema(description = "type")
    private Integer type;

    @Schema(description = "publisherName")
    private String publisherName;

    @Schema(description = "Priority (L-Low M-Medium H-High)")
    private String level;

    @Schema(description = "Release status (0-Not released 1 Released 2 Withdrawn) Redundant field to facilitate the determination of whether it has been released")
    private Integer publishStatus;

    @Schema(description = "publishTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
}

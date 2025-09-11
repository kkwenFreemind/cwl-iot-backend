package community.waterlevel.iot.system.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * NoticeDTO is a data transfer object representing a system notice for delivery
 * to clients or other services.
 * <p>
 * This class encapsulates the notice ID, type, title, and publish time, and is
 * used for transferring notice data in the IoT backend.
 *
 * @author Theo
 * @since 2024-9-2 14:32:58
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
public class NoticeDTO {

    @Schema(description = "Notice ID")
    private Long id;

    @Schema(description = "Notice Type")
    private Integer type;

    @Schema(description = "Notice Title")
    private String title;

    @Schema(description = "Publish Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime publishTime;

}

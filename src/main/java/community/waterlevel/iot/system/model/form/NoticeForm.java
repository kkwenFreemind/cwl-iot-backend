package community.waterlevel.iot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Notice announcement form object for notification management operations.
 * Handles notice creation and updates with comprehensive content validation,
 * targeting options, and priority settings for effective communication
 * management.
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Getter
@Setter
@Schema(description = "Notice Form")
public class NoticeForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Notice ID")
    private Long id;

    @Schema(description = "Notice Title")
    @NotBlank(message = "Notification title cannot be empty")
    @Size(max = 50, message = "The notification title cannot exceed 50 characters.")
    private String title;

    @Schema(description = "Notification content")
    @NotBlank(message = "Notification content cannot be empty")
    @Size(max = 65535, message = "The notification content length cannot exceed 65535 characters")
    private String content;

    @Schema(description = "Notification Type")
    private Integer type;

    @Schema(description = "Priority (L-Low M-Medium H-High)")
    private String level;

    @Schema(description = "Target type (1-all 2-specific)")
    @Range(min = 1, max = 2, message = "Target type value range[1,2]")
    private Integer targetType;

    @Schema(description = "Recipient ID collection")
    private List<String> targetUserIds;

}

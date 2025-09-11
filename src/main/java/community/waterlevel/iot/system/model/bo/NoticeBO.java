package community.waterlevel.iot.system.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * NoticeBO is a business object representing a system notice or announcement.
 * <p>
 * This class encapsulates the core business data for notices, including title, type, content, publisher, level, target type, publish status, and relevant timestamps.
 * Used for transferring notice data between service and presentation layers in the IoT backend.
 *
 * @author Theo
 * @since 2024-09-01 10:31
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
public class NoticeBO {

    /**
     * Unique notice identifier for database reference.
     * Primary key used for notice retrieval and management operations.
     */
    private Long id;

    /**
     * Notice title for display and identification.
     * Brief, descriptive heading summarizing the notification content.
     */
    private String title;

    /**
     * Notice type classification code.
     * Numeric identifier categorizing the type of notification.
     */
    private Integer type;

    /**
     * Human-readable notice type description.
     * Localized label corresponding to the type code for UI display.
     */
    private String typeLabel;

    /**
     * Full notification content body.
     * Detailed message text for the announcement or notification.
     */
    private String content;

    /**
     * Publisher's display name for attribution.
     * Human-readable name of the user who created this notice.
     */
    private String publisherName;

    /**
     * Priority level indicator for importance classification.
     * L = Low priority, M = Medium priority, H = High priority
     */
    private String level;

    /**
     * Target audience scope for delivery control.
     * 1 = All users (broadcast), 2 = Specific users (targeted delivery)
     */
    private Integer targetType;

    /**
     * Current publication status of the notice.
     * 0 = Unpublished (draft), 1 = Published (active), -1 = Revoked (withdrawn)
     */
    private Integer publishStatus;

    /**
     * Notice creation timestamp for audit tracking.
     * Records when the notice was initially created in the system.
     */
    private LocalDateTime createTime;

    /**
     * Publication timestamp for lifecycle tracking.
     * Records when the notice was made available to users.
     */
    private LocalDateTime publishTime;

    /**
     * Revocation timestamp for withdrawal tracking.
     * Records when a published notice was withdrawn from public view.
     */
    private LocalDateTime revokeTime;
}

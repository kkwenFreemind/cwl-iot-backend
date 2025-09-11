package community.waterlevel.iot.system.model.entity;

import community.waterlevel.iot.common.base.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

/**
 * Notice announcement JPA entity class for managing system notifications and
 * announcements.
 * Supports targeted notification delivery with priority levels and publication
 * lifecycle management.
 * Includes soft delete functionality for data retention and audit purposes.
 *
 * @author Kylin
 * @since 2024-08-27 10:31
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Getter
@Setter
@Entity
@Table(name = "sys_notice")
@SQLDelete(sql = "UPDATE sys_notice SET is_deleted = 1 WHERE id = ?")
@SQLRestriction("is_deleted = 0")
public class NoticeJpa extends BaseJpaEntity {

    /**
     * Title of the notice announcement.
     * Brief, descriptive heading for the notification content.
     */
    @Column(name = "title")
    private String title;

    /**
     * Full content body of the notice.
     * Detailed notification message stored as text for unlimited length.
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * Type classification of the notice.
     * Used to categorize notices for filtering and display purposes.
     */
    @Column(name = "type")
    private Integer type;

    /**
     * User ID of the notice publisher.
     * References the user who created and published this notice.
     */
    @Column(name = "publisher_id")
    private Long publisherId;

    /**
     * Priority level of the notice.
     * L = Low priority, M = Medium priority, H = High priority
     */
    @Column(name = "level")
    private String level;

    /**
     * Target audience type for the notice.
     * 1 = All users (broadcast), 2 = Specific users (targeted)
     */
    @Column(name = "target_type")
    private Integer targetType;

    /**
     * Target user IDs for specific notifications.
     * Comma-separated list of user IDs when targetType is 2 (specific users).
     */
    @Column(name = "target_user_ids", columnDefinition = "TEXT")
    private String targetUserIds;

    /**
     * Publication status of the notice.
     * 0 = Unpublished (draft), 1 = Published (active), -1 = Revoked (withdrawn)
     */
    @Column(name = "publish_status")
    private Integer publishStatus;

    /**
     * Timestamp when the notice was published.
     * Records the exact time when the notice became active for users.
     */
    @Column(name = "publish_time")
    private LocalDateTime publishTime;

    /**
     * Timestamp when the notice was revoked.
     * Records when a published notice was withdrawn from public view.
     */
    @Column(name = "revoke_time")
    private LocalDateTime revokeTime;
}

package community.waterlevel.iot.system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User-notice association JPA entity for tracking notification delivery status.
 * Manages the relationship between users and notices, including read status
 * and timestamp tracking for notification lifecycle management.
 *
 * @author Kylin
 * @since 2024-08-28 16:56
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Getter
@Setter
@Entity
@Table(name = "sys_user_notice")
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE sys_user_notice SET is_deleted = 1 WHERE id = ?")
@SQLRestriction("is_deleted = 0")
public class UserNoticeJpa implements Serializable {

    /**
     * Primary key identifier for the user-notice association.
     * Auto-generated unique identifier for each notification delivery record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Foreign key reference to the notice entity.
     * Links to sys_notice.id for the associated notification.
     */
    @Column(name = "notice_id")
    private Long noticeId;

    /**
     * Foreign key reference to the user entity.
     * Links to sys_user.id for the notification recipient.
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Notification read status tracking.
     * 0 = Unread (notification not viewed), 1 = Read (notification viewed)
     */
    @Column(name = "is_read")
    private Integer isRead = 0;

    /**
     * Timestamp when user read the notification.
     * Records the exact moment when notification status changed to read.
     */
    @Column(name = "read_time")
    private LocalDateTime readTime;

    /**
     * Record creation timestamp.
     * Automatically populated when the user-notice association is created.
     */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * Record last modification timestamp.
     * Automatically updated when any field in this entity changes.
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * Soft delete flag for data retention.
     * 0 = Active record, 1 = Logically deleted (hidden from queries)
     */
    @Column(name = "is_deleted", nullable = false)
    private Integer isDeleted = 0;
}

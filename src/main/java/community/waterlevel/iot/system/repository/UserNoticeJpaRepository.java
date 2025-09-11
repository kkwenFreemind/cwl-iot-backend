package community.waterlevel.iot.system.repository;

import community.waterlevel.iot.system.model.entity.UserNoticeJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing user notice entities.
 * <p>
 * Provides methods for marking notices as read, querying user notices, deleting
 * by notice ID,
 * counting unread notices, listing unread notices, and creating user notice
 * records.
 * Extends the base JPA repository for CRUD operations and specification-based
 * queries on {@link UserNoticeJpa} entities.
 * </p>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Repository
public interface UserNoticeJpaRepository
        extends JpaRepository<UserNoticeJpa, Long>, JpaSpecificationExecutor<UserNoticeJpa> {

    /**
     * Marks a user notice as read by updating its read status and read time.
     *
     * @param id       the ID of the user notice
     * @param readTime the time the notice was read
     * @return the number of affected rows
     */
    @Modifying
    @Query("UPDATE UserNoticeJpa un SET un.isRead = 1, un.readTime = :readTime WHERE un.id = :id")
    int markAsRead(@Param("id") Long id, @Param("readTime") LocalDateTime readTime);

    /**
     * Finds a user notice by notice ID and user ID.
     *
     * @param noticeId the ID of the notice
     * @param userId   the ID of the user
     * @return the user notice entity if found, or null otherwise
     */
    @Query("SELECT un FROM UserNoticeJpa un WHERE un.noticeId = :noticeId AND un.userId = :userId")
    UserNoticeJpa findByNoticeIdAndUserId(@Param("noticeId") Long noticeId, @Param("userId") Long userId);

    /**
     * Logically deletes all user notices by notice ID (sets isDeleted to 1).
     *
     * @param noticeId the ID of the notice
     * @return the number of affected rows
     */
    @Modifying
    @Query("UPDATE UserNoticeJpa un SET un.isDeleted = 1 WHERE un.noticeId = :noticeId")
    int deleteByNoticeId(@Param("noticeId") Long noticeId);

    /**
     * Counts the number of unread notices for a specific user.
     *
     * @param userId the ID of the user
     * @return the number of unread notices
     */
    @Query("SELECT COUNT(un) FROM UserNoticeJpa un WHERE un.userId = :userId AND un.isRead = 0")
    long countUnreadByUserId(@Param("userId") Long userId);

    /**
     * Retrieves a list of unread user notices for a specific user, ordered by
     * creation time descending.
     *
     * @param userId the ID of the user
     * @return a list of unread user notices ordered by creation time descending
     */
    @Query("SELECT un FROM UserNoticeJpa un WHERE un.userId = :userId AND un.isRead = 0 ORDER BY un.createTime DESC")
    List<UserNoticeJpa> listUnreadByUserId(@Param("userId") Long userId);

    /**
     * Creates a user notice record in the database.
     *
     * @param noticeId   the ID of the notice
     * @param userId     the ID of the user
     * @param createTime the creation time of the record
     * @param createBy   the ID of the user who created the record
     */
    @Modifying
    @Query(value = """
            INSERT INTO sys_user_notice (notice_id, user_id, is_read, create_time, create_by, is_deleted)
            VALUES (:noticeId, :userId, 0, :createTime, :createBy, 0)
            """, nativeQuery = true)
    void createUserNotice(@Param("noticeId") Long noticeId,
            @Param("userId") Long userId,
            @Param("createTime") LocalDateTime createTime,
            @Param("createBy") Long createBy);
}

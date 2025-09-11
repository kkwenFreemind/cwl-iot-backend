package community.waterlevel.iot.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import community.waterlevel.iot.system.model.query.NoticePageQuery;
import community.waterlevel.iot.system.model.vo.UserNoticePageVO;

/**
 * Service interface for managing user notice status.
 * <p>
 * Provides methods for paginating user notices, marking notices as read,
 * checking notice existence,
 * creating user notice records, deleting notices by notice ID, and retrieving
 * unread notice counts.
 * </p>
 *
 * @author youlaitech
 * @since 2024-08-28 16:56
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface UserNoticeJpaService {

    /**
     * Retrieves a paginated list of user notices based on the provided query
     * parameters.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return a paginated list of user notice view objects
     */
    IPage<UserNoticePageVO> getUserNoticePage(NoticePageQuery queryParams);

    /**
     * Marks a user notice as read by its unique identifier.
     *
     * @param id the unique identifier of the user notice
     * @return true if the operation was successful, false otherwise
     */
    boolean markAsRead(Long id);

    /**
     * Checks whether a user has received a specific notice.
     *
     * @param noticeId the unique identifier of the notice
     * @param userId   the unique identifier of the user
     * @return true if the user has the notice, false otherwise
     */
    boolean hasUserNotice(Long noticeId, Long userId);

    /**
     * Creates user notice records for a given notice and a list of user IDs.
     *
     * @param noticeId the unique identifier of the notice
     * @param userIds  a comma-separated string of user IDs
     * @return true if the operation was successful, false otherwise
     */
    boolean createUserNotices(Long noticeId, String userIds);

    /**
     * Deletes all user notice records associated with a specific notice ID.
     *
     * @param noticeId the unique identifier of the notice
     * @return true if the operation was successful, false otherwise
     */
    boolean deleteByNoticeId(Long noticeId);

    /**
     * Retrieves the count of unread notices for a specific user.
     *
     * @param userId the unique identifier of the user
     * @return the number of unread notices
     */
    long getUnreadCount(Long userId);
}

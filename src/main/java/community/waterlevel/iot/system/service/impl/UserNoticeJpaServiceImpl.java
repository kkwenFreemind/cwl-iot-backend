package community.waterlevel.iot.system.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import community.waterlevel.iot.core.security.util.SecurityUtils;
import community.waterlevel.iot.system.model.entity.UserNoticeJpa;
import community.waterlevel.iot.system.model.query.NoticePageQuery;
import community.waterlevel.iot.system.model.vo.UserNoticePageVO;
import community.waterlevel.iot.system.repository.UserNoticeJpaRepository;
import community.waterlevel.iot.system.service.SystemUserNoticeService;
import community.waterlevel.iot.system.service.UserNoticeJpaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of user notification status service using JPA.
 * <p>
 * This service provides operations for managing user notification states, including pagination, marking as read,
 * checking existence, batch creation, deletion, and unread count retrieval. It integrates with Spring Data JPA repositories
 * and security utilities for user context.
 * </p>
 *
 * @author youlaitech
 * @since 2024-08-28 16:56
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class UserNoticeJpaServiceImpl implements UserNoticeJpaService, SystemUserNoticeService {

    private final UserNoticeJpaRepository userNoticeJpaRepository;

    @Override
    /**
     * Retrieves a paginated list of user notifications based on query parameters.
     *
     * @param queryParams the notice page query parameters
     * @return a paginated list of user notification view objects
     */
    public IPage<UserNoticePageVO> getUserNoticePage(NoticePageQuery queryParams) {
        try {
            Long userId = queryParams.getUserId();
            if (userId == null) {
                userId = SecurityUtils.getUserId();
            }

            Pageable pageable = PageRequest.of(
                queryParams.getPageNum() - 1, 
                queryParams.getPageSize()
            );

            final Long finalUserId = userId;
            
            // List<UserNoticeJpa> unreadNotices = userNoticeJpaRepository.listUnreadByUserId(userId);

            org.springframework.data.domain.Page<UserNoticeJpa> jpaPage = userNoticeJpaRepository.findAll(
                (root, query, criteriaBuilder) -> {
                    var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
                    
                    predicates.add(criteriaBuilder.equal(root.get("userId"), finalUserId));
                    
                    if (queryParams.getIsRead() != null) {
                        predicates.add(criteriaBuilder.equal(root.get("isRead"), queryParams.getIsRead()));
                    }

                    query.orderBy(criteriaBuilder.desc(root.get("createTime")));
                    
                    return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
                }, 
                pageable
            );

            Page<UserNoticePageVO> result = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
            result.setTotal(jpaPage.getTotalElements());
            
            List<UserNoticePageVO> records = jpaPage.getContent().stream()
                .map(this::convertToPageVO)
                .toList();
            result.setRecords(records);

            return result;
        } catch (Exception e) {
            log.error("Failed to obtain the paginated list of user notifications.", e);
            return new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
        }
    }

    @Override
    /**
     * Marks a user notification as read by its ID.
     *
     * @param id the user notification ID
     * @return true if the notification was marked as read successfully, false otherwise
     */
    @Transactional
    public boolean markAsRead(Long id) {
        try {
            int updated = userNoticeJpaRepository.markAsRead(id, LocalDateTime.now());
            return updated > 0;
        } catch (Exception e) {
            log.error("Marking notification as read failed, id: {}", id, e);
            return false;
        }
    }

    @Override
    /**
     * Checks if a user has a specific notification.
     *
     * @param noticeId the notification ID
     * @param userId the user ID
     * @return true if the user has the notification, false otherwise
     */
    public boolean hasUserNotice(Long noticeId, Long userId) {
        try {
            UserNoticeJpa userNotice = userNoticeJpaRepository.findByNoticeIdAndUserId(noticeId, userId);
            return userNotice != null;
        } catch (Exception e) {
            log.error("Query user notification failed, noticeId: {}, userId: {}", noticeId, userId, e);
            return false;
        }
    }

    @Override
    /**
     * Creates user notification records for a given notification and a list of user IDs.
     *
     * @param noticeId the notification ID
     * @param userIds comma-separated user IDs
     * @return true if user notifications were created successfully, false otherwise
     */
    @Transactional
    public boolean createUserNotices(Long noticeId, String userIds) {
        try {
            if (StrUtil.isBlank(userIds)) {
                return false;
            }

            List<String> userIdList = Arrays.asList(userIds.split(","));
            LocalDateTime now = LocalDateTime.now();
            Long createBy = SecurityUtils.getUserId();

            for (String userIdStr : userIdList) {
                try {
                    Long userId = Long.parseLong(userIdStr.trim());
                    userNoticeJpaRepository.createUserNotice(noticeId, userId, now, createBy);
                } catch (NumberFormatException e) {
                    log.warn("Invalid user ID: {}", userIdStr);
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Failed to create user notification, noticeId: {}, userIds: {}", noticeId, userIds, e);
            return false;
        }
    }

    @Override
    /**
     * Deletes user notification records by notification ID.
     *
     * @param noticeId the notification ID
     * @return true if user notifications were deleted successfully, false otherwise
     */
    @Transactional
    public boolean deleteByNoticeId(Long noticeId) {
        try {
            int updated = userNoticeJpaRepository.deleteByNoticeId(noticeId);
            return updated >= 0; 
        } catch (Exception e) {
            log.error("Failed to delete user notification based on notification ID, noticeId: {}", noticeId, e);
            return false;
        }
    }

    @Override
    /**
     * Retrieves the count of unread notifications for a user.
     *
     * @param userId the user ID (if null, uses the current authenticated user)
     * @return the number of unread notifications
     */
    public long getUnreadCount(Long userId) {
        try {
            if (userId == null) {
                userId = SecurityUtils.getUserId();
            }
            return userNoticeJpaRepository.countUnreadByUserId(userId);
        } catch (Exception e) {
            log.error("Failed to obtain the number of unread notifications for the user., userId: {}", userId, e);
            return 0;
        }
    }

    /**
     * Converts a UserNoticeJpa entity to a UserNoticePageVO view object.
     *
     * @param entity the user notice JPA entity
     * @return the user notice page view object
     */
    private UserNoticePageVO convertToPageVO(UserNoticeJpa entity) {
        UserNoticePageVO vo = new UserNoticePageVO();
        vo.setId(entity.getNoticeId()); 
        vo.setIsRead(entity.getIsRead());
        vo.setTitle("JOIN query is required to obtain");
        vo.setType(0);
        vo.setLevel("M");
        vo.setPublisherName("System");
        vo.setPublishTime(entity.getCreateTime());
        return vo;
    }
}

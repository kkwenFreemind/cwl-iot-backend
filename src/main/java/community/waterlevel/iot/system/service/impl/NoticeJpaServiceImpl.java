package community.waterlevel.iot.system.service.impl;

import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import community.waterlevel.iot.core.security.util.SecurityUtils;
import community.waterlevel.iot.system.model.entity.NoticeJpa;
import community.waterlevel.iot.system.enums.NoticePublishStatusEnum;
import community.waterlevel.iot.system.model.form.NoticeForm;
import community.waterlevel.iot.system.model.query.NoticePageQuery;
import community.waterlevel.iot.system.model.vo.NoticeDetailVO;
import community.waterlevel.iot.system.model.vo.NoticePageVO;
import community.waterlevel.iot.system.model.vo.UserNoticePageVO;
import community.waterlevel.iot.system.repository.NoticeJpaRepository;
import community.waterlevel.iot.system.service.NoticeJpaService;
import community.waterlevel.iot.system.service.SystemNoticeJpaService;
import community.waterlevel.iot.system.service.SystemUserNoticeService;
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
import java.util.Optional;

/**
 * Implementation of notification and announcement business logic for the system.
 * <p>
 * Provides services for managing notifications, including CRUD operations, publishing,
 * revoking, pagination, and user-specific notification management. Integrates with user
 * notification services and supports both JPA and MyBatis-Plus pagination models.
 * </p>
 *
 * @author Theo
 * @since 2024-08-27 10:31
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class NoticeJpaServiceImpl implements NoticeJpaService, SystemNoticeJpaService {

    private final NoticeJpaRepository noticeJpaRepository;
    private final SystemUserNoticeService userNoticeService;

    /**
     * Retrieves a paginated list of notifications and announcements based on query parameters.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return a MyBatis-Plus IPage containing notification page view objects
     */
    @Override
    public IPage<NoticePageVO> getNoticePage(NoticePageQuery queryParams) {
        try {
            org.springframework.data.domain.Page<NoticeJpa> jpaPage = executeJpaQuery(queryParams);
            IPage<NoticePageVO> result = convertToMybatisPage(jpaPage, queryParams);
            return result;
        } catch (Exception e) {
            log.error("Failed to obtain the paginated list of notifications and announcements", e);
            return new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
        }
    }

    /**
     * Executes a JPA query to retrieve a paginated list of notifications based on query parameters.
     *
     * @param queryParams the query parameters for filtering and pagination
     * @return a JPA Page of NoticeJpa entities
     */
    private org.springframework.data.domain.Page<NoticeJpa> executeJpaQuery(NoticePageQuery queryParams) {
        long totalCount = noticeJpaRepository.count();
        log.info("Total number of notifications in the database: {}", totalCount);
        Pageable pageable = PageRequest.of(
                queryParams.getPageNum() - 1,
                queryParams.getPageSize());
        if (StrUtil.isBlank(queryParams.getTitle()) && queryParams.getPublishStatus() == null) {
            org.springframework.data.domain.Page<NoticeJpa> jpaPage = noticeJpaRepository.findAll(pageable);
            return jpaPage;
        }
        org.springframework.data.domain.Page<NoticeJpa> jpaPage = noticeJpaRepository.findAll(
                (root, query, criteriaBuilder) -> {
                    var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
                    if (StrUtil.isNotBlank(queryParams.getTitle())) {
                        predicates.add(criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("title")),
                                "%" + queryParams.getTitle().toLowerCase() + "%"));
                        log.info("Add title query conditions: {}", queryParams.getTitle());
                    }
                    if (queryParams.getPublishStatus() != null) {
                        predicates
                                .add(criteriaBuilder.equal(root.get("publishStatus"), queryParams.getPublishStatus()));
                        log.info("Add publishing status query conditions: {}", queryParams.getPublishStatus());
                    }
                    if (query != null) {
                        query.orderBy(criteriaBuilder.desc(root.get("createTime")));
                    }
                    if (predicates.isEmpty()) {
                        return criteriaBuilder.conjunction();
                    }
                    return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
                },
                pageable);
        return jpaPage;
    }


    /**
     * Converts a JPA Page of NoticeJpa entities to a MyBatis-Plus IPage of NoticePageVO objects.
     *
     * @param jpaPage     the JPA Page of NoticeJpa entities
     * @param queryParams the query parameters for pagination
     * @return a MyBatis-Plus IPage of NoticePageVO objects
     */
    private IPage<NoticePageVO> convertToMybatisPage(org.springframework.data.domain.Page<NoticeJpa> jpaPage,
            NoticePageQuery queryParams) {
        Page<NoticePageVO> result = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
        result.setTotal(jpaPage.getTotalElements());
        List<NoticePageVO> records = jpaPage.getContent().stream()
                .map(this::convertToPageVO)
                .toList();
        result.setRecords(records);
        return result;
    }

    /**
     * Retrieves the form data for a specific notification by its ID.
     *
     * @param id the notification ID
     * @return the NoticeForm object, or null if not found
     */
    @Override
    public NoticeForm getNoticeFormData(Long id) {
        try {
            Optional<NoticeJpa> notice = noticeJpaRepository.findById(id);
            if (notice.isPresent()) {
                return convertToForm(notice.get());
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to obtain notification form information, id: {}", id, e);
            return null;
        }
    }

    /**
     * Saves a new notification announcement to the database.
     *
     * @param formData the form data for the notification
     * @return true if the notification was saved successfully
     */
    @Override
    @Transactional
    public boolean saveNotice(NoticeForm formData) {
        try {
            NoticeJpa notice = convertToEntity(formData);
            notice.setPublishStatus(NoticePublishStatusEnum.UNPUBLISHED.getValue());
            noticeJpaRepository.save(notice);
            return true;
        } catch (Exception e) {
            log.error("Failed to save notification announcement", e);
            return false;
        }
    }

    /**
     * Updates an existing notification announcement by its ID.
     *
     * @param id       the notification ID
     * @param formData the updated form data
     * @return true if the update was successful
     */
    @Override
    @Transactional
    public boolean updateNotice(Long id, NoticeForm formData) {
        try {
            Optional<NoticeJpa> existingNotice = noticeJpaRepository.findById(id);
            if (existingNotice.isPresent()) {
                NoticeJpa notice = convertToEntity(formData);
                notice.setId(id);
                notice.setPublishStatus(existingNotice.get().getPublishStatus());
                notice.setPublishTime(existingNotice.get().getPublishTime());
                notice.setRevokeTime(existingNotice.get().getRevokeTime());
                noticeJpaRepository.save(notice);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Update notification announcement failed, id: {}", id, e);
            return false;
        }
    }

    /**
     * Deletes one or more notifications by their IDs.
     *
     * @param ids a comma-separated string of notification IDs
     * @return true if all notifications were deleted successfully
     */
    @Override
    @Transactional
    public boolean deleteNotices(String ids) {
        try {
            List<String> idList = Arrays.asList(ids.split(","));
            for (String id : idList) {
                Long noticeId = Long.parseLong(id.trim());
                noticeJpaRepository.deleteById(noticeId);
                userNoticeService.deleteByNoticeId(noticeId);
            }
            return true;
        } catch (Exception e) {
            log.error("Failed to delete notification announcement, ids: {}", ids, e);
            return false;
        }
    }

    /**
     * Publishes a notification announcement by its ID.
     *
     * @param id the notification ID
     * @return true if the notification was published successfully
     */
    @Override
    @Transactional
    public boolean publishNotice(Long id) {
        try {
            Optional<NoticeJpa> noticeOpt = noticeJpaRepository.findById(id);
            if (noticeOpt.isPresent()) {
                NoticeJpa notice = noticeOpt.get();
                int updated = noticeJpaRepository.publishNotice(id, LocalDateTime.now());
                if (updated > 0) {
                    if (notice.getTargetType() == 1) {
                        log.info("All notifications are released successfully, and the logic of obtaining all users needs to be implemented");
                    } else if (notice.getTargetType() == 2 && StrUtil.isNotBlank(notice.getTargetUserIds())) {
                        userNoticeService.createUserNotices(id, notice.getTargetUserIds());
                    }
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Release notification announcement failed, id: {}", id, e);
            return false;
        }
    }

    /**
     * Revokes (withdraws) a notification announcement by its ID.
     *
     * @param id the notification ID
     * @return true if the notification was revoked successfully
     */
    @Override
    @Transactional
    public boolean revokeNotice(Long id) {
        try {
            int updated = noticeJpaRepository.revokeNotice(id, LocalDateTime.now());
            return updated > 0;
        } catch (Exception e) {
            log.error("Failed to withdraw the notice, id: {}", id, e);
            return false;
        }
    }

    /**
     * Retrieves the detailed view object for a specific notification by its ID.
     *
     * @param id the notification ID
     * @return the NoticeDetailVO object, or null if not found
     */
    @Override
    public NoticeDetailVO getNoticeDetail(Long id) {
        try {
            Optional<NoticeJpa> notice = noticeJpaRepository.findById(id);
            if (notice.isPresent()) {
                return convertToDetailVO(notice.get());
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to obtain notification announcement details, id: {}", id, e);
            return null;
        }
    }

    /**
     * Retrieves a paginated list of notifications for the current user.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return a MyBatis-Plus IPage of UserNoticePageVO objects
     */
    @Override
    public IPage<UserNoticePageVO> getMyNoticePage(NoticePageQuery queryParams) {
        return userNoticeService.getUserNoticePage(queryParams);
    }

    /**
     * Converts a NoticeJpa entity to a NoticePageVO view object.
     *
     * @param entity the NoticeJpa entity
     * @return the NoticePageVO view object
     */
    private NoticePageVO convertToPageVO(NoticeJpa entity) {
        NoticePageVO vo = new NoticePageVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setType(entity.getType());
        vo.setLevel(entity.getLevel());
        vo.setTargetType(entity.getTargetType());
        vo.setPublishStatus(entity.getPublishStatus());
        vo.setPublishTime(entity.getPublishTime());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    /**
     * Converts a NoticeJpa entity to a NoticeForm object for editing.
     *
     * @param entity the NoticeJpa entity
     * @return the NoticeForm object
     */
    private NoticeForm convertToForm(NoticeJpa entity) {
        NoticeForm form = new NoticeForm();
        form.setId(entity.getId());
        form.setTitle(entity.getTitle());
        form.setContent(entity.getContent());
        form.setType(entity.getType());
        form.setLevel(entity.getLevel());
        form.setTargetType(entity.getTargetType());
        if (StrUtil.isNotBlank(entity.getTargetUserIds())) {
            form.setTargetUserIds(Arrays.asList(entity.getTargetUserIds().split(",")));
        }
        return form;
    }

    /**
     * Converts a NoticeForm object to a NoticeJpa entity for persistence.
     *
     * @param form the NoticeForm object
     * @return the NoticeJpa entity
     */
    private NoticeJpa convertToEntity(NoticeForm form) {
        NoticeJpa entity = new NoticeJpa();
        entity.setTitle(form.getTitle());
        entity.setContent(form.getContent());
        entity.setType(form.getType());
        entity.setLevel(form.getLevel());
        entity.setTargetType(form.getTargetType());
        entity.setPublisherId(SecurityUtils.getUserId());
        if (form.getTargetUserIds() != null && !form.getTargetUserIds().isEmpty()) {
            entity.setTargetUserIds(String.join(",", form.getTargetUserIds()));
        }
        return entity;
    }

    /**
     * Converts a NoticeJpa entity to a NoticeDetailVO view object for detailed display.
     *
     * @param entity the NoticeJpa entity
     * @return the NoticeDetailVO view object
     */
    private NoticeDetailVO convertToDetailVO(NoticeJpa entity) {
        NoticeDetailVO vo = new NoticeDetailVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setType(entity.getType());
        vo.setLevel(entity.getLevel());
        vo.setPublishStatus(entity.getPublishStatus());
        vo.setPublishTime(entity.getPublishTime());
        return vo;
    }
}

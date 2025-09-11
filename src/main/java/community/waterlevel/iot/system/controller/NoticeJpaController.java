package community.waterlevel.iot.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import community.waterlevel.iot.common.result.PageResult;
import community.waterlevel.iot.common.result.Result;
import community.waterlevel.iot.system.model.form.NoticeForm;
import community.waterlevel.iot.system.model.query.NoticePageQuery;
import community.waterlevel.iot.system.model.vo.NoticeDetailVO;
import community.waterlevel.iot.system.model.vo.NoticePageVO;
import community.waterlevel.iot.system.model.vo.UserNoticePageVO;
import community.waterlevel.iot.system.service.SystemNoticeJpaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * NoticeJpaController is a REST controller that provides endpoints for managing
 * notifications and announcements in the system.
 * <p>
 * It exposes APIs for listing, creating, updating, publishing, revoking,
 * deleting, and marking notifications as read, supporting both admin and
 * user-specific operations. The controller delegates business logic to the
 * {@link NoticeJpaService} and {@link UserNoticeJpaService}, and returns standardized
 * API responses.
 * <p>
 * Designed for use in the IoT backend's administration and user interfaces to
 * manage and deliver system-wide announcements and personal notifications.
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Tag(name = "09.Notice Controller")
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeJpaController {

    private final SystemNoticeJpaService noticeService;

    /**
     * Retrieves a paginated list of system notices.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return PageResult containing notice page data
     */
    @Operation(summary = "Retrieves a paginated list of system notices.")
    @GetMapping("/page")
    public PageResult<NoticePageVO> getNoticePage(
            @Parameter(description = "queryParams") NoticePageQuery queryParams) {

        IPage<NoticePageVO> result = noticeService.getNoticePage(queryParams);
        return PageResult.success(result);
    }

    /**
     * Retrieves the form data for a specific notice by ID.
     *
     * @param id the notice ID
     * @return Result containing the notice form data
     */
    @Operation(summary = "Retrieves the form data for a specific notice by ID.")
    @GetMapping("/{id}/form")
    public Result<NoticeForm> getNoticeForm(
            @Parameter(description = "notice Id") @PathVariable Long id) {

        NoticeForm formData = noticeService.getNoticeFormData(id);
        return Result.success(formData);
    }

    /**
     * Creates a new system notice.
     *
     * @param formData the notice form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Creates a new system notice.")
    @PostMapping
    public Result<Void> saveNotice(
            @Valid @RequestBody NoticeForm formData) {

        boolean result = noticeService.saveNotice(formData);
        return Result.judge(result);
    }

    /**
     * Updates an existing notice by ID.
     *
     * @param id       the notice ID
     * @param formData the notice form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Updates an existing notice by ID.")
    @PutMapping("/{id}")
    public Result<Void> updateNotice(
            @Parameter(description = "notice Id") @PathVariable Long id,
            @Valid @RequestBody NoticeForm formData) {

        boolean result = noticeService.updateNotice(id, formData);
        return Result.judge(result);
    }

    /**
     * Deletes one or more notices by IDs.
     *
     * @param ids the notice IDs, separated by commas
     * @return Result indicating success or failure
     */
    @Operation(summary = "Deletes one or more notices by IDs.")
    @DeleteMapping("/{ids}")
    public Result<Void> deleteNotices(
            @Parameter(description = "the notice IDs, separated by commas") @PathVariable String ids) {

        boolean result = noticeService.deleteNotices(ids);
        return Result.judge(result);
    }

    /**
     * Publishes a notice by ID.
     *
     * @param id the notice ID
     * @return Result indicating success or failure
     */
    @Operation(summary = "Publishes a notice by ID.")
    @PutMapping("/{id}/publish")
    public Result<Void> publishNotice(
            @Parameter(description = " notice ID") @PathVariable Long id) {

        boolean result = noticeService.publishNotice(id);
        return Result.judge(result);
    }

    /**
     * Revokes a published notice by ID.
     *
     * @param id the notice ID
     * @return Result indicating success or failure
     */
    @Operation(summary = "Revokes a published notice by ID.")
    @PutMapping("/{id}/revoke")
    public Result<Void> revokeNotice(
            @Parameter(description = "notice ID") @PathVariable Long id) {

        boolean result = noticeService.revokeNotice(id);
        return Result.judge(result);
    }

    /**
     * Retrieves the detail of a notice for reading by ID.
     *
     * @param id the notice ID
     * @return Result containing the notice detail value object
     */
    @Operation(summary = "Retrieves the detail of a notice for reading by ID.")
    @GetMapping("/{id}/detail")
    public Result<NoticeDetailVO> getNoticeDetail(
            @Parameter(description = "notice ID") @PathVariable Long id) {

        NoticeDetailVO noticeDetail = noticeService.getNoticeDetail(id);
        return Result.success(noticeDetail);
    }

    /**
     * Retrieves a paginated list of notices for the current user.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return PageResult containing user notice page data
     */
    @Operation(summary = "Retrieves a paginated list of notices for the current user")
    @GetMapping("/my-page")
    public PageResult<UserNoticePageVO> getMyNoticePage(
            @Parameter(description = "queryParams") NoticePageQuery queryParams) {

        IPage<UserNoticePageVO> result = noticeService.getMyNoticePage(queryParams);
        return PageResult.success(result);
    }
}


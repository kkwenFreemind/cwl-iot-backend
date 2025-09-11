package community.waterlevel.iot.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import community.waterlevel.iot.system.model.form.NoticeForm;
import community.waterlevel.iot.system.model.query.NoticePageQuery;
import community.waterlevel.iot.system.model.vo.NoticeDetailVO;
import community.waterlevel.iot.system.model.vo.NoticePageVO;
import community.waterlevel.iot.system.model.vo.UserNoticePageVO;

/**
 * Unified service interface for managing notice and announcement data with
 * support for multiple persistence strategies.
 * <p>
 * Provides methods for paginated queries, retrieving, saving, updating,
 * deleting, publishing, revoking, and viewing details of notices.
 * Used in notice management features to support CRUD, publishing workflow, and
 * user-specific notice retrieval.
 * </p>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface SystemNoticeJpaService {

    /**
     * Retrieves a paginated list of notice records based on query parameters.
     *
     * @param queryParams the parameters for pagination and filtering
     * @return a paginated result of notice view objects
     */
    IPage<NoticePageVO> getNoticePage(NoticePageQuery queryParams);

    /**
     * Retrieves the form data for a specific notice by its ID.
     *
     * @param id the ID of the notice
     * @return the form object containing notice data
     */
    NoticeForm getNoticeFormData(Long id);

    /**
     * Saves a new notice record.
     *
     * @param formData the form object containing notice data to save
     * @return true if the save operation was successful, false otherwise
     */
    boolean saveNotice(NoticeForm formData);

    /**
     * Updates an existing notice record by its ID.
     *
     * @param id       the ID of the notice to update
     * @param formData the form object containing updated notice data
     * @return true if the update operation was successful, false otherwise
     */
    boolean updateNotice(Long id, NoticeForm formData);

    /**
     * Deletes one or more notice records by their IDs.
     *
     * @param ids a comma-separated string of notice IDs to delete
     * @return true if the delete operation was successful, false otherwise
     */
    boolean deleteNotices(String ids);

    /**
     * Publishes a notice by its ID.
     *
     * @param id the ID of the notice to publish
     * @return true if the publish operation was successful, false otherwise
     */
    boolean publishNotice(Long id);

    /**
     * Revokes a published notice by its ID.
     *
     * @param id the ID of the notice to revoke
     * @return true if the revoke operation was successful, false otherwise
     */
    boolean revokeNotice(Long id);

    /**
     * Retrieves the detailed view object for a specific notice by its ID.
     *
     * @param id the ID of the notice
     * @return the detailed view object containing notice information
     */
    NoticeDetailVO getNoticeDetail(Long id);

    /**
     * Retrieves a paginated list of user-specific notice records based on query
     * parameters.
     *
     * @param queryParams the parameters for pagination and filtering
     * @return a paginated result of user notice view objects
     */
    IPage<UserNoticePageVO> getMyNoticePage(NoticePageQuery queryParams);
}

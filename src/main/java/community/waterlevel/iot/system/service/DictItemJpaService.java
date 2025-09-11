package community.waterlevel.iot.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import community.waterlevel.iot.system.model.form.DictItemForm;
import community.waterlevel.iot.system.model.query.DictItemPageQuery;
import community.waterlevel.iot.system.model.vo.DictItemOptionVO;
import community.waterlevel.iot.system.model.vo.DictItemPageVO;

import java.util.List;

/**
 * Service interface for managing dictionary item data and operations.
 * <p>
 * Provides methods for paginated queries, listing, retrieving, saving,
 * updating, and deleting dictionary items.
 * Used in dictionary management features to support CRUD and option selection
 * for dictionary items.
 * </p>
 *
 * @author Ray Hao
 * @since 2023/3/4
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface DictItemJpaService {

    /**
     * Retrieves a paginated list of dictionary item records based on query
     * parameters.
     *
     * @param queryParams the parameters for pagination and filtering
     * @return a paginated result of dictionary item view objects
     */
    Page<DictItemPageVO> getDictItemPage(DictItemPageQuery queryParams);

    /**
     * Retrieves a list of dictionary item options for a specific dictionary code.
     *
     * @param dictCode the code of the dictionary
     * @return a list of dictionary item option view objects
     */
    List<DictItemOptionVO> getDictItems(String dictCode);

    /**
     * Retrieves the form data for a specific dictionary item by its ID.
     *
     * @param itemId the ID of the dictionary item
     * @return the form object containing dictionary item data
     */
    DictItemForm getDictItemForm(Long itemId);

    /**
     * Saves a new dictionary item record.
     *
     * @param formData the form object containing dictionary item data to save
     * @return true if the save operation was successful, false otherwise
     */
    boolean saveDictItem(DictItemForm formData);

    /**
     * Updates an existing dictionary item record.
     *
     * @param formData the form object containing updated dictionary item data
     * @return true if the update operation was successful, false otherwise
     */
    boolean updateDictItem(DictItemForm formData);

    /**
     * Deletes one or more dictionary item records by their IDs.
     *
     * @param ids a comma-separated string of dictionary item IDs to delete
     */
    void deleteDictItemByIds(String ids);

}

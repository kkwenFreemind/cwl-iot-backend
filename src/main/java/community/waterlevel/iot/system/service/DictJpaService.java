package community.waterlevel.iot.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.system.model.form.DictForm;
import community.waterlevel.iot.system.model.query.DictPageQuery;
import community.waterlevel.iot.system.model.vo.DictPageVO;

import java.util.List;

/**
 * Service interface for managing dictionary data and operations.
 * <p>
 * Provides methods for paginated queries, listing, retrieving, saving,
 * updating, and deleting dictionary records,
 * as well as obtaining dictionary codes by IDs. Used in dictionary management
 * features to support CRUD and option selection.
 * </p>
 *
 * @author haoxr
 * @since 2022/10/12
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface DictJpaService {


    /**
     * Retrieves a paginated list of dictionary records based on query parameters.
     *
     * @param queryParams the parameters for pagination and filtering
     * @return a paginated result of dictionary view objects
     */
    Page<DictPageVO> getDictPage(DictPageQuery queryParams);

    /**
     * Retrieves a list of dictionary options for selection components.
     *
     * @return a list of dictionary options with codes
     */
    List<Option<String>> getDictList();

    /**
     * Retrieves the form data for a specific dictionary by its ID.
     *
     * @param id the ID of the dictionary
     * @return the form object containing dictionary data
     */
    DictForm getDictForm(Long id);

    /**
     * Saves a new dictionary record.
     *
     * @param dictForm the form object containing dictionary data to save
     * @return true if the save operation was successful, false otherwise
     */
    boolean saveDict(DictForm dictForm);

    /**
     * Updates an existing dictionary record by its ID.
     *
     * @param id the ID of the dictionary to update
     * @param dictForm the form object containing updated dictionary data
     * @return true if the update operation was successful, false otherwise
     */
    boolean updateDict(Long id, DictForm dictForm);

    /**
     * Deletes one or more dictionary records by their IDs.
     *
     * @param ids a list of dictionary IDs to delete
     */
    void deleteDictByIds(List<String> ids);

    /**
     * Retrieves a list of dictionary codes by their IDs.
     *
     * @param ids a list of dictionary IDs
     * @return a list of dictionary codes
     */
    List<String> getDictCodesByIds(List<String> ids);
}

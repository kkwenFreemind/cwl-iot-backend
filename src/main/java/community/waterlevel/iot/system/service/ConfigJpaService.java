package community.waterlevel.iot.system.service;

import community.waterlevel.iot.common.result.PageResult;
import community.waterlevel.iot.system.model.form.ConfigForm;
import community.waterlevel.iot.system.model.query.ConfigPageQuery;
import community.waterlevel.iot.system.model.vo.ConfigVO;

/**
 * Service interface for managing system configuration data using JPA.
 * <p>
 * Provides methods for paginated queries, retrieving, saving, updating, and
 * deleting system configuration records.
 * Extends {@link SystemConfigService} to support additional JPA-based
 * operations in system configuration management features.
 * </p>
 *
 * @author Theo
 * @since 2024-07-29 11:17:26
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
public interface ConfigJpaService extends SystemConfigService {

    /**
     * Retrieves a paginated list of system configuration records based on query
     * parameters.
     *
     * @param queryParams the parameters for pagination and filtering
     * @return a paginated result of system configuration view objects
     */
    PageResult<ConfigVO> getConfigPage(ConfigPageQuery queryParams);

    /**
     * Retrieves the form data for a specific system configuration by its ID.
     *
     * @param id the ID of the system configuration
     * @return the form object containing configuration data
     */
    ConfigForm getConfigFormData(Long id);

    /**
     * Saves a new system configuration record.
     *
     * @param configForm the form object containing configuration data to save
     * @return true if the save operation was successful, false otherwise
     */
    boolean save(ConfigForm configForm);

    /**
     * Updates an existing system configuration record by its ID.
     *
     * @param id         the ID of the system configuration to update
     * @param configForm the form object containing updated configuration data
     * @return true if the update operation was successful, false otherwise
     */
    boolean update(Long id, ConfigForm configForm);

    /**
     * Deletes a system configuration record by its ID.
     *
     * @param id the ID of the system configuration to delete
     * @return true if the delete operation was successful, false otherwise
     */
    boolean delete(Long id);

}

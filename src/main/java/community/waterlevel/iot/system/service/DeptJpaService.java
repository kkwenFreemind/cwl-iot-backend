package community.waterlevel.iot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import community.waterlevel.iot.system.model.entity.DeptJpa;
import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.system.model.form.DeptForm;
import community.waterlevel.iot.system.model.query.DeptQuery;
import community.waterlevel.iot.system.model.vo.DeptVO;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Service interface for managing department data and operations.
 * <p>
 * Provides methods for listing, creating, updating, deleting, and retrieving
 * department records,
 * as well as generating department options for selection components. Extends
 * {@link IService} for CRUD operations.
 * </p>
 *
 * @author haoxr
 * @since 2021/8/22
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface DeptJpaService extends IService<DeptJpa> {

    /**
     * Retrieves a list of department view objects based on query parameters.
     *
     * @param queryParams the parameters for filtering departments
     * @return a list of department view objects
     */
    List<DeptVO> getDeptList(DeptQuery queryParams);

    /**
     * Retrieves a list of department options for selection components.
     * Data permission filtering is automatically applied based on user roles.
     *
     * @param specification optional specification for additional filtering
     * @return a list of department options with IDs
     */
    List<Option<Long>> listDeptOptions(Specification<DeptJpa> specification);

    /**
     * Saves a new department record.
     *
     * @param formData the form object containing department data to save
     * @return the ID of the newly created department
     */
    Long saveDept(DeptForm formData);

    /**
     * Updates an existing department record by its ID.
     *
     * @param deptId   the ID of the department to update
     * @param formData the form object containing updated department data
     * @return the ID of the updated department
     */
    Long updateDept(Long deptId, DeptForm formData);

    /**
     * Deletes one or more department records by their IDs.
     *
     * @param ids a comma-separated string of department IDs to delete
     * @return true if the delete operation was successful, false otherwise
     */
    boolean deleteByIds(String ids);

    /**
     * Retrieves the form data for a specific department by its ID.
     *
     * @param deptId the ID of the department
     * @return the form object containing department data
     */
    DeptForm getDeptForm(Long deptId);
}
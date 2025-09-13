package community.waterlevel.iot.system.controller;

import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.common.result.Result;
import community.waterlevel.iot.system.model.entity.DeptJpa;
import community.waterlevel.iot.system.model.form.DeptForm;
import community.waterlevel.iot.system.model.query.DeptQuery;
import community.waterlevel.iot.system.model.vo.DeptVO;
import community.waterlevel.iot.system.service.impl.DeptJpaServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * DeptJpaController is a REST controller that provides endpoints for managing
 * organizational departments.
 * <p>
 * It exposes APIs for listing, creating, updating, deleting, and retrieving
 * department data, including support for dropdown options and form data
 * retrieval. The controller delegates business logic to the {@link DeptJpaService}
 * and returns standardized API responses.
 * <p>
 * Designed for use in the IoT backend's administration interface to manage
 * department structures and related operations.
 *
 * @author Ray.Hao
 * @since 2020/11/6
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Tag(name = "05.Dept Controller")
@RestController
@RequestMapping("/api/v1/dept")
@RequiredArgsConstructor
public class DeptJpaController {

    /**
     * Service for department JPA operations
     */
    private final DeptJpaServiceImpl deptJpaService;

    /**
     * 部門列表
     * Get the list of departments.
     *
     * @param queryParams Query parameters for filtering departments
     * @return Result containing the list of department value objects
     */
    @Operation(summary = "Get the list of departments")
    @GetMapping
    public Result<List<DeptVO>> getDeptList(DeptQuery queryParams) {
        List<DeptVO> list = deptJpaService.getDeptList(queryParams);
        return Result.success(list);
    }

    /**
     * 部門下拉選項
     * Get department dropdown options.
     *
     * @return Result containing the list of department options
     */
    @Operation(summary = "Get department dropdown options.")
    @GetMapping("/options")
    public Result<List<Option<Long>>> listDeptOptions() {
        // Pass an empty Specification to enable AOP data permission filtering
        Specification<DeptJpa> emptySpec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        List<Option<Long>> list = deptJpaService.listDeptOptions(emptySpec);
        return Result.success(list);
    }

    /**
     * 部門表單數據
     * Get department form data by department ID.
     *
     * @param deptId Department ID
     * @return Result containing department form data
     */
    @Operation(summary = "Get department form data by department ID")
    @GetMapping("/{deptId}/form")
    public Result<DeptForm> getDeptForm(
            @Parameter(description = "Department ID") @PathVariable Long deptId) {
        DeptForm formData = deptJpaService.getDeptForm(deptId);
        return Result.success(formData);
    }

    /**
     * 新增部門
     * Create a new department.
     *
     * @param formData Department form data
     * @return Result containing the new department ID
     */
    @Operation(summary = "Create a new department.")
    @PostMapping
    public Result<Long> saveDept(@Valid @RequestBody DeptForm formData) {
        Long deptId = deptJpaService.saveDept(formData);
        return Result.success(deptId);
    }

    /**
     * 修改部門
     * Update an existing department by ID.
     *
     * @param deptId   Department ID
     * @param formData Department form data
     * @return Result containing the updated department ID
     */
    @Operation(summary = "Update an existing department by ID.")
    @PutMapping("/{deptId}")
    public Result<Long> updateDept(
            @Parameter(description = "Department ID") @PathVariable Long deptId,
            @Valid @RequestBody DeptForm formData) {
        Long id = deptJpaService.updateDept(deptId, formData);
        return Result.success(id);
    }

    /**
     * 刪除部門
     * Delete one or more departments by IDs.
     *
     * @param ids Department IDs, separated by commas
     * @return Result indicating success or failure
     */
    @Operation(summary = "Delete one or more departments by ID")
    @DeleteMapping("/{ids}")
    public Result<Void> deleteByIds(
            @Parameter(description = "Department IDs, separated by commas") @PathVariable String ids) {
        deptJpaService.deleteByIds(ids);
        return Result.success();
    }
}

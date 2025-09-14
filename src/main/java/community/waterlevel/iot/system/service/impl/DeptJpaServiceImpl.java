package community.waterlevel.iot.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import community.waterlevel.iot.common.annotation.DataPermission;
import community.waterlevel.iot.common.constant.SystemConstants;
import community.waterlevel.iot.common.enums.StatusEnum;
import community.waterlevel.iot.common.exception.BusinessException;
import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.core.security.util.SecurityUtils;
import community.waterlevel.iot.system.converter.DeptJpaConverter;
import community.waterlevel.iot.system.model.entity.DeptJpa;
import community.waterlevel.iot.system.model.form.DeptForm;
import community.waterlevel.iot.system.model.query.DeptQuery;
import community.waterlevel.iot.system.model.vo.DeptVO;
import community.waterlevel.iot.system.repository.DeptJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the department service business logic.
 * <p>
 * Provides methods for managing department entities, including CRUD operations,
 * tree building,
 * option listing, and validation. Integrates with JPA repositories and
 * converters for entity transformation.
 * </p>
 *
 * @author Ray
 * @since 2021/08/22
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */

@Service("deptJpaService")
@RequiredArgsConstructor
public class DeptJpaServiceImpl {

    private final DeptJpaRepository deptJpaRepository;
    private final DeptJpaConverter deptJpaConverter;

    /**
     * Retrieves a list of departments based on the provided query parameters,
     * supporting filtering and sorting.
     * Data permission is automatically applied based on user roles and department access.
     *
     * @param queryParams the query parameters for filtering departments
     * @return a hierarchical list of department view objects
     */
    @DataPermission(deptIdColumnName = "id")
    public List<DeptVO> getDeptList(DeptQuery queryParams) {

        Specification<DeptJpa> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StrUtil.isNotBlank(queryParams.getKeywords())) {
                predicates.add(
                        criteriaBuilder.like(root.get("name"), "%" + queryParams.getKeywords() + "%"));
            }

            if (queryParams.getStatus() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("status"), queryParams.getStatus()));
            }

            // Note: Data permission filtering is automatically applied by @DataPermission annotation
            // No manual role-based filtering code needed here

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by("sort").ascending().and(Sort.by("id").ascending());
        List<DeptJpa> deptList = deptJpaRepository.findAll(spec, sort);

        List<DeptVO> deptVOList = deptList.stream()
                .map(deptJpaConverter::toVo)
                .collect(Collectors.toList());

        return buildDeptTree(deptVOList);
    }

    /**
     * Retrieves a list of department options for selection components.
     * Data permission is automatically applied based on user roles and department access.
     *
     * @param specification optional specification for additional filtering
     * @return a list of department options
     */
    public List<Option<Long>> listDeptOptions(Specification<DeptJpa> specification) {
        // Check current user's data scope
        Integer dataScope = SecurityUtils.getDataScope();
        Long currentUserDeptId = SecurityUtils.getDeptId();
        
        // For DEPT data scope (value = 3), we need special handling to include parent departments
        // for proper tree structure
        if (dataScope != null && dataScope == 3 && currentUserDeptId != null) {
            return listDeptOptionsForDeptScope(specification, currentUserDeptId);
        }
        
        // For other data scopes, use the normal data permission filtering
        return listDeptOptionsWithDataPermission(specification);
    }
    
    /**
     * Lists department options with data permission filtering applied.
     * Used for data scopes other than DEPT (3).
     */
    @DataPermission(deptIdColumnName = "id")
    private List<Option<Long>> listDeptOptionsWithDataPermission(Specification<DeptJpa> specification) {
        // Create base specification for enabled departments
        Specification<DeptJpa> baseSpec = (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("status"), StatusEnum.ENABLE.getValue());
        };

        // Combine with input specification if provided
        Specification<DeptJpa> finalSpec = specification != null ?
            baseSpec.and(specification) : baseSpec;

        // Retrieve filtered departments and convert to VO
        List<DeptJpa> deptList = deptJpaRepository.findAll(finalSpec, Sort.by("sort").ascending().and(Sort.by("id").ascending()));

        List<DeptVO> deptVOList = deptList.stream()
                .map(deptJpaConverter::toVo)
                .collect(Collectors.toList());

        // Build tree structure of DeptVO
        List<DeptVO> tree = buildDeptTree(deptVOList);

        // Convert DeptVO tree to Option<Long> tree
        List<Option<Long>> options = new ArrayList<>();
        for (DeptVO rootDept : tree) {
            options.add(convertDeptVoToOption(rootDept));
        }

        return options;
    }

    /**
     * Recursively convert DeptVO to Option<Long> including children
     */
    private Option<Long> convertDeptVoToOption(DeptVO dept) {
        Option<Long> option = new Option<>(dept.getId(), dept.getName());
        if (dept.getChildren() != null && !dept.getChildren().isEmpty()) {
            List<Option<Long>> children = new ArrayList<>();
            for (DeptVO child : dept.getChildren()) {
                children.add(convertDeptVoToOption(child));
            }
            option.setChildren(children);
        }
        return option;
    }

    /**
     * Lists department options for users with DEPT data scope (value = 3).
     * This method includes the user's department and all parent departments needed 
     * for proper tree structure, plus any child departments.
     */
    private List<Option<Long>> listDeptOptionsForDeptScope(Specification<DeptJpa> specification, Long currentUserDeptId) {
        // Get the current user's department
        DeptJpa currentUserDept = deptJpaRepository.findById(currentUserDeptId)
            .orElse(null);
        
        if (currentUserDept == null) {
            return new ArrayList<>();
        }
        
        // Collect all department IDs that the user should see
        Set<Long> allowedDeptIds = new HashSet<>();
        
        // Add user's department
        allowedDeptIds.add(currentUserDeptId);
        
        // Add all parent departments by parsing tree_path
        String treePath = currentUserDept.getTreePath();
        if (StrUtil.isNotBlank(treePath)) {
            String[] pathIds = treePath.split(",");
            for (String pathId : pathIds) {
                try {
                    Long id = Long.parseLong(pathId.trim());
                    if (!id.equals(SystemConstants.ROOT_NODE_ID)) {
                        allowedDeptIds.add(id);
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid numbers
                }
            }
        }
        
        // Add child departments (departments where parent_id = currentUserDeptId)
        List<DeptJpa> childDepts = deptJpaRepository.findByParentIdOrderBySort(currentUserDeptId);
        childDepts.stream()
            .filter(child -> child.getStatus().equals(StatusEnum.ENABLE.getValue()))
            .forEach(child -> allowedDeptIds.add(child.getId()));
        
        // Create specification to filter by allowed department IDs and enabled status
        Specification<DeptJpa> deptScopeSpec = (root, query, criteriaBuilder) -> {
            Predicate enabledPredicate = criteriaBuilder.equal(root.get("status"), StatusEnum.ENABLE.getValue());
            Predicate allowedIdsPredicate = root.get("id").in(allowedDeptIds);
            return criteriaBuilder.and(enabledPredicate, allowedIdsPredicate);
        };
        
        // Combine with input specification if provided
        Specification<DeptJpa> finalSpec = specification != null ?
            deptScopeSpec.and(specification) : deptScopeSpec;

        // Retrieve filtered departments and convert to VO
        List<DeptJpa> deptList = deptJpaRepository.findAll(finalSpec, Sort.by("sort").ascending().and(Sort.by("id").ascending()));

        List<DeptVO> deptVOList = deptList.stream()
                .map(deptJpaConverter::toVo)
                .collect(Collectors.toList());

        // Build tree structure of DeptVO
        List<DeptVO> tree = buildDeptTree(deptVOList);

        // Convert DeptVO tree to Option<Long> tree
        List<Option<Long>> options = new ArrayList<>();
        for (DeptVO rootDept : tree) {
            options.add(convertDeptVoToOption(rootDept));
        }

        return options;
    }

    /**
     * Retrieves the department form data for a specific department ID.
     *
     * @param deptId the unique identifier of the department
     * @return the department form data
     */
    public DeptForm getDeptForm(Long deptId) {
        DeptJpa dept = deptJpaRepository.findById(deptId)
                .orElseThrow(() -> new BusinessException("Department does not exist"));
        return deptJpaConverter.toForm(dept);
    }

    /**
     * Saves a new department or updates an existing one based on the provided form
     * data.
     *
     * @param deptForm the department form data
     * @return the ID of the saved or updated department
     */
    @Transactional
    public Long saveDept(DeptForm deptForm) {
        Long deptId = deptForm.getId();
        DeptJpa dept;

        if (deptId != null) {

            dept = deptJpaRepository.findById(deptId)
                    .orElseThrow(() -> new BusinessException("Department does not exist"));

            String code = deptForm.getCode();
            if (StrUtil.isNotBlank(code) &&
                    deptJpaRepository.existsByCodeAndIdNot(code, deptId)) {
                throw new BusinessException("Department code already exists");
            }

            dept.setName(deptForm.getName());
            dept.setCode(deptForm.getCode());
            dept.setParentId(deptForm.getParentId());
            dept.setStatus(deptForm.getStatus());
            dept.setSort(deptForm.getSort());
            dept.setUpdateTime(LocalDateTime.now());
        } else {
            dept = deptJpaConverter.toEntity(deptForm);

            String code = deptForm.getCode();
            if (StrUtil.isNotBlank(code) &&
                    deptJpaRepository.existsByCode(code)) {
                throw new BusinessException("Department code already exists");
            }

            dept.setCreateTime(LocalDateTime.now());
            dept.setUpdateTime(LocalDateTime.now());
        }

        String treePath = generateTreePath(dept.getParentId());
        dept.setTreePath(treePath);

        dept = deptJpaRepository.save(dept);
        return dept.getId();
    }

    /**
     * Updates an existing department with the provided form data.
     *
     * @param deptId   the unique identifier of the department
     * @param deptForm the department form data
     * @return the ID of the updated department
     */
    @Transactional
    public Long updateDept(Long deptId, DeptForm deptForm) {
        deptForm.setId(deptId);
        return saveDept(deptForm);
    }

    /**
     * Deletes departments by a comma-separated string of department IDs.
     *
     * @param ids a comma-separated string of department IDs
     * @return true if the operation was successful, false otherwise
     */
    @Transactional
    public boolean deleteByIds(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "The deleted department ID cannot be empty");

        List<Long> deptIds = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toList());

        for (Long deptId : deptIds) {

            boolean hasChildren = deptJpaRepository.existsByParentId(deptId);
            if (hasChildren) {
                throw new BusinessException("There are sub-departments under the department and it cannot be deleted.");
            }

            deptJpaRepository.deleteById(deptId);
        }

        return true;
    }

    /**
     * Builds a hierarchical tree of department view objects from a flat list.
     *
     * @param deptList the flat list of department view objects
     * @return a hierarchical list of department view objects
     */
    private List<DeptVO> buildDeptTree(List<DeptVO> deptList) {
        if (CollectionUtil.isEmpty(deptList)) {
            return new ArrayList<>();
        }

        Map<Long, List<DeptVO>> parentChildrenMap = deptList.stream()
                .filter(dept -> dept.getParentId() != null)
                .collect(Collectors.groupingBy(DeptVO::getParentId));

        for (DeptVO dept : deptList) {
            List<DeptVO> children = parentChildrenMap.get(dept.getId());
            if (children != null) {
                dept.setChildren(children);
            }
        }

        return deptList.stream()
                .filter(dept -> dept.getParentId() == null || dept.getParentId().equals(SystemConstants.ROOT_NODE_ID))
                .collect(Collectors.toList());
    }

    /**
     * Generates the tree path string for a department based on its parent ID.
     *
     * @param parentId the parent department ID
     * @return the tree path string
     */
    private String generateTreePath(Long parentId) {
        if (parentId == null || parentId.equals(SystemConstants.ROOT_NODE_ID)) {
            return SystemConstants.ROOT_NODE_ID.toString();
        }

        Optional<DeptJpa> parentDept = deptJpaRepository.findById(parentId);
        if (parentDept.isPresent()) {
            String parentTreePath = parentDept.get().getTreePath();
            return parentTreePath + "," + parentId;
        }

        return SystemConstants.ROOT_NODE_ID.toString();
    }
}

package community.waterlevel.iot.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
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
     * Role-based filtering:
     * - System Administrator: Can see all departments
     * - Other roles: Can only see their own department and child departments
     *
     * @param queryParams the query parameters for filtering departments
     * @return a hierarchical list of department view objects
     */
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

            // Add role-based filtering
            Set<String> roles = SecurityUtils.getRoles();
            boolean isSystemAdmin = roles != null && roles.contains("System_Admin");
            boolean isCommunityAdmin = roles != null && roles.contains("Community_Admin");
            
            if (!isSystemAdmin) {
                // Non-System Administrator users need department filtering
                Long currentUserDeptId = SecurityUtils.getDeptId();
                
                if (isCommunityAdmin && currentUserDeptId == null) {
                    // Community Admin with no specific department sees all community departments (non-root)
                    predicates.add(criteriaBuilder.and(
                        criteriaBuilder.isNotNull(root.get("parentId")),
                        criteriaBuilder.greaterThan(root.get("parentId"), 0L)
                    ));
                } else if (currentUserDeptId != null) {
                    // Get current user's department tree path
                    Optional<DeptJpa> currentDeptOpt = deptJpaRepository.findById(currentUserDeptId);
                    if (currentDeptOpt.isPresent()) {
                        DeptJpa currentDept = currentDeptOpt.get();
                        String currentTreePath = currentDept.getTreePath() + "," + currentDept.getId();
                        
                        // Filter departments: current department or child departments
                        Predicate currentDeptPredicate = criteriaBuilder.equal(root.get("id"), currentUserDeptId);
                        Predicate childDeptsPredicate = criteriaBuilder.like(root.get("treePath"), currentTreePath + "%");
                        
                        predicates.add(criteriaBuilder.or(currentDeptPredicate, childDeptsPredicate));
                    } else {
                        // If current department not found, show no departments
                        predicates.add(criteriaBuilder.equal(root.get("id"), -1L));
                    }
                } else {
                    // If user has no department, show no departments
                    predicates.add(criteriaBuilder.equal(root.get("id"), -1L));
                }
            }

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
     * Role-based filtering:
     * - System Administrator: Can see all departments
     * - Other roles: Can only see their own department and child departments
     *
     * @return a hierarchical list of department options
     */
    public List<Option<Long>> listDeptOptions() {
        List<DeptJpa> deptList;
        
        // Check user roles
        Set<String> roles = SecurityUtils.getRoles();
        Long currentUserDeptId = SecurityUtils.getDeptId();
        boolean isSystemAdmin = roles != null && roles.contains("System_Admin");
        boolean isCommunityAdmin = roles != null && roles.contains("Community_Admin");
        
        if (isSystemAdmin) {
            // System Administrator can see all departments
            deptList = deptJpaRepository.findByStatus(StatusEnum.ENABLE.getValue());
        } else if (isCommunityAdmin || currentUserDeptId != null) {
            // Community Admin and users with departments can see their department tree
            if (currentUserDeptId == null) {
                // If Community Admin has no specific department, show all community departments (non-root)
                deptList = deptJpaRepository.findByStatus(StatusEnum.ENABLE.getValue())
                        .stream()
                        .filter(dept -> dept.getParentId() != null && dept.getParentId() > 0) // Exclude root department
                        .collect(Collectors.toList());
            } else {
                // Show user's department and its children
                
                // Get current user's department
                Optional<DeptJpa> currentDeptOpt = deptJpaRepository.findById(currentUserDeptId);
                if (!currentDeptOpt.isPresent()) {
                    return new ArrayList<>();
                }
                
                DeptJpa currentDept = currentDeptOpt.get();
                String currentTreePath = currentDept.getTreePath() + "," + currentDept.getId();
                
                // Find all departments that are children of current department or the current department itself
                List<DeptJpa> allDepts = deptJpaRepository.findByStatus(StatusEnum.ENABLE.getValue());
                
                deptList = allDepts.stream()
                        .filter(dept -> {
                            // Include current department
                            if (dept.getId().equals(currentUserDeptId)) {
                                return true;
                            }
                            // Include child departments
                            String deptTreePath = dept.getTreePath();
                            return deptTreePath != null && deptTreePath.startsWith(currentTreePath);
                        })
                        .collect(Collectors.toList());
            }
        } else {
            // Users with no department and no Community Admin role get empty list
            return new ArrayList<>();
        }

        List<Option<Long>> optionList = deptList.stream()
                .map(dept -> new Option<>(dept.getId(), dept.getName()))
                .collect(Collectors.toList());

        return optionList;
    }

    /**
     * Builds a hierarchical tree of department options from a flat list.
     *
     * @param optionList the flat list of department options
     * @param deptList   the list of department entities
     * @return a hierarchical list of department options
     */
    private List<Option<Long>> buildDeptOptionTree(List<Option<Long>> optionList, List<DeptJpa> deptList) {
        if (CollectionUtil.isEmpty(optionList)) {
            return new ArrayList<>();
        }

        Map<Long, DeptJpa> deptMap = deptList.stream()
                .collect(Collectors.toMap(DeptJpa::getId, dept -> dept));

        Map<Long, List<Option<Long>>> parentChildrenMap = new HashMap<>();

        for (Option<Long> option : optionList) {
            DeptJpa dept = deptMap.get(option.getValue());
            Long parentId = dept.getParentId();

            if (parentId != null && !parentId.equals(SystemConstants.ROOT_NODE_ID)) {
                parentChildrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(option);
            }
        }

        for (Option<Long> option : optionList) {
            List<Option<Long>> children = parentChildrenMap.get(option.getValue());
            if (children != null) {
                option.setChildren(children);
            }
        }

        return optionList.stream()
                .filter(option -> {
                    DeptJpa dept = deptMap.get(option.getValue());
                    Long parentId = dept.getParentId();
                    return parentId == null || parentId.equals(SystemConstants.ROOT_NODE_ID);
                })
                .collect(Collectors.toList());
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

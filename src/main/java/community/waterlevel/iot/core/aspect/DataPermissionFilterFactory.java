package community.waterlevel.iot.core.aspect;

import cn.hutool.core.util.StrUtil;
import community.waterlevel.iot.common.annotation.DataPermission;
import community.waterlevel.iot.core.security.util.SecurityUtils;
import community.waterlevel.iot.system.repository.DeptJpaRepository;
import community.waterlevel.iot.system.model.entity.DeptJpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Factory class for creating data permission filters based on different data scopes.
 * Provides centralized logic for applying data access restrictions to JPA queries
 * based on user roles and department hierarchy.
 *
 * @author Chang Xiu-Wen
 * @since 2025/09/14
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataPermissionFilterFactory {

    private final DeptJpaRepository deptJpaRepository;

    /**
     * Creates data permission filter predicate based on the given data scope and permission configuration
     *
     * @param root the entity root for query building
     * @param criteriaBuilder the criteria builder for creating predicates
     * @param dataPermission the data permission annotation configuration
     * @param dataScope the user's data scope level
     * @return predicate for data filtering, or null if no filtering needed
     */
    public Predicate createDataPermissionFilter(Root<?> root, 
                                               CriteriaBuilder criteriaBuilder,
                                               DataPermission dataPermission, 
                                               Integer dataScope) {
        
        log.info("Creating data permission filter for dataScope: {}", dataScope);

        // If no user context (null userId and deptId), skip filtering for testing/development
        Long currentUserId = SecurityUtils.getUserId();
        Long currentUserDeptId = SecurityUtils.getDeptId();
        
        if (currentUserId == null && currentUserDeptId == null) {
            log.info("No user authentication context found, skipping data permission filtering for testing");
            return null;
        }
        
        log.info("Current user ID: {}, Current user dept ID: {}", currentUserId, currentUserDeptId);

        if (dataScope == null) {
            log.info("DataScope is null, no filtering applied");
            return null;
        }

        switch (dataScope) {
            case 1: // ALL - System administrators can see all data
                log.info("ALL data scope - no filtering applied");
                return null;
                
            case 2: // DEPT_AND_SUB - Department and sub-department data
                return createDeptAndSubDeptFilter(root, criteriaBuilder, dataPermission, currentUserDeptId);
                
            case 3: // DEPT - Department data only
                return createDeptOnlyFilter(root, criteriaBuilder, dataPermission, currentUserDeptId);
                
            case 4: // SELF - Self data only
                return createSelfOnlyFilter(root, criteriaBuilder, dataPermission, currentUserId);
                
            default:
                log.warn("Unknown data scope: {}, applying SELF filter as default", dataScope);
                return createSelfOnlyFilter(root, criteriaBuilder, dataPermission, currentUserId);
        }
    }

    /**
     * Creates filter for department and sub-department data access
     */
    private Predicate createDeptAndSubDeptFilter(Root<?> root, 
                                                CriteriaBuilder criteriaBuilder,
                                                DataPermission dataPermission, 
                                                Long currentUserDeptId) {
        
        if (currentUserDeptId == null) {
            log.debug("No department ID found for DEPT_AND_SUB scope, returning false predicate");
            return criteriaBuilder.equal(criteriaBuilder.literal(1), 0); // Always false
        }

        String deptIdColumn = getDeptIdColumnName(dataPermission);
        
        try {
            // Get current department and all sub-departments
            List<Long> deptIds = getDeptAndSubDeptIds(currentUserDeptId);
            log.debug("DEPT_AND_SUB filter - including departments: {}", deptIds);
            
            return root.get(deptIdColumn).in(deptIds);
            
        } catch (Exception e) {
            log.error("Error creating DEPT_AND_SUB filter", e);
            // Fallback to department only
            return criteriaBuilder.equal(root.get(deptIdColumn), currentUserDeptId);
        }
    }

    /**
     * Creates filter for department data only
     */
    private Predicate createDeptOnlyFilter(Root<?> root, 
                                          CriteriaBuilder criteriaBuilder,
                                          DataPermission dataPermission, 
                                          Long currentUserDeptId) {
        
        if (currentUserDeptId == null) {
            log.info("No department ID found for DEPT scope, returning false predicate");
            return criteriaBuilder.equal(criteriaBuilder.literal(1), 0); // Always false
        }

        String deptIdColumn = getDeptIdColumnName(dataPermission);
        log.info("DEPT filter - restricting to department: {} using column: {}", currentUserDeptId, deptIdColumn);
        
        Predicate deptFilter = criteriaBuilder.equal(root.get(deptIdColumn), currentUserDeptId);
        log.info("Created DEPT filter predicate for department ID: {}", currentUserDeptId);
        
        return deptFilter;
    }

    /**
     * Creates filter for self data only
     */
    private Predicate createSelfOnlyFilter(Root<?> root, 
                                          CriteriaBuilder criteriaBuilder,
                                          DataPermission dataPermission, 
                                          Long currentUserId) {
        
        if (currentUserId == null) {
            log.debug("No user ID found for SELF scope, returning false predicate");
            return criteriaBuilder.equal(criteriaBuilder.literal(1), 0); // Always false
        }

        String userIdColumn = getUserIdColumnName(dataPermission);
        log.debug("SELF filter - restricting to user: {} using column: {}", currentUserId, userIdColumn);
        
        return criteriaBuilder.equal(root.get(userIdColumn), currentUserId);
    }

    /**
     * Gets the department ID column name from annotation or uses default
     */
    private String getDeptIdColumnName(DataPermission dataPermission) {
        String columnName = dataPermission.deptIdColumnName();
        if (StrUtil.isBlank(columnName)) {
            columnName = "deptId"; // Default JPA field name
        }
        return columnName;
    }

    /**
     * Gets the user ID column name from annotation or uses default
     */
    private String getUserIdColumnName(DataPermission dataPermission) {
        String columnName = dataPermission.userIdColumnName();
        if (StrUtil.isBlank(columnName)) {
            columnName = "createBy"; // Default audit field
        }
        return columnName;
    }

    /**
     * Gets current department and all its sub-departments
     */
    private List<Long> getDeptAndSubDeptIds(Long deptId) {
        List<Long> deptIds = new ArrayList<>();
        
        // Add current department
        deptIds.add(deptId);
        
        // Get current department details
        Optional<DeptJpa> currentDeptOpt = deptJpaRepository.findById(deptId);
        if (currentDeptOpt.isPresent()) {
            DeptJpa currentDept = currentDeptOpt.get();
            String currentTreePath = currentDept.getTreePath() + "," + currentDept.getId();
            
            // Find all sub-departments
            List<DeptJpa> allDepts = deptJpaRepository.findAll();
            for (DeptJpa dept : allDepts) {
                if (dept.getTreePath() != null && 
                    dept.getTreePath().startsWith(currentTreePath) && 
                    !dept.getId().equals(deptId)) {
                    deptIds.add(dept.getId());
                }
            }
        }
        
        return deptIds;
    }
}
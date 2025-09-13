package community.waterlevel.iot.core.aspect;

import community.waterlevel.iot.common.annotation.DataPermission;
import community.waterlevel.iot.common.enums.DataScopeEnum;
import community.waterlevel.iot.core.security.util.SecurityUtils;
import community.waterlevel.iot.system.service.RoleJpaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Predicate;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Data Permission AOP Aspect for automatic data filtering based on user roles and data scope.
 * Intercepts methods annotated with @DataPermission and applies appropriate data filters
 * to ensure users only access data within their permitted scope.
 * 
 * Supports different data scopes:
 * - ALL: System administrators can see all data
 * - DEPT_AND_SUB: Department and sub-department data
 * - DEPT: Department data only
 * - SELF: Self data only
 *
 * @author Chang Xiu-Wen
 * @since 2025/09/14
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DataPermissionAspect {

    private final RoleJpaService roleJpaService;
    private final DataPermissionFilterFactory filterFactory;

    /**
     * Pointcut for methods annotated with @DataPermission
     */
    @Pointcut("@annotation(community.waterlevel.iot.common.annotation.DataPermission)")
    public void dataPermissionPointcut() {}

    /**
     * Pointcut for classes annotated with @DataPermission
     */
    @Pointcut("@within(community.waterlevel.iot.common.annotation.DataPermission)")
    public void dataPermissionClassPointcut() {}

    /**
     * Around advice that intercepts method calls and applies data permission filtering
     *
     * @param joinPoint the proceeding join point
     * @return the result of the method execution with applied data filters
     * @throws Throwable if method execution fails
     */
    @Around("dataPermissionPointcut() || dataPermissionClassPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Data permission aspect triggered for method: {}", joinPoint.getSignature().getName());

        // Skip filtering for super administrators
        if (SecurityUtils.isRoot()) {
            log.info("Super administrator detected, skipping data permission filtering");
            return joinPoint.proceed();
        }

        // Get @DataPermission annotation
        DataPermission dataPermission = getDataPermissionAnnotation(joinPoint);
        if (dataPermission == null) {
            log.info("No @DataPermission annotation found, proceeding without filtering");
            return joinPoint.proceed();
        }

        // Check if this is a query method that should be filtered
        if (!isQueryMethod(joinPoint)) {
            log.info("Not a query method, proceeding without filtering");
            return joinPoint.proceed();
        }

        // Apply data permission filtering
        return applyDataPermissionFilter(joinPoint, dataPermission);
    }

    /**
     * Gets the @DataPermission annotation from method or class
     */
    private DataPermission getDataPermissionAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Check method-level annotation first
        DataPermission annotation = method.getAnnotation(DataPermission.class);
        if (annotation != null) {
            return annotation;
        }

        // Check class-level annotation
        return method.getDeclaringClass().getAnnotation(DataPermission.class);
    }

    /**
     * Checks if the method is a query method that should be filtered
     */
    private boolean isQueryMethod(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Checking if method '{}' is a query method", methodName);
        
        boolean isQuery = methodName.startsWith("find") || 
                         methodName.startsWith("get") || 
                         methodName.startsWith("list") || 
                         methodName.startsWith("search") ||
                         methodName.startsWith("query");
        
        log.info("Method '{}' is query method: {}", methodName, isQuery);
        return isQuery;
    }

    /**
     * Applies data permission filtering to the method execution
     */
    private Object applyDataPermissionFilter(ProceedingJoinPoint joinPoint, DataPermission dataPermission) throws Throwable {
        log.info("Applying data permission filter with deptAlias: {}, userAlias: {}", 
                 dataPermission.deptAlias(), dataPermission.userAlias());

        // Get current user's data scope
        Set<String> roleCodes = SecurityUtils.getRoles();
        Integer dataScope = roleJpaService.getMaximumDataScope(roleCodes);
        
        if (dataScope == null) {
            dataScope = DataScopeEnum.SELF.getValue();
        }

        log.info("User data scope determined: {}", dataScope);

        // Check method parameters for Specification
        Object[] args = joinPoint.getArgs();
        log.info("Checking method arguments, total count: {}", args.length);
        
        boolean foundSpecification = false;
        for (int i = 0; i < args.length; i++) {
            log.info("Argument {}: type = {}, value = {}", i, 
                    args[i] != null ? args[i].getClass().getSimpleName() : "null", args[i]);
            
            if (args[i] instanceof Specification) {
                foundSpecification = true;
                log.info("Found Specification at argument index: {}", i);
                // Wrap the existing specification with data permission filter
                Specification<?> originalSpec = (Specification<?>) args[i];
                Specification<?> filteredSpec = createFilteredSpecification(originalSpec, dataPermission, dataScope);
                args[i] = filteredSpec;
                log.info("Enhanced Specification with data permission filter");
                break;
            }
        }

        if (!foundSpecification) {
            log.warn("No Specification parameter found in method arguments - data permission filtering cannot be applied");
        }

        return joinPoint.proceed(args);
    }

    /**
     * Creates a filtered Specification that includes data permission constraints
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Specification<?> createFilteredSpecification(Specification<?> originalSpec, 
                                                        DataPermission dataPermission, 
                                                        Integer dataScope) {
        log.info("Creating filtered specification with data scope: {}", dataScope);
        
        return (Specification) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add original specification predicates
            if (originalSpec != null) {
                Predicate originalPredicate = originalSpec.toPredicate(root, query, criteriaBuilder);
                if (originalPredicate != null) {
                    predicates.add(originalPredicate);
                    log.info("Added original specification predicate");
                }
            }

            // Add data permission predicates
            log.info("Calling DataPermissionFilterFactory.createDataPermissionFilter");
            Predicate dataPermissionPredicate = filterFactory.createDataPermissionFilter(
                root, criteriaBuilder, dataPermission, dataScope);
            
            if (dataPermissionPredicate != null) {
                predicates.add(dataPermissionPredicate);
                log.info("Successfully added data permission predicate");
            } else {
                log.warn("DataPermissionFilterFactory returned null predicate");
            }

            Predicate finalPredicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            log.info("Created final predicate with {} conditions", predicates.size());
            return finalPredicate;
        };
    }
}
package community.waterlevel.iot.core.security.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import community.waterlevel.iot.common.constant.RedisConstants;
import community.waterlevel.iot.core.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import java.util.*;

/**
 * Service for permission validation in Spring Security.
 * Provides methods to check if the current user has the required permissions,
 * leveraging role-based access control and permission caching in Redis.
 * Supports wildcard permission matching and super admin bypass.
 *
 * @author haoxr
 * @since 2022/2/22
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Component("ss")
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    /**
     * Redis template for accessing role permissions cache.
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Determines whether the current logged-in user has the required permission.
     * <p>
     * Checks for super admin, retrieves user roles, and matches permissions
     * (supports wildcards).
     * </p>
     *
     * @param requiredPerm the required permission string
     * @return {@code true} if the user has the permission; {@code false} otherwise
     */
    public boolean hasPerm(String requiredPerm) {

        if (StrUtil.isBlank(requiredPerm)) {
            return false;
        }

        if (SecurityUtils.isRoot()) {
            return true;
        }

        Set<String> roleCodes = SecurityUtils.getRoles();
        if (CollectionUtil.isEmpty(roleCodes)) {
            return false;
        }

        Set<String> rolePerms = this.getRolePermsFormCache(roleCodes);
        if (CollectionUtil.isEmpty(rolePerms)) {
            return false;
        }

        boolean hasPermission = rolePerms.stream()
                .anyMatch(rolePerm -> PatternMatchUtils.simpleMatch(rolePerm, requiredPerm));

        if (!hasPermission) {
            log.error("使用者無操作許可權：{}", requiredPerm);
        }
        return hasPermission;
    }

    /**
     * Retrieves the set of permissions for the given role codes from the cache
     * (Redis).
     * <p>
     * Fetches and aggregates permissions for all specified roles, handling cache
     * misses and type checks.
     * </p>
     *
     * @param roleCodes the set of role codes
     * @return the set of permissions for the roles, or an empty set if not found
     */
    public Set<String> getRolePermsFormCache(Set<String> roleCodes) {
        log.debug("Fetching role permissions, input role codes: {}", roleCodes);

        if (CollectionUtil.isEmpty(roleCodes)) {
            log.warn("If the role code set is empty, an empty set is returned.");
            return Collections.emptySet();
        }

        Set<String> perms = new HashSet<>();

        try {
            Boolean hasKey = redisTemplate.hasKey(RedisConstants.System.ROLE_PERMS);
            log.debug("Does the permission cache key exist in Redis {}: {}", RedisConstants.System.ROLE_PERMS, hasKey);

            if (Boolean.TRUE.equals(hasKey)) {
                Set<Object> allRoleCodes = redisTemplate.opsForHash().keys(RedisConstants.System.ROLE_PERMS);
                log.info("All role codes cached in Redis: {}", allRoleCodes);
            }

            Collection<Object> roleCodesAsObjects = new ArrayList<>(roleCodes);
            log.debug("Prepare to obtain permissions from the Redis cache, cache key: {}, Role Coding: {}",
                    RedisConstants.System.ROLE_PERMS, roleCodesAsObjects);

            List<Object> rolePermsList = redisTemplate.opsForHash().multiGet(RedisConstants.System.ROLE_PERMS,
                    roleCodesAsObjects);
            log.debug("The permission list obtained from Redis: {}", rolePermsList);

            if (rolePermsList == null || rolePermsList.isEmpty()) {
                log.warn("The permission list obtained from Redis is empty or null");
                return Collections.emptySet();
            }

            int nullCount = 0;
            int validCount = 0;

            for (int i = 0; i < rolePermsList.size(); i++) {
                Object rolePermsObj = rolePermsList.get(i);
                String currentRoleCode = roleCodesAsObjects.size() > i ? roleCodesAsObjects.toArray()[i].toString()
                        : "unknown";

                if (rolePermsObj == null) {
                    nullCount++;
                    log.warn("Role {} There is no corresponding permission data in the cache", currentRoleCode);
                    continue;
                }

                log.debug("Role {} Permission object type: {}, Value: {}",
                        currentRoleCode, rolePermsObj.getClass().getName(), rolePermsObj);

                if (rolePermsObj instanceof Set) {
                    @SuppressWarnings("unchecked")
                    Set<String> rolePerms = (Set<String>) rolePermsObj;
                    log.debug("Role {}'s permission set size: {}, content: {}",
                            currentRoleCode, rolePerms.size(), rolePerms);
                    perms.addAll(rolePerms);
                    validCount++;
                } else {
                    log.warn("The permission object of Role {} is not of Set type, actual type: {}, value: {}",
                            currentRoleCode, rolePermsObj.getClass().getName(), rolePermsObj);
                }
            }

            log.info(
                    "Permission acquisition completed - Number of valid roles: {}, Number of empty roles: {}, Final number of permissions: {}",
                    validCount, nullCount, perms.size());

        } catch (Exception e) {
            log.error("An exception occurred while retrieving role permissions from Redis.", e);
            return Collections.emptySet();
        }

        log.debug("Returned permission set: {}", perms);
        return perms;
    }

}

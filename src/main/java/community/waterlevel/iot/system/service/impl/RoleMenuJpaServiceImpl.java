package community.waterlevel.iot.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import community.waterlevel.iot.common.constant.RedisConstants;
import community.waterlevel.iot.system.model.entity.MenuJpa;
import community.waterlevel.iot.system.model.entity.RoleJpa;
import community.waterlevel.iot.system.model.bo.RolePermsBO;

import community.waterlevel.iot.system.repository.MenuJpaRepository;
import community.waterlevel.iot.system.repository.RoleJpaRepository;
import community.waterlevel.iot.system.repository.RoleMenuJpaRepository;
import community.waterlevel.iot.system.service.RoleMenuJpaService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import community.waterlevel.iot.system.model.entity.RoleMenuJpa;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of role-menu business logic and permission caching for the
 * system.
 * <p>
 * Provides services for managing role-menu relationships, permission caching in
 * Redis,
 * and permission retrieval for roles. Integrates with JPA repositories and
 * supports
 * cache refresh, permission queries, and MyBatis compatibility stubs.
 * </p>
 *
 * @author Ray.Hao
 * @since 2.5.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Service("roleMenuJpaService")
@RequiredArgsConstructor
@Slf4j
public class RoleMenuJpaServiceImpl implements RoleMenuJpaService {

    private final RoleMenuJpaRepository roleMenuJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final MenuJpaRepository menuJpaRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Initializes the role-permission cache in Redis after bean construction.
     */
    @PostConstruct
    public void initRolePermsCache() {
        log.info("Initialize the permission cache (JPA implementation)... ");
        try {
            refreshRolePermsCache();
            log.info("Permission cache initialization completed");
        } catch (Exception e) {
            log.error("Permission cache initialization failed", e);
        }
    }

    /**
     * Refreshes the permission cache for all roles in Redis.
     * Retrieves all role permissions and updates the cache accordingly.
     */
    @Override
    public void refreshRolePermsCache() {
        log.info("Start refreshing the permission cache for all roles...");
        try {
            redisTemplate.delete(RedisConstants.System.ROLE_PERMS);
            List<RolePermsBO> list = getRolePermsList(null);
            if (list != null && CollectionUtil.isNotEmpty(list)) {
                int successCount = 0;
                for (RolePermsBO item : list) {
                    String roleCode = item.getRoleCode();
                    Set<String> perms = item.getPerms();
                    if (CollectionUtil.isNotEmpty(perms)) {
                        redisTemplate.opsForHash().put(RedisConstants.System.ROLE_PERMS, roleCode, perms);
                        successCount++;
                        log.info("Successfully cached permissions for role {}", roleCode);
                    } else {
                        log.warn("Role {} has no permissions, skipping caching", roleCode);
                    }
                }
                log.info("Permission cache refresh completed, successfully cached the permissions of {} roles",
                        successCount);
            } else {
                log.warn("No role permission information was obtained, and the permission cache is empty.");
            }
            Boolean hasKey = redisTemplate.hasKey(RedisConstants.System.ROLE_PERMS);
            log.info("Does the permission cache key {} exist?: {}", RedisConstants.System.ROLE_PERMS, hasKey);
            if (Boolean.TRUE.equals(hasKey)) {
                Set<Object> cachedRoles = redisTemplate.opsForHash().keys(RedisConstants.System.ROLE_PERMS);
                log.info("List of roles in the cache: {}", cachedRoles);
            }
        } catch (Exception e) {
            log.error("An exception occurred while refreshing the permission cache", e);
            throw e;
        }
    }

    /**
     * Refreshes the permission cache for a specific role code in Redis.
     *
     * @param roleCode the role code to refresh
     */
    @Override
    public void refreshRolePermsCache(String roleCode) {
        redisTemplate.opsForHash().delete(RedisConstants.System.ROLE_PERMS, roleCode);
        List<RolePermsBO> list = getRolePermsList(roleCode);
        if (CollectionUtil.isNotEmpty(list)) {
            RolePermsBO rolePerms = list.get(0);
            if (rolePerms == null) {
                return;
            }
            Set<String> perms = rolePerms.getPerms();
            if (CollectionUtil.isNotEmpty(perms)) {
                redisTemplate.opsForHash().put(RedisConstants.System.ROLE_PERMS, roleCode, perms);
            }
        }
    }

    /**
     * Refreshes the permission cache when a role code is changed (old to new) in
     * Redis.
     *
     * @param oldRoleCode the old role code
     * @param newRoleCode the new role code
     */
    @Override
    public void refreshRolePermsCache(String oldRoleCode, String newRoleCode) {
        redisTemplate.opsForHash().delete(RedisConstants.System.ROLE_PERMS, oldRoleCode);
        List<RolePermsBO> list = getRolePermsList(newRoleCode);
        if (CollectionUtil.isNotEmpty(list)) {
            RolePermsBO rolePerms = list.get(0);
            if (rolePerms == null) {
                return;
            }
            Set<String> perms = rolePerms.getPerms();
            redisTemplate.opsForHash().put(RedisConstants.System.ROLE_PERMS, newRoleCode, perms);
        }
    }

    /**
     * Retrieves the set of permissions for the given set of role codes.
     *
     * @param roles the set of role codes
     * @return the set of permissions associated with the roles
     */
    @Override
    public Set<String> getRolePermsByRoleCodes(Set<String> roles) {
        if (CollectionUtil.isEmpty(roles)) {
            return Collections.emptySet();
        }
        List<RoleJpa> roleJpaList = roleJpaRepository.findAll();
        Set<Long> roleIds = roleJpaList.stream()
                .filter(role -> roles.contains(role.getCode()))
                .map(RoleJpa::getId)
                .collect(Collectors.toSet());
        if (CollectionUtil.isEmpty(roleIds)) {
            return Collections.emptySet();
        }
        List<Long> menuIds = roleMenuJpaRepository.findMenuIdsByRoleIds(roleIds);
        if (CollectionUtil.isEmpty(menuIds)) {
            return Collections.emptySet();
        }
        List<MenuJpa> menuList = menuJpaRepository.findAllById(menuIds);
        return menuList.stream()
                .filter(menu -> menu.getPerm() != null && !menu.getPerm().trim().isEmpty())
                .map(MenuJpa::getPerm)
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves the list of menu IDs associated with a specific role ID.
     *
     * @param roleId the role ID
     * @return the list of menu IDs for the role
     */
    @Override
    public List<Long> listMenuIdsByRoleId(Long roleId) {
        return roleMenuJpaRepository.findMenuIdsByRoleIds(Set.of(roleId));
    }

    /**
     * Not supported: Use JPA-specific save methods instead.
     *
     * @param entity the entity to save
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean save(RoleMenuJpa entity) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    /**
     * Not supported: Use JPA-specific batch save methods instead.
     *
     * @param entityList the list of entities to save
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean saveBatch(Collection<RoleMenuJpa> entityList) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    /**
     * Not supported: Use JPA-specific batch save methods instead.
     *
     * @param entityList the list of entities to save
     * @param batchSize  the batch size
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean saveBatch(Collection<RoleMenuJpa> entityList, int batchSize) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    /**
     * Not supported: Use JPA-specific saveOrUpdate methods instead.
     *
     * @param entity the entity to save or update
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean saveOrUpdate(RoleMenuJpa entity) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    /**
     * Not supported: Use JPA-specific saveOrUpdate batch methods instead.
     *
     * @param entityList the list of entities to save or update
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean saveOrUpdateBatch(Collection<RoleMenuJpa> entityList) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    /**
     * Not supported: Use JPA-specific saveOrUpdate batch methods instead.
     *
     * @param entityList the list of entities to save or update
     * @param batchSize  the batch size
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean saveOrUpdateBatch(Collection<RoleMenuJpa> entityList, int batchSize) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    /**
     * Not supported: Use JPA-specific removeById methods instead.
     *
     * @param id the ID to remove
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean removeById(Serializable id) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    /**
     * Not supported: Use JPA-specific removeByIds methods instead.
     *
     * @param idList the list of IDs to remove
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean removeByIds(Collection<?> idList) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    /**
     * Not supported: Use JPA-specific updateById methods instead.
     *
     * @param entity the entity to update
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean updateById(RoleMenuJpa entity) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    /**
     * Not supported: Use JPA-specific updateBatchById methods instead.
     *
     * @param entityList the list of entities to update
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean updateBatchById(Collection<RoleMenuJpa> entityList) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    /**
     * Not supported: Use JPA-specific updateBatchById methods instead.
     *
     * @param entityList the list of entities to update
     * @param batchSize  the batch size
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean updateBatchById(Collection<RoleMenuJpa> entityList, int batchSize) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    /**
     * Not supported: JPA implementation does not support single ID query, use
     * business method instead.
     *
     * @param id the ID to query
     * @throws UnsupportedOperationException always
     */
    @Override
    public RoleMenuJpa getById(Serializable id) {
        throw new UnsupportedOperationException(
                "JPA implementation does not support single ID query, please use business method");
    }

    /**
     * Not supported: Use JPA-specific listByIds methods instead.
     *
     * @param idList the list of IDs to query
     * @throws UnsupportedOperationException always
     */
    @Override
    public List<RoleMenuJpa> listByIds(Collection<? extends Serializable> idList) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    /**
     * Returns the total count of role-menu relationships.
     *
     * @return the total count
     */
    @Override
    public long count() {
        return roleMenuJpaRepository.count();
    }

    /**
     * Returns the total count of role-menu relationships (ignores queryWrapper).
     *
     * @param queryWrapper ignored
     * @return the total count
     */
    @Override
    public long count(Wrapper<RoleMenuJpa> queryWrapper) {
        return roleMenuJpaRepository.count();
    }

    /**
     * Not supported: Use business methods instead.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public List<RoleMenuJpa> list() {
        throw new UnsupportedOperationException("Please use business methods");
    }

    /**
     * Not supported: Use business methods instead.
     *
     * @param queryWrapper ignored
     * @throws UnsupportedOperationException always
     */
    @Override
    public List<RoleMenuJpa> list(Wrapper<RoleMenuJpa> queryWrapper) {
        throw new UnsupportedOperationException("Please use business methods");
    }

    /**
     * Not supported: Use business methods instead.
     *
     * @param page the page object
     * @throws UnsupportedOperationException always
     */
    public Page<RoleMenuJpa> page(Page<RoleMenuJpa> page) {
        throw new UnsupportedOperationException("Please use business methods");
    }

    /**
     * Not supported: Use business methods instead.
     *
     * @param page         the page object
     * @param queryWrapper ignored
     * @throws UnsupportedOperationException always
     */
    public Page<RoleMenuJpa> page(Page<RoleMenuJpa> page, Wrapper<RoleMenuJpa> queryWrapper) {
        throw new UnsupportedOperationException("Please use business methods");
    }

    /**
     * Not supported: Use business methods instead.
     *
     * @param queryWrapper ignored
     * @throws UnsupportedOperationException always
     */
    @Override
    public RoleMenuJpa getOne(Wrapper<RoleMenuJpa> queryWrapper) {
        throw new UnsupportedOperationException("Please use business methods");
    }

    /**
     * Not supported: Use business methods instead.
     *
     * @param queryWrapper ignored
     * @param throwEx      ignored
     * @throws UnsupportedOperationException always
     */
    @Override
    public RoleMenuJpa getOne(Wrapper<RoleMenuJpa> queryWrapper, boolean throwEx) {
        throw new UnsupportedOperationException("Please use business methods");
    }

    /**
     * Not supported: Use business methods instead.
     *
     * @param queryWrapper ignored
     * @throws UnsupportedOperationException always
     */
    @Override
    public Optional<RoleMenuJpa> getOneOpt(Wrapper<RoleMenuJpa> queryWrapper) {
        throw new UnsupportedOperationException("Please use business methods");
    }

    /**
     * Not supported: Use business methods instead.
     *
     * @param queryWrapper ignored
     * @param throwEx      ignored
     * @throws UnsupportedOperationException always
     */
    @Override
    public Optional<RoleMenuJpa> getOneOpt(Wrapper<RoleMenuJpa> queryWrapper, boolean throwEx) {
        throw new UnsupportedOperationException("Please use business methods");
    }

    /**
     * Not supported: Use business methods instead.
     *
     * @param queryWrapper ignored
     * @throws UnsupportedOperationException always
     */
    @Override
    public Map<String, Object> getMap(Wrapper<RoleMenuJpa> queryWrapper) {
        throw new UnsupportedOperationException("Please use business methods");
    }

    /**
     * Not supported: Use business methods instead.
     *
     * @param queryWrapper ignored
     * @param mapper       ignored
     * @throws UnsupportedOperationException always
     */
    @Override
    public <V> V getObj(Wrapper<RoleMenuJpa> queryWrapper, Function<? super Object, V> mapper) {
        throw new UnsupportedOperationException("Please use business methods");
    }

    /**
     * Not supported: JPA implementation does not support MyBatis BaseMapper.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public BaseMapper<RoleMenuJpa> getBaseMapper() {
        throw new UnsupportedOperationException("JPA implementation does not support MyBatis BaseMapper");
    }

    /**
     * Returns the entity class for RoleMenuJpa.
     *
     * @return the RoleMenuJpa class
     */
    @Override
    public Class<RoleMenuJpa> getEntityClass() {
        return RoleMenuJpa.class;
    }

    /**
     * Retrieves a list of role-permission business objects for the specified role
     * code (or all roles if null).
     *
     * @param roleCode the role code to filter by, or null for all roles
     * @return the list of RolePermsBO objects
     */
    private List<RolePermsBO> getRolePermsList(String roleCode) {
        List<RoleJpa> roles;
        if (roleCode != null) {
            Optional<RoleJpa> roleOpt = roleJpaRepository.findByCode(roleCode);
            if (roleOpt.isEmpty()) {
                return Collections.emptyList();
            }
            roles = List.of(roleOpt.get());
        } else {
            roles = roleJpaRepository.findAll();
        }
        List<RolePermsBO> result = new ArrayList<>();
        for (RoleJpa role : roles) {
            List<Long> menuIds = roleMenuJpaRepository.findMenuIdsByRoleIds(Set.of(role.getId()));
            if (CollectionUtil.isNotEmpty(menuIds)) {
                List<MenuJpa> menus = menuJpaRepository.findAllById(menuIds);
                Set<String> perms = menus.stream()
                        .filter(menu -> menu.getPerm() != null && !menu.getPerm().trim().isEmpty())
                        .map(MenuJpa::getPerm)
                        .collect(Collectors.toSet());
                if (CollectionUtil.isNotEmpty(perms)) {
                    RolePermsBO rolePermsBO = new RolePermsBO();
                    rolePermsBO.setRoleCode(role.getCode());
                    rolePermsBO.setPerms(perms);
                    result.add(rolePermsBO);
                }
            }
        }
        return result;
    }
}

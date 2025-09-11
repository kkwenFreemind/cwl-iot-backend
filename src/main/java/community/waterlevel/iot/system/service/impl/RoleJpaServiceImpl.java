package community.waterlevel.iot.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import community.waterlevel.iot.common.constant.SystemConstants;
import community.waterlevel.iot.common.exception.BusinessException;
import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.core.security.util.SecurityUtils;
import community.waterlevel.iot.system.converter.RoleJpaConverter;
import community.waterlevel.iot.system.model.entity.RoleJpa;
import community.waterlevel.iot.system.model.entity.RoleMenuJpa;
import community.waterlevel.iot.system.model.entity.RoleMenuId;

import community.waterlevel.iot.system.model.form.RoleForm;
import community.waterlevel.iot.system.model.query.RolePageQuery;
import community.waterlevel.iot.system.model.vo.RolePageVO;
import community.waterlevel.iot.system.repository.RoleJpaRepository;
import community.waterlevel.iot.system.repository.RoleMenuJpaRepository;
import community.waterlevel.iot.system.repository.UserRoleJpaRepository;
import community.waterlevel.iot.system.service.RoleJpaService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of role-related business logic and operations for the system.
 * <p>
 * Provides services for role management, including CRUD operations, pagination,
 * option listing,
 * menu assignment, status updates, and permission cache management. Integrates
 * with JPA repositories,
 * supports MyBatis compatibility, and enforces business constraints for role
 * uniqueness and assignment.
 * </p>
 *
 * @author haoxr
 * @since 2022/6/3
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Service
@Primary
@RequiredArgsConstructor
public class RoleJpaServiceImpl implements RoleJpaService {

    private final RoleJpaRepository roleJpaRepository;
    private final RoleMenuJpaRepository roleMenuJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;
    private final RoleJpaConverter roleJpaConverter;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Retrieves a paginated list of roles based on query parameters.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return a MyBatis-Plus Page containing role page view objects
     */
    @Override
    public Page<RolePageVO> getRolePage(RolePageQuery queryParams) {
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        String keywords = queryParams.getKeywords();
        Specification<RoleJpa> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StrUtil.isNotBlank(keywords)) {
                Predicate namePredicate = criteriaBuilder.like(root.get("name"), "%" + keywords + "%");
                Predicate codePredicate = criteriaBuilder.like(root.get("code"), "%" + keywords + "%");
                predicates.add(criteriaBuilder.or(namePredicate, codePredicate));
            }
            if (!SecurityUtils.isRoot()) {
                predicates.add(criteriaBuilder.notEqual(root.get("code"), SystemConstants.ROOT_ROLE_CODE));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Sort sort = Sort.by("sort").ascending()
                .and(Sort.by("createTime").descending())
                .and(Sort.by("updateTime").descending());
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        org.springframework.data.domain.Page<RoleJpa> jpaPage = roleJpaRepository.findAll(spec, pageable);
        return convertToMyBatisPage(jpaPage);
    }

    /**
     * Lists all roles as options for dropdowns, excluding the root role for
     * non-root users.
     *
     * @return the list of role options
     */
    @Override
    public List<Option<Long>> listRoleOptions() {
        Specification<RoleJpa> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!SecurityUtils.isRoot()) {
                predicates.add(criteriaBuilder.notEqual(root.get("code"), SystemConstants.ROOT_ROLE_CODE));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Sort sort = Sort.by("sort").ascending();
        List<RoleJpa> roleList = roleJpaRepository.findAll(spec, sort);
        return roleJpaConverter.toOptions(roleList);
    }

    /**
     * Saves or updates a role based on the provided form data.
     * Validates role code and name uniqueness, sets timestamps, and refreshes
     * permission cache if needed.
     *
     * @param roleForm the role form data
     * @return true if the role was saved successfully
     */
    @Override
    public boolean saveRole(RoleForm roleForm) {
        Long roleId = roleForm.getId();
        RoleJpa oldRole = null;
        if (roleId != null) {
            oldRole = roleJpaRepository.findById(roleId)
                    .orElseThrow(() -> new BusinessException("The role does not exist"));
        }
        String roleCode = roleForm.getCode();
        String roleName = roleForm.getName();
        boolean codeExists = roleId != null
                ? roleJpaRepository.existsByCodeAndIdNot(roleCode, roleId)
                : roleJpaRepository.findByCode(roleCode).isPresent();
        boolean nameExists = roleId != null
                ? roleJpaRepository.existsByNameAndIdNot(roleName, roleId)
                : roleJpaRepository.findByName(roleName).isPresent();
        Assert.isTrue(!codeExists && !nameExists,
                "The role name or role code already exists, please modify it and try again!");
        RoleJpa role = roleJpaConverter.toEntity(roleForm);
        if (roleId != null) {
            role.setId(roleId);
            role.setUpdateTime(LocalDateTime.now());
        } else {
            role.setCreateTime(LocalDateTime.now());
            role.setUpdateTime(LocalDateTime.now());
        }
        role = roleJpaRepository.save(role);
        boolean result = role.getId() != null;
        if (result && oldRole != null) {
            if (!StrUtil.equals(oldRole.getCode(), roleCode) ||
                    !ObjectUtil.equals(oldRole.getStatus(), roleForm.getStatus())) {
                refreshRolePermsCache(oldRole.getCode(), roleCode);
            }
        }
        return result;
    }

    /**
     * Retrieves the form data for a specific role by its ID.
     *
     * @param roleId the role ID
     * @return the RoleForm object
     */
    @Override
    public RoleForm getRoleForm(Long roleId) {
        RoleJpa entity = roleJpaRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("The role does not exist"));
        return roleJpaConverter.toForm(entity);
    }

    /**
     * Updates the status of a role by its ID and refreshes the permission cache.
     *
     * @param roleId the role ID
     * @param status the new status
     * @return true if the update was successful
     */
    @Override
    public boolean updateRoleStatus(Long roleId, Integer status) {
        RoleJpa role = roleJpaRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("The role does not exist"));
        role.setStatus(status);
        role.setUpdateTime(LocalDateTime.now());
        RoleJpa savedRole = roleJpaRepository.save(role);
        if (savedRole != null && savedRole.getId() != null) {
            refreshRolePermsCache(savedRole.getCode());
            return true;
        }
        return false;
    }

    /**
     * Deletes one or more roles by their IDs, ensuring they are not assigned to any
     * users.
     * Also deletes related role-menu and user-role relationships and refreshes
     * permission cache.
     *
     * @param ids a comma-separated string of role IDs to delete
     */
    @Override
    @Transactional
    public void deleteRoles(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "The deleted role ID cannot be empty");
        List<Long> roleIds = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        for (Long roleId : roleIds) {
            RoleJpa role = roleJpaRepository.findById(roleId)
                    .orElseThrow(() -> new BusinessException("The role does not exist"));
            List<Long> userIds = userRoleJpaRepository.findUserIdsByRoleId(roleId);
            Assert.isTrue(CollectionUtil.isEmpty(userIds),
                    "The role [{}] has been assigned to a user. Please unlink it before deleting it.", role.getName());
            roleJpaRepository.deleteById(roleId);
            roleMenuJpaRepository.deleteByIdRoleId(roleId);
            userRoleJpaRepository.deleteByIdRoleId(roleId);
            refreshRolePermsCache(role.getCode());
        }
    }

    /**
     * Retrieves the list of menu IDs associated with a specific role ID.
     *
     * @param roleId the role ID
     * @return the list of menu IDs for the role
     */
    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return roleMenuJpaRepository.findMenuIdsByRoleIds(Set.of(roleId));
    }

    /**
     * Assigns a list of menus to a role, replacing any existing assignments, and
     * refreshes permission cache.
     *
     * @param roleId  the role ID
     * @param menuIds the list of menu IDs to assign
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "menu", key = "'routes'")
    public void assignMenusToRole(Long roleId, List<Long> menuIds) {
        RoleJpa role = roleJpaRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("The role does not exist"));
        roleMenuJpaRepository.deleteByIdRoleId(roleId);
        if (CollectionUtil.isNotEmpty(menuIds)) {
            List<RoleMenuJpa> roleMenus = menuIds.stream()
                    .map(menuId -> {
                        RoleMenuJpa roleMenu = new RoleMenuJpa();
                        RoleMenuId id = new RoleMenuId();
                        id.setRoleId(roleId);
                        id.setMenuId(menuId);
                        roleMenu.setId(id);
                        return roleMenu;
                    })
                    .toList();
            roleMenuJpaRepository.saveAll(roleMenus);
        }
        refreshRolePermsCache(role.getCode());
    }

    /**
     * Retrieves the minimum data scope value among the specified roles with status
     * enabled.
     *
     * @param roles the set of role codes
     * @return the minimum data scope value, or null if not found
     */
    @Override
    public Integer getMaximumDataScope(Set<String> roles) {
        if (CollectionUtil.isEmpty(roles)) {
            return null;
        }
        Specification<RoleJpa> spec = (root, query, criteriaBuilder) -> {
            Predicate codePredicate = root.get("code").in(roles);
            Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), 1);
            return criteriaBuilder.and(codePredicate, statusPredicate);
        };
        List<RoleJpa> roleList = roleJpaRepository.findAll(spec);
        return roleList.stream()
                .map(RoleJpa::getDataScope)
                .filter(Objects::nonNull)
                .min(Integer::compareTo)
                .orElse(null);
    }

    @Override
    public boolean save(RoleJpa entity) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    @Override
    public boolean saveBatch(Collection<RoleJpa> entityList) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    @Override
    public boolean saveBatch(Collection<RoleJpa> entityList, int batchSize) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    @Override
    public boolean saveOrUpdate(RoleJpa entity) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<RoleJpa> entityList) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<RoleJpa> entityList, int batchSize) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    @Override
    public boolean removeById(Serializable id) {
        try {
            roleJpaRepository.deleteById((Long) id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean removeByIds(Collection<?> idList) {
        try {
            List<Long> longIds = idList.stream()
                    .map(id -> (Long) id)
                    .collect(Collectors.toList());
            roleJpaRepository.deleteAllById(longIds);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateById(RoleJpa entity) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    @Override
    public boolean updateBatchById(Collection<RoleJpa> entityList) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    @Override
    public boolean updateBatchById(Collection<RoleJpa> entityList, int batchSize) {
        throw new UnsupportedOperationException("Please use JPA-specific methods");
    }

    @Override
    public RoleJpa getById(Serializable id) {
        RoleJpa roleJpa = roleJpaRepository.findById((Long) id).orElse(null);
        if (roleJpa == null) {
            return null;
        }

        RoleJpa role = new RoleJpa();
        role.setId(roleJpa.getId());
        role.setName(roleJpa.getName());
        role.setCode(roleJpa.getCode());
        role.setSort(roleJpa.getSort());
        role.setStatus(roleJpa.getStatus());
        role.setDataScope(roleJpa.getDataScope());
        role.setCreateTime(roleJpa.getCreateTime());
        role.setUpdateTime(roleJpa.getUpdateTime());
        return role;
    }

    @Override
    public List<RoleJpa> listByIds(Collection<? extends Serializable> idList) {
        List<Long> longIds = idList.stream()
                .map(id -> (Long) id)
                .collect(Collectors.toList());

        List<RoleJpa> roleJpaList = roleJpaRepository.findAllById(longIds);
        return roleJpaList.stream()
                .map(roleJpa -> {
                    RoleJpa role = new RoleJpa();
                    role.setId(roleJpa.getId());
                    role.setName(roleJpa.getName());
                    role.setCode(roleJpa.getCode());
                    role.setSort(roleJpa.getSort());
                    role.setStatus(roleJpa.getStatus());
                    role.setDataScope(roleJpa.getDataScope());
                    role.setCreateTime(roleJpa.getCreateTime());
                    role.setUpdateTime(roleJpa.getUpdateTime());
                    return role;
                })
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return roleJpaRepository.count();
    }

    @Override
    public long count(Wrapper<RoleJpa> queryWrapper) {
        return roleJpaRepository.count();
    }

    @Override
    public List<RoleJpa> list() {
        List<RoleJpa> roleJpaList = roleJpaRepository.findAll();
        return roleJpaList.stream()
                .map(roleJpa -> {
                    RoleJpa role = new RoleJpa();
                    role.setId(roleJpa.getId());
                    role.setName(roleJpa.getName());
                    role.setCode(roleJpa.getCode());
                    role.setSort(roleJpa.getSort());
                    role.setStatus(roleJpa.getStatus());
                    role.setDataScope(roleJpa.getDataScope());
                    role.setCreateTime(roleJpa.getCreateTime());
                    role.setUpdateTime(roleJpa.getUpdateTime());
                    return role;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleJpa> list(Wrapper<RoleJpa> queryWrapper) {
        return list();
    }

    public Page<RoleJpa> page(Page<RoleJpa> page) {
        Pageable pageable = PageRequest.of((int) page.getCurrent() - 1, (int) page.getSize());
        org.springframework.data.domain.Page<RoleJpa> jpaPage = roleJpaRepository.findAll(pageable);

        Page<RoleJpa> result = new Page<>(page.getCurrent(), page.getSize(), jpaPage.getTotalElements());
        List<RoleJpa> records = jpaPage.getContent().stream()
                .map(roleJpa -> {
                    RoleJpa role = new RoleJpa();
                    role.setId(roleJpa.getId());
                    role.setName(roleJpa.getName());
                    role.setCode(roleJpa.getCode());
                    role.setSort(roleJpa.getSort());
                    role.setStatus(roleJpa.getStatus());
                    role.setDataScope(roleJpa.getDataScope());
                    role.setCreateTime(roleJpa.getCreateTime());
                    role.setUpdateTime(roleJpa.getUpdateTime());
                    return role;
                })
                .collect(Collectors.toList());
        result.setRecords(records);
        return result;
    }

    public Page<RoleJpa> page(Page<RoleJpa> page, Wrapper<RoleJpa> queryWrapper) {
        return page(page);
    }

    @Override
    public RoleJpa getOne(Wrapper<RoleJpa> queryWrapper) {
        List<RoleJpa> list = list(queryWrapper);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public RoleJpa getOne(Wrapper<RoleJpa> queryWrapper, boolean throwEx) {
        RoleJpa one = getOne(queryWrapper);
        if (throwEx && one == null) {
            throw new BusinessException("The query result is empty");
        }
        return one;
    }

    @Override
    public Optional<RoleJpa> getOneOpt(Wrapper<RoleJpa> queryWrapper) {
        return Optional.ofNullable(getOne(queryWrapper));
    }

    @Override
    public Optional<RoleJpa> getOneOpt(Wrapper<RoleJpa> queryWrapper, boolean throwEx) {
        return Optional.ofNullable(getOne(queryWrapper, throwEx));
    }

    @Override
    public Map<String, Object> getMap(Wrapper<RoleJpa> queryWrapper) {
        RoleJpa role = getOne(queryWrapper);
        if (role == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", role.getId());
        map.put("name", role.getName());
        map.put("code", role.getCode());
        map.put("sort", role.getSort());
        map.put("status", role.getStatus());
        map.put("dataScope", role.getDataScope());
        return map;
    }

    @Override
    public <V> V getObj(Wrapper<RoleJpa> queryWrapper, Function<? super Object, V> mapper) {
        RoleJpa role = getOne(queryWrapper);
        return role != null ? mapper.apply(role) : null;
    }

    @Override
    public BaseMapper<RoleJpa> getBaseMapper() {
        throw new UnsupportedOperationException("JPA implementation does not support MyBatis BaseMapper");
    }

    @Override
    public Class<RoleJpa> getEntityClass() {
        return RoleJpa.class;
    }

    /**
     * Converts a JPA Page of RoleJpa entities to a MyBatis-Plus Page of RolePageVO
     * objects.
     *
     * @param jpaPage the JPA Page of RoleJpa entities
     * @return a MyBatis-Plus Page of RolePageVO objects
     */
    private Page<RolePageVO> convertToMyBatisPage(org.springframework.data.domain.Page<RoleJpa> jpaPage) {
        Page<RolePageVO> mybatisPage = new Page<>(jpaPage.getNumber() + 1, jpaPage.getSize(),
                jpaPage.getTotalElements());
        List<RolePageVO> records = jpaPage.getContent().stream()
                .map(roleJpaConverter::toPageVO)
                .collect(Collectors.toList());
        mybatisPage.setRecords(records);
        return mybatisPage;
    }

    /**
     * Refreshes the permission cache for the specified role codes by deleting their
     * cache entries.
     *
     * @param roleCodes the role codes to refresh
     */
    private void refreshRolePermsCache(String... roleCodes) {
        for (String roleCode : roleCodes) {
            if (StrUtil.isNotBlank(roleCode)) {
                String cacheKey = "role:perms:" + roleCode;
                redisTemplate.delete(cacheKey);
            }
        }
    }
}

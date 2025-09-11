package community.waterlevel.iot.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import community.waterlevel.iot.core.security.token.TokenManager;
import community.waterlevel.iot.core.security.util.SecurityUtils;
import community.waterlevel.iot.system.model.entity.UserRoleJpa;
import community.waterlevel.iot.system.model.entity.UserRoleId;
import community.waterlevel.iot.system.repository.UserJpaRepository;
import community.waterlevel.iot.system.repository.UserRoleJpaRepository;
import community.waterlevel.iot.system.service.UserRoleJpaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of user-role relationship management using JPA.
 * <p>
 * This service provides operations for managing user-role assignments, including batch operations, deletion,
 * existence checks, and integration with security token management. It supports both single and batch CRUD operations
 * and is designed for use with Spring Data JPA repositories.
 * </p>
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleJpaServiceImpl implements UserRoleJpaService {

  private final UserRoleJpaRepository userRoleJpaRepository;
  private final UserJpaRepository userJpaRepository;
  private final TokenManager tokenManager;

  @Override
    /**
     * Saves user-role assignments for a given user, updating roles as needed and invalidating tokens if changed.
     *
     * @param userId the user ID
     * @param roleIds the list of role IDs to assign
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveUserRoles(Long userId, List<Long> roleIds) {
    if (userId == null || CollectionUtil.isEmpty(roleIds)) {
      return;
    }

    List<Long> existingRoleIds = userRoleJpaRepository.findRoleIdsByUserId(userId);

    Set<Long> oldRoles = new HashSet<>(existingRoleIds);
    Set<Long> newRoles = new HashSet<>(roleIds);

    Set<Long> addedRoles = new HashSet<>(newRoles);
    addedRoles.removeAll(oldRoles);

    Set<Long> removedRoles = new HashSet<>(oldRoles);
    removedRoles.removeAll(newRoles);

    boolean rolesChanged = !addedRoles.isEmpty() || !removedRoles.isEmpty();

    if (!addedRoles.isEmpty()) {
      List<UserRoleJpa> userRoles = addedRoles.stream()
          .map(roleId -> new UserRoleJpa(userId, roleId))
          .collect(Collectors.toList());
      userRoleJpaRepository.saveAll(userRoles);
    }

    if (!removedRoles.isEmpty()) {
      for (Long roleId : removedRoles) {
        UserRoleId id = new UserRoleId(userId, roleId);
        userRoleJpaRepository.deleteById(id);
      }
    }

    if (rolesChanged) {
      String accessToken = SecurityUtils.getTokenFromRequest();
      if (accessToken != null) {
        tokenManager.invalidateToken(accessToken);
      }
    }
  }

  @Override
    /**
     * Checks if a role has any assigned users.
     *
     * @param roleId the role ID
     * @return true if the role has assigned users, false otherwise
     */
    public boolean hasAssignedUsers(Long roleId) {
    if (roleId == null) {
      return false;
    }

    List<Long> userIds = userRoleJpaRepository.findUserIdsByRoleId(roleId);

    if (CollectionUtil.isEmpty(userIds)) {
      return false;
    }

    long activeUserCount = userJpaRepository.findAllById(userIds).size();
    return activeUserCount > 0;
  }

  @Override
    /**
     * Retrieves the list of role IDs assigned to a user.
     *
     * @param userId the user ID
     * @return the list of role IDs
     */
    public List<Long> getRoleIdsByUserId(Long userId) {
    if (userId == null) {
      return Collections.emptyList();
    }
    return userRoleJpaRepository.findRoleIdsByUserId(userId);
  }

  @Override
    /**
     * Retrieves the list of user IDs assigned to a role.
     *
     * @param roleId the role ID
     * @return the list of user IDs
     */
    public List<Long> getUserIdsByRoleId(Long roleId) {
    if (roleId == null) {
      return Collections.emptyList();
    }
    return userRoleJpaRepository.findUserIdsByRoleId(roleId);
  }

  @Override
    /**
     * Deletes all user-role assignments for a given user ID.
     *
     * @param userId the user ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteByUserId(Long userId) {
    if (userId != null) {
      userRoleJpaRepository.deleteByIdUserId(userId);
    }
  }

  @Override
    /**
     * Deletes all user-role assignments for a given role ID.
     *
     * @param roleId the role ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteByRoleId(Long roleId) {
    if (roleId != null) {
      userRoleJpaRepository.deleteByIdRoleId(roleId);
    }
  }

  @Override
    /**
     * Saves a user-role entity.
     *
     * @param entity the user-role entity
     * @return true if saved successfully, false otherwise
     */
    public boolean save(UserRoleJpa entity) {
    return userRoleJpaRepository.save(entity) != null;
  }

  @Override
    /**
     * Saves a batch of user-role entities.
     *
     * @param entityList the collection of user-role entities
     * @return true if saved successfully, false otherwise
     */
    public boolean saveBatch(Collection<UserRoleJpa> entityList) {
    if (CollectionUtil.isEmpty(entityList)) {
      return true;
    }
    userRoleJpaRepository.saveAll(entityList);
    return true;
  }

  @Override
    /**
     * Saves a batch of user-role entities with a specified batch size.
     *
     * @param entityList the collection of user-role entities
     * @param batchSize the batch size
     * @return true if saved successfully, false otherwise
     */
    public boolean saveBatch(Collection<UserRoleJpa> entityList, int batchSize) {
    return saveBatch(entityList);
  }

  @Override
    /**
     * Saves or updates a batch of user-role entities.
     *
     * @param entityList the collection of user-role entities
     * @return true if saved or updated successfully, false otherwise
     */
    public boolean saveOrUpdateBatch(Collection<UserRoleJpa> entityList) {
    return saveBatch(entityList);
  }

  @Override
    /**
     * Saves or updates a batch of user-role entities with a specified batch size.
     *
     * @param entityList the collection of user-role entities
     * @param batchSize the batch size
     * @return true if saved or updated successfully, false otherwise
     */
    public boolean saveOrUpdateBatch(Collection<UserRoleJpa> entityList, int batchSize) {
    return saveBatch(entityList);
  }

  @Override
    /**
     * Removes a user-role entity by its composite ID.
     *
     * @param id the user-role composite ID
     * @return true if removed successfully, false otherwise
     */
    public boolean removeById(Serializable id) {
    if (id instanceof UserRoleId) {
      userRoleJpaRepository.deleteById((UserRoleId) id);
      return true;
    }
    return false;
  }

  @Override
    /**
     * Removes user-role entities by a map of column values. Not implemented in JPA version.
     *
     * @param columnMap the column map
     * @return false (not implemented)
     */
    public boolean removeByMap(Map<String, Object> columnMap) {
    log.warn("removeByMap not implemented in JPA version");
    return false;
  }

  @Override
    /**
     * Removes user-role entities by a query wrapper. Not implemented in JPA version.
     *
     * @param queryWrapper the query wrapper
     * @return false (not implemented)
     */
    public boolean remove(Wrapper<UserRoleJpa> queryWrapper) {
    log.warn("remove with Wrapper not implemented in JPA version");
    return false;
  }

  @Override
    /**
     * Removes user-role entities by a collection of composite IDs.
     *
     * @param idList the collection of user-role composite IDs
     * @return true if removed successfully, false otherwise
     */
    public boolean removeByIds(Collection<?> idList) {
    if (CollectionUtil.isEmpty(idList)) {
      return true;
    }

    List<UserRoleId> ids = idList.stream()
        .filter(id -> id instanceof UserRoleId)
        .map(id -> (UserRoleId) id)
        .collect(Collectors.toList());

    userRoleJpaRepository.deleteAllById(ids);
    return true;
  }

  @Override
    /**
     * Updates a user-role entity by its ID.
     *
     * @param entity the user-role entity
     * @return true if updated successfully, false otherwise
     */
    public boolean updateById(UserRoleJpa entity) {
    return userRoleJpaRepository.save(entity) != null;
  }

  @Override
    /**
     * Updates user-role entities by a query wrapper. Not implemented in JPA version.
     *
     * @param updateWrapper the update wrapper
     * @return false (not implemented)
     */
    public boolean update(Wrapper<UserRoleJpa> updateWrapper) {
    log.warn("update with Wrapper not implemented in JPA version");
    return false;
  }

  @Override
    /**
     * Updates a user-role entity by entity and query wrapper. Not implemented in JPA version.
     *
     * @param entity the user-role entity
     * @param updateWrapper the update wrapper
     * @return false (not implemented)
     */
    public boolean update(UserRoleJpa entity, Wrapper<UserRoleJpa> updateWrapper) {
    log.warn("update with entity and Wrapper not implemented in JPA version");
    return false;
  }

  @Override
    /**
     * Updates a batch of user-role entities by their IDs.
     *
     * @param entityList the collection of user-role entities
     * @return true if updated successfully, false otherwise
     */
    public boolean updateBatchById(Collection<UserRoleJpa> entityList) {
    return saveBatch(entityList);
  }

  @Override
    /**
     * Updates a batch of user-role entities by their IDs with a specified batch size.
     *
     * @param entityList the collection of user-role entities
     * @param batchSize the batch size
     * @return true if updated successfully, false otherwise
     */
    public boolean updateBatchById(Collection<UserRoleJpa> entityList, int batchSize) {
    return saveBatch(entityList);
  }

  @Override
    /**
     * Saves or updates a user-role entity.
     *
     * @param entity the user-role entity
     * @return true if saved or updated successfully, false otherwise
     */
    public boolean saveOrUpdate(UserRoleJpa entity) {
    return save(entity);
  }

  @Override
    /**
     * Retrieves a user-role entity by its composite ID.
     *
     * @param id the user-role composite ID
     * @return the user-role entity, or null if not found
     */
    public UserRoleJpa getById(Serializable id) {
    if (id instanceof UserRoleId) {
      return userRoleJpaRepository.findById((UserRoleId) id).orElse(null);
    }
    return null;
  }

  @Override
    /**
     * Retrieves a list of user-role entities by a collection of composite IDs.
     *
     * @param idList the collection of user-role composite IDs
     * @return the list of user-role entities
     */
    public List<UserRoleJpa> listByIds(Collection<? extends Serializable> idList) {
    if (CollectionUtil.isEmpty(idList)) {
      return Collections.emptyList();
    }

    List<UserRoleId> ids = idList.stream()
        .filter(id -> id instanceof UserRoleId)
        .map(id -> (UserRoleId) id)
        .collect(Collectors.toList());

    return userRoleJpaRepository.findAllById(ids);
  }

  @Override
    /**
     * Retrieves a list of user-role entities by a map of column values. Not implemented in JPA version.
     *
     * @param columnMap the column map
     * @return an empty list (not implemented)
     */
    public List<UserRoleJpa> listByMap(Map<String, Object> columnMap) {
    log.warn("listByMap not implemented in JPA version");
    return Collections.emptyList();
  }

  @Override
    /**
     * Retrieves a single user-role entity by a query wrapper. Not implemented in JPA version.
     *
     * @param queryWrapper the query wrapper
     * @return null (not implemented)
     */
    public UserRoleJpa getOne(Wrapper<UserRoleJpa> queryWrapper) {
    log.warn("getOne with Wrapper not implemented in JPA version");
    return null;
  }

  @Override
    /**
     * Retrieves a single user-role entity by a query wrapper, optionally throwing an exception. Not implemented in JPA version.
     *
     * @param queryWrapper the query wrapper
     * @param throwEx whether to throw an exception if not found
     * @return null (not implemented)
     */
    public UserRoleJpa getOne(Wrapper<UserRoleJpa> queryWrapper, boolean throwEx) {
    log.warn("getOne with Wrapper not implemented in JPA version");
    return null;
  }

  @Override
    /**
     * Retrieves an optional user-role entity by a query wrapper. Not implemented in JPA version.
     *
     * @param queryWrapper the query wrapper
     * @return an empty optional (not implemented)
     */
    public Optional<UserRoleJpa> getOneOpt(Wrapper<UserRoleJpa> queryWrapper) {
    log.warn("getOneOpt with Wrapper not implemented in JPA version");
    return Optional.empty();
  }

  @Override
    /**
     * Retrieves an optional user-role entity by a query wrapper, optionally throwing an exception. Not implemented in JPA version.
     *
     * @param queryWrapper the query wrapper
     * @param throwEx whether to throw an exception if not found
     * @return an empty optional (not implemented)
     */
    public Optional<UserRoleJpa> getOneOpt(Wrapper<UserRoleJpa> queryWrapper, boolean throwEx) {
    log.warn("getOneOpt with Wrapper not implemented in JPA version");
    return Optional.empty();
  }

  @Override
    /**
     * Retrieves a map of user-role properties by a query wrapper. Not implemented in JPA version.
     *
     * @param queryWrapper the query wrapper
     * @return an empty map (not implemented)
     */
    public Map<String, Object> getMap(Wrapper<UserRoleJpa> queryWrapper) {
    log.warn("getMap not implemented in JPA version");
    return Collections.emptyMap();
  }

  @Override
    /**
     * Retrieves a mapped object from a user-role entity by a query wrapper. Not implemented in JPA version.
     *
     * @param queryWrapper the query wrapper
     * @param mapper the mapping function
     * @return null (not implemented)
     */
    public <V> V getObj(Wrapper<UserRoleJpa> queryWrapper, Function<? super Object, V> mapper) {
    log.warn("getObj not implemented in JPA version");
    return null;
  }

  @Override
    /**
     * Counts the total number of user-role entities.
     *
     * @return the total user-role count
     */
    public long count() {
    return userRoleJpaRepository.count();
  }

  @Override
    /**
     * Counts the number of user-role entities matching the query wrapper. Not implemented in JPA version.
     *
     * @param queryWrapper the query wrapper
     * @return 0 (not implemented)
     */
    public long count(Wrapper<UserRoleJpa> queryWrapper) {
    log.warn("count with Wrapper not implemented in JPA version");
    return 0;
  }

  @Override
    /**
     * Retrieves a list of all user-role entities.
     *
     * @return the list of all user-role entities
     */
    public List<UserRoleJpa> list() {
    return userRoleJpaRepository.findAll();
  }

  @Override
    /**
     * Retrieves a list of user-role entities matching the query wrapper. Not implemented in JPA version.
     *
     * @param queryWrapper the query wrapper
     * @return an empty list (not implemented)
     */
    public List<UserRoleJpa> list(Wrapper<UserRoleJpa> queryWrapper) {
    log.warn("list with Wrapper not implemented in JPA version");
    return Collections.emptyList();
  }

  @Override
    /**
     * Retrieves a paginated list of user-role entities matching the query wrapper. Not implemented in JPA version.
     *
     * @param page the page object
     * @param queryWrapper the query wrapper
     * @return the page object (not implemented)
     */
    public <E extends IPage<UserRoleJpa>> E page(E page, Wrapper<UserRoleJpa> queryWrapper) {
    log.warn("page with Wrapper not implemented in JPA version");
    return page;
  }

  @Override
    /**
     * Retrieves a paginated list of user-role entities. Not implemented in JPA version.
     *
     * @param page the page object
     * @return the page object (not implemented)
     */
    public <E extends IPage<UserRoleJpa>> E page(E page) {
    log.warn("page not implemented in JPA version");
    return page;
  }

  @Override
    /**
     * Retrieves a list of maps of user-role properties matching the query wrapper. Not implemented in JPA version.
     *
     * @param queryWrapper the query wrapper
     * @return an empty list (not implemented)
     */
    public List<Map<String, Object>> listMaps(Wrapper<UserRoleJpa> queryWrapper) {
    log.warn("listMaps not implemented in JPA version");
    return Collections.emptyList();
  }

  @Override
    /**
     * Retrieves a list of maps of all user-role properties. Not implemented in JPA version.
     *
     * @return an empty list (not implemented)
     */
    public List<Map<String, Object>> listMaps() {
    log.warn("listMaps not implemented in JPA version");
    return Collections.emptyList();
  }

  @Override
    /**
     * Retrieves a list of all user-role objects. Not implemented in JPA version.
     *
     * @return an empty list (not implemented)
     */
    public List<Object> listObjs() {
    log.warn("listObjs not implemented in JPA version");
    return Collections.emptyList();
  }

  @Override
    /**
     * Retrieves a list of mapped user-role objects. Not implemented in JPA version.
     *
     * @param mapper the mapping function
     * @return an empty list (not implemented)
     */
    public <V> List<V> listObjs(Function<? super Object, V> mapper) {
    log.warn("listObjs with mapper not implemented in JPA version");
    return Collections.emptyList();
  }

  @Override
    /**
     * Retrieves a list of user-role objects matching the query wrapper. Not implemented in JPA version.
     *
     * @param queryWrapper the query wrapper
     * @return an empty list (not implemented)
     */
    public <T> List<T> listObjs(Wrapper<UserRoleJpa> queryWrapper) {
    log.warn("listObjs with Wrapper not implemented in JPA version");
    return Collections.emptyList();
  }

  @Override
    /**
     * Retrieves a list of mapped user-role objects matching the query wrapper. Not implemented in JPA version.
     *
     * @param queryWrapper the query wrapper
     * @param mapper the mapping function
     * @return an empty list (not implemented)
     */
    public <V> List<V> listObjs(Wrapper<UserRoleJpa> queryWrapper, Function<? super Object, V> mapper) {
    log.warn("listObjs with Wrapper and mapper not implemented in JPA version");
    return Collections.emptyList();
  }

  @Override
    /**
     * Retrieves a paginated list of maps of user-role properties matching the query wrapper. Not implemented in JPA version.
     *
     * @param page the page object
     * @param queryWrapper the query wrapper
     * @return the page object (not implemented)
     */
    public <E extends IPage<Map<String, Object>>> E pageMaps(E page, Wrapper<UserRoleJpa> queryWrapper) {
    log.warn("pageMaps not implemented in JPA version");
    return page;
  }

  @Override
    /**
     * Retrieves a paginated list of maps of all user-role properties. Not implemented in JPA version.
     *
     * @param page the page object
     * @return the page object (not implemented)
     */
    public <E extends IPage<Map<String, Object>>> E pageMaps(E page) {
    log.warn("pageMaps not implemented in JPA version");
    return page;
  }

  @Override
    /**
     * Gets the MyBatis BaseMapper. Not supported in JPA version.
     *
     * @return null (not supported)
     */
    public BaseMapper<UserRoleJpa> getBaseMapper() {
    log.warn("getBaseMapper not supported in JPA version");
    return null;
  }

  @Override
    /**
     * Gets the entity class for UserRoleJpa.
     *
     * @return the UserRoleJpa class
     */
    public Class<UserRoleJpa> getEntityClass() {
    return UserRoleJpa.class;
  }

}

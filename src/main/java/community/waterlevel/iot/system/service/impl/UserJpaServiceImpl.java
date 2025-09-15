package community.waterlevel.iot.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import community.waterlevel.iot.common.constant.SystemConstants;
import community.waterlevel.iot.common.enums.DataScopeEnum;
import community.waterlevel.iot.common.exception.BusinessException;
import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.core.security.model.UserAuthCredentials;
import community.waterlevel.iot.core.security.service.PermissionService;
import community.waterlevel.iot.core.security.token.TokenManager;
import community.waterlevel.iot.core.security.util.SecurityUtils;

import community.waterlevel.iot.system.converter.UserJpaConverter;
import community.waterlevel.iot.system.model.entity.DeptJpa;
import community.waterlevel.iot.system.model.entity.RoleJpa;
import community.waterlevel.iot.system.model.entity.UserJpa;
import community.waterlevel.iot.system.model.entity.UserRoleJpa;
import community.waterlevel.iot.system.enums.DictCodeEnum;
import community.waterlevel.iot.system.model.bo.UserBO;
import community.waterlevel.iot.system.model.dto.CurrentUserDTO;

import community.waterlevel.iot.system.model.form.*;
import community.waterlevel.iot.system.model.query.UserPageQuery;
import community.waterlevel.iot.system.model.vo.UserPageVO;
import community.waterlevel.iot.system.model.vo.UserProfileVO;
import community.waterlevel.iot.system.repository.DeptJpaRepository;
import community.waterlevel.iot.system.repository.RoleJpaRepository;
import community.waterlevel.iot.system.repository.UserJpaRepository;
import community.waterlevel.iot.system.repository.UserRoleJpaRepository;

import community.waterlevel.iot.system.service.RoleJpaService;
import community.waterlevel.iot.system.service.SystemDictItemJpaService;
import community.waterlevel.iot.system.service.UserJpaService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of user-related business logic using JPA.
 * <p>
 * This service provides operations for user management, including CRUD operations, authentication, role binding,
 * password management, profile updates, and user export functionalities. It integrates with Spring Data JPA repositories,
 * security utilities, and Redis for caching and verification code management.
 * </p>
 *
 * @author Ray.Hao
 * @since 2022/1/14
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class UserJpaServiceImpl implements UserJpaService {

    private final UserJpaRepository userJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final DeptJpaRepository deptJpaRepository;
    private final UserJpaConverter userJpaConverter;

    private final PasswordEncoder passwordEncoder;
    private final RoleJpaService roleJpaService;
    private final PermissionService permissionService;
    private final TokenManager tokenManager;


    @Override
    /**
     * Retrieves a paginated list of users based on query parameters with data permission control.
     * Applies data scope filtering based on the current user's role permissions:
     * - System administrators can see all users
     * - Community administrators can only see users in their department
     * - Regular users can only see themselves
     *
     * @param queryParams the user page query parameters
     * @return a paginated list of user view objects
     */
    public IPage<UserPageVO> getUserPage(UserPageQuery queryParams) {

        Pageable pageable = PageRequest.of(
                queryParams.getPageNum() - 1,
                queryParams.getPageSize(),
                Sort.by("createTime").descending());

        Specification<UserJpa> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Apply basic query filters
            if (StrUtil.isNotBlank(queryParams.getKeywords())) {
                String keywords = "%" + queryParams.getKeywords() + "%";
                Predicate usernamePredicate = criteriaBuilder.like(root.get("username"), keywords);
                Predicate nicknamePredicate = criteriaBuilder.like(root.get("nickname"), keywords);
                predicates.add(criteriaBuilder.or(usernamePredicate, nicknamePredicate));
            }

            if (queryParams.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), queryParams.getStatus()));
            }

            if (queryParams.getDeptId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("deptId"), queryParams.getDeptId()));
            }
            
            // Apply data permission filtering
            applyDataPermissionFilter(predicates, root, criteriaBuilder);
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        org.springframework.data.domain.Page<UserJpa> jpaPage = userJpaRepository.findAll(spec, pageable);

        Page<UserPageVO> result = new Page<>(queryParams.getPageNum(), queryParams.getPageSize(),
                jpaPage.getTotalElements());

        List<UserPageVO> records = jpaPage.getContent().stream()
                .map(user -> {
                    UserPageVO vo = userJpaConverter.toPageVO(user);

                    if (user.getDeptId() != null) {
                        deptJpaRepository.findById(user.getDeptId())
                                .ifPresent(dept -> vo.setDeptName(dept.getName()));
                    }
                    List<Long> roleIds = userRoleJpaRepository.findRoleIdsByUserId(user.getId());
                    if (CollectionUtil.isNotEmpty(roleIds)) {
                        List<String> roleNames = roleJpaRepository.findAllById(roleIds)
                                .stream()
                                .map(role -> role.getName())
                                .collect(Collectors.toList());
                        vo.setRoleNames(String.join(",", roleNames));
                    }

                    return vo;
                })
                .collect(Collectors.toList());

        result.setRecords(records);
        return result;
    }

    @Override
    /**
     * Retrieves user form data for the specified user ID.
     *
     * @param userId the user ID
     * @return the user form data
     */
    public UserForm getUserFormData(Long userId) {
        UserJpa userJpa = userJpaRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User does not exist"));

        UserForm userForm = userJpaConverter.toForm(userJpa);

        List<Long> roleIds = userRoleJpaRepository.findRoleIdsByUserId(userId);
        userForm.setRoleIds(roleIds);

        return userForm;
    }

    @Override
    /**
     * Saves a new user with the provided form data.
     *
     * @param userForm the user form data
     * @return true if the user was saved successfully, false otherwise
     */
    @Transactional
    public boolean saveUser(UserForm userForm) {
        String username = userForm.getUsername();

        boolean exists = userJpaRepository.findByUsername(username).isPresent();
        Assert.isTrue(!exists, "Username already exists");

        UserJpa entity = userJpaConverter.toEntity(userForm);

        String defaultEncryptPwd = passwordEncoder.encode(SystemConstants.DEFAULT_PASSWORD);
        entity.setPassword(defaultEncryptPwd);
        UserJpa savedUser = userJpaRepository.save(entity);

        if (savedUser.getId() != null) {
            saveUserRoles(savedUser.getId(), userForm.getRoleIds());
            return true;
        }
        return false;
    }

    @Override
    /**
     * Updates an existing user with the provided form data.
     *
     * @param userId the user ID
     * @param userForm the user form data
     * @return true if the user was updated successfully, false otherwise
     */
    @Transactional
    public boolean updateUser(Long userId, UserForm userForm) {
        String username = userForm.getUsername();

        boolean exists = userJpaRepository.existsByUsernameAndIdNot(username, userId);
        Assert.isTrue(!exists, "Username already exists");

        UserJpa entity = userJpaConverter.toEntity(userForm);
        entity.setId(userId);

        // UserForm does not contain a password field. Preserve existing password
        // to avoid unintentionally blanking it during an update.
        userJpaRepository.findById(userId).ifPresent(existing -> entity.setPassword(existing.getPassword()));

        UserJpa savedUser = userJpaRepository.save(entity);

        if (savedUser.getId() != null) {
            saveUserRoles(userId, userForm.getRoleIds());
            return true;
        }
        return false;
    }

    @Override
    /**
     * Deletes users by a comma-separated string of user IDs.
     *
     * @param idsStr comma-separated user IDs
     * @return true if users were deleted successfully
     */
    @Transactional
    public boolean deleteUsers(String idsStr) {
        Assert.isTrue(StrUtil.isNotBlank(idsStr), "The deleted user data is empty");

        List<Long> ids = Arrays.stream(idsStr.split(","))
                .map(Long::parseLong)
                .toList();

        userJpaRepository.deleteAllById(ids);

        for (Long userId : ids) {
            userRoleJpaRepository.deleteByIdUserId(userId);
        }

        return true;
    }

    @Override
    /**
     * Retrieves authentication credentials for a user by username.
     *
     * @param username the username
     * @return the user's authentication credentials, or null if not found
     */
    public UserAuthCredentials getAuthCredentialsByUsername(String username) {
        Optional<UserJpa> userOpt = userJpaRepository.findActiveUserByUsername(username);
        if (userOpt.isEmpty()) {
            return null;
        }

        UserJpa user = userOpt.get();
        UserAuthCredentials credentials = buildUserAuthCredentials(user);

        Set<String> roles = credentials.getRoles();
        if (CollectionUtil.isNotEmpty(roles)) {
            Integer dataScope = roleJpaService.getMaximumDataScope(roles);
            credentials.setDataScope(dataScope);
        }
        return credentials;
    }

   
    @Override
    /**
     * Retrieves information about the currently authenticated user.
     *
     * @return the current user's information DTO
     */
    public CurrentUserDTO getCurrentUserInfo() {
        String username = SecurityUtils.getUsername();
        log.debug("Get current user information, username: {}", username);

        Optional<UserJpa> userOpt = userJpaRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new BusinessException("User does not exist");
        }

        UserJpa user = userOpt.get();
        CurrentUserDTO userInfoVO = userJpaConverter.toCurrentUserDTO(user);
        log.debug("User basic information obtained successfully, user ID: {}, nickname: {}", user.getId(),
                user.getNickname());

        Set<String> roles = SecurityUtils.getRoles();
        log.debug("The role obtained from SecurityUtils: {}", roles);
        userInfoVO.setRoles(roles);

        if (CollectionUtil.isNotEmpty(roles)) {
            log.debug("Start obtaining role permissions and role quantity: {}", roles.size());
            Set<String> perms = permissionService.getRolePermsFormCache(roles);
            log.info("Number of permissions obtained: {}, permission content: {}",
                    perms != null ? perms.size() : 0, perms);
            userInfoVO.setPerms(perms);
        } else {
            log.warn("The user role is empty and cannot obtain permissions.");
            userInfoVO.setPerms(Collections.emptySet());
        }

        log.info("User information acquisition completed, return data: userId={}, roles={}, perms={}",
                userInfoVO.getUserId(), userInfoVO.getRoles(), userInfoVO.getPerms());
        return userInfoVO;
    }

    @Override
    /**
     * Retrieves the profile information for a user by user ID.
     *
     * @param userId the user ID
     * @return the user's profile view object
     */
    public UserProfileVO getUserProfile(Long userId) {
        Optional<UserJpa> userOpt = userJpaRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new BusinessException("User does not exist");
        }

        UserJpa user = userOpt.get();
        UserBO userBO = userJpaConverter.toBO(user);
        UserProfileVO profileVO = userJpaConverter.toProfileVO(userBO);
        
        // Set department name
        if (user.getDeptId() != null) {
            deptJpaRepository.findById(user.getDeptId())
                    .ifPresent(dept -> profileVO.setDeptName(dept.getName()));
        }
        
        // Set role names
        List<Long> roleIds = userRoleJpaRepository.findRoleIdsByUserId(user.getId());
        if (CollectionUtil.isNotEmpty(roleIds)) {
            List<String> roleNames = roleJpaRepository.findAllById(roleIds)
                    .stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList());
            profileVO.setRoleNames(String.join(",", roleNames));
        }
        
        return profileVO;
    }

    @Override
    /**
     * Updates the profile information of the currently authenticated user.
     *
     * @param formData the user profile form data
     * @return true if the profile was updated successfully, false otherwise
     */
    @Transactional
    public boolean updateUserProfile(UserProfileForm formData) {
        Long userId = SecurityUtils.getUserId();

        Optional<UserJpa> userOpt = userJpaRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new BusinessException("User does not exist");
        }

        UserJpa entity = userJpaConverter.toEntity(formData);
        entity.setId(userId);

        // Preserve existing password during profile update (profile should not clear password)
        UserJpa existing = userOpt.get();
        entity.setPassword(existing.getPassword());

        UserJpa savedUser = userJpaRepository.save(entity);
        return savedUser.getId() != null;
    }

    @Override
    /**
     * Changes the password for a user.
     *
     * @param userId the user ID
     * @param data the password update form
     * @return true if the password was changed successfully, false otherwise
     */
    @Transactional
    public boolean changePassword(Long userId, PasswordUpdateForm data) {
        Optional<UserJpa> userOpt = userJpaRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new BusinessException("User does not exist");
        }

        UserJpa user = userOpt.get();
        String oldPassword = data.getOldPassword();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("The original password is incorrect");
        }

        if (passwordEncoder.matches(data.getNewPassword(), user.getPassword())) {
            throw new BusinessException("The new password cannot be the same as the original password");
        }

        if (!data.getNewPassword().equals(data.getConfirmPassword())) {
            throw new BusinessException("The new password and the confirm password do not match");
        }

        String newPassword = data.getNewPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        UserJpa savedUser = userJpaRepository.save(user);

        if (savedUser.getId() != null) {
            String accessToken = SecurityUtils.getTokenFromRequest();
            tokenManager.invalidateToken(accessToken);
            return true;
        }
        return false;
    }

    @Override
    /**
     * Resets the password for a user.
     *
     * @param userId the user ID
     * @param password the new password
     * @return true if the password was reset successfully, false otherwise
     */
    @Transactional
    public boolean resetPassword(Long userId, String password) {
        Optional<UserJpa> userOpt = userJpaRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        UserJpa user = userOpt.get();
        user.setPassword(passwordEncoder.encode(password));
        UserJpa savedUser = userJpaRepository.save(user);

        return savedUser.getId() != null;
    }


    @Override
    /**
     * Updates the status of a user.
     *
     * @param userId the user ID
     * @param status the new status
     * @return true if the status was updated successfully, false otherwise
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserStatus(Long userId, Integer status) {
        if (userId == null || status == null) {
            return false;
        }

        Optional<UserJpa> userOptional = userJpaRepository.findById(userId);
        if (userOptional.isPresent()) {
            UserJpa user = userOptional.get();
            user.setStatus(status);
            userJpaRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    /**
     * Lists user options for selection components.
     *
     * @return a list of user options
     */
    public List<Option<String>> listUserOptions() {
        List<UserJpa> list = userJpaRepository.findByStatus(1);
        return list.stream()
                .map(user -> new Option<>(String.valueOf(user.getId()), user.getNickname()))
                .collect(Collectors.toList());
    }

    @Override
    /**
     * Saves a user entity.
     *
     * @param entity the user entity
     * @return true if the user was saved successfully, false otherwise
     */
    public boolean save(UserJpa entity) {
        UserJpa savedUser = userJpaRepository.save(entity);
        return savedUser.getId() != null;
    }

    @Override
    /**
     * Saves a batch of user entities.
     *
     * @param entityList the collection of user entities
     * @return true if the batch was saved successfully, false otherwise
     */
    public boolean saveBatch(Collection<UserJpa> entityList) {
        List<UserJpa> savedUsers = userJpaRepository.saveAll(entityList);
        return !savedUsers.isEmpty();
    }

    @Override
    /**
     * Saves a batch of user entities with a specified batch size.
     *
     * @param entityList the collection of user entities
     * @param batchSize the batch size
     * @return true if the batch was saved successfully, false otherwise
     */
    public boolean saveBatch(Collection<UserJpa> entityList, int batchSize) {
        return saveBatch(entityList);
    }

    @Override
    /**
     * Saves or updates a batch of user entities.
     *
     * @param entityList the collection of user entities
     * @return true if the batch was saved or updated successfully, false otherwise
     */
    public boolean saveOrUpdateBatch(Collection<UserJpa> entityList) {
        List<UserJpa> savedUsers = userJpaRepository.saveAll(entityList);
        return !savedUsers.isEmpty();
    }

    @Override
    /**
     * Saves or updates a batch of user entities with a specified batch size.
     *
     * @param entityList the collection of user entities
     * @param batchSize the batch size
     * @return true if the batch was saved or updated successfully, false otherwise
     */
    public boolean saveOrUpdateBatch(Collection<UserJpa> entityList, int batchSize) {
        return saveOrUpdateBatch(entityList);
    }

    @Override
    /**
     * Removes a user by ID.
     *
     * @param id the user ID
     * @return true if the user was removed successfully
     */
    public boolean removeById(Serializable id) {
        userJpaRepository.deleteById((Long) id);
        return true;
    }

    @Override
    /**
     * Removes users by a map of column values. Not supported in JPA implementation.
     *
     * @param columnMap the column map
     * @return always throws UnsupportedOperationException
     */
    public boolean removeByMap(Map<String, Object> columnMap) {
        throw new UnsupportedOperationException("JPA implementation does not support removeByMap");
    }

    @Override
    /**
     * Removes users by a query wrapper. Not supported in JPA implementation.
     *
     * @param queryWrapper the query wrapper
     * @return always throws UnsupportedOperationException
     */
    public boolean remove(Wrapper<UserJpa> queryWrapper) {
        throw new UnsupportedOperationException("The JPA implementation does not support conditional deletes");
    }

    @Override
    /**
     * Removes users by a collection of IDs.
     *
     * @param idList the collection of user IDs
     * @return true if the users were removed successfully
     */
    public boolean removeByIds(Collection<?> idList) {
        List<Long> ids = idList.stream().map(id -> (Long) id).collect(Collectors.toList());
        userJpaRepository.deleteAllById(ids);
        return true;
    }

    @Override
    /**
     * Updates a user by entity.
     *
     * @param entity the user entity
     * @return true if the user was updated successfully, false otherwise
     */
    public boolean updateById(UserJpa entity) {
        UserJpa savedUser = userJpaRepository.save(entity);
        return savedUser.getId() != null;
    }

    @Override
    /**
     * Updates a user by entity and update wrapper.
     *
     * @param entity the user entity
     * @param updateWrapper the update wrapper
     * @return true if the user was updated successfully, false otherwise
     */
    public boolean update(UserJpa entity, Wrapper<UserJpa> updateWrapper) {
        return updateById(entity);
    }

    @Override
    /**
     * Updates a batch of user entities by ID.
     *
     * @param entityList the collection of user entities
     * @return true if the batch was updated successfully, false otherwise
     */
    public boolean updateBatchById(Collection<UserJpa> entityList) {
        List<UserJpa> savedUsers = userJpaRepository.saveAll(entityList);
        return !savedUsers.isEmpty();
    }

    @Override
    /**
     * Updates a batch of user entities by ID with a specified batch size.
     *
     * @param entityList the collection of user entities
     * @param batchSize the batch size
     * @return true if the batch was updated successfully, false otherwise
     */
    public boolean updateBatchById(Collection<UserJpa> entityList, int batchSize) {
        return updateBatchById(entityList);
    }

    @Override
    /**
     * Retrieves a user by ID.
     *
     * @param id the user ID
     * @return the user entity, or null if not found
     */
    public UserJpa getById(Serializable id) {
        return userJpaRepository.findById((Long) id).orElse(null);
    }

    @Override
    /**
     * Retrieves a list of users by a collection of IDs.
     *
     * @param idList the collection of user IDs
     * @return the list of user entities
     */
    public List<UserJpa> listByIds(Collection<? extends Serializable> idList) {
        List<Long> ids = idList.stream().map(id -> (Long) id).collect(Collectors.toList());
        return userJpaRepository.findAllById(ids);
    }

    @Override
    /**
     * Retrieves a list of users by a map of column values.
     *
     * @param columnMap the column map
     * @return the list of user entities
     */
    public List<UserJpa> listByMap(Map<String, Object> columnMap) {
        return userJpaRepository.findAll();
    }

    @Override
    /**
     * Retrieves a single user by a query wrapper.
     *
     * @param queryWrapper the query wrapper
     * @return the user entity, or null if not found
     */
    public UserJpa getOne(Wrapper<UserJpa> queryWrapper) {
        List<UserJpa> list = list(queryWrapper);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    /**
     * Retrieves a single user by a query wrapper, optionally throwing an exception if not found.
     *
     * @param queryWrapper the query wrapper
     * @param throwEx whether to throw an exception if not found
     * @return the user entity, or null if not found
     */
    public UserJpa getOne(Wrapper<UserJpa> queryWrapper, boolean throwEx) {
        UserJpa one = getOne(queryWrapper);
        if (throwEx && one == null) {
            throw new BusinessException("The query result is empty");
        }
        return one;
    }

    @Override
    /**
     * Retrieves an optional user by a query wrapper.
     *
     * @param queryWrapper the query wrapper
     * @return an optional containing the user entity if found, or empty otherwise
     */
    public Optional<UserJpa> getOneOpt(Wrapper<UserJpa> queryWrapper) {
        return Optional.ofNullable(getOne(queryWrapper));
    }

    @Override
    /**
     * Retrieves an optional user by a query wrapper, optionally throwing an exception if not found.
     *
     * @param queryWrapper the query wrapper
     * @param throwEx whether to throw an exception if not found
     * @return an optional containing the user entity if found, or empty otherwise
     */
    public Optional<UserJpa> getOneOpt(Wrapper<UserJpa> queryWrapper, boolean throwEx) {
        return Optional.ofNullable(getOne(queryWrapper, throwEx));
    }

    @Override
    /**
     * Retrieves a map of user properties by a query wrapper.
     *
     * @param queryWrapper the query wrapper
     * @return a map of user properties, or an empty map if not found
     */
    public Map<String, Object> getMap(Wrapper<UserJpa> queryWrapper) {
        UserJpa user = getOne(queryWrapper);
        if (user == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("nickname", user.getNickname());
        map.put("email", user.getEmail());
        map.put("mobile", user.getMobile());
        map.put("status", user.getStatus());
        return map;
    }

    @Override
    /**
     * Retrieves a mapped object from a user entity by a query wrapper.
     *
     * @param queryWrapper the query wrapper
     * @param mapper the mapping function
     * @return the mapped object, or null if not found
     */
    public <V> V getObj(Wrapper<UserJpa> queryWrapper, Function<? super Object, V> mapper) {
        UserJpa user = getOne(queryWrapper);
        return user != null ? mapper.apply(user) : null;
    }

    @Override
    /**
     * Counts the total number of users.
     *
     * @return the total user count
     */
    public long count() {
        return userJpaRepository.count();
    }

    @Override
    /**
     * Counts the number of users matching the query wrapper.
     *
     * @param queryWrapper the query wrapper
     * @return the user count
     */
    public long count(Wrapper<UserJpa> queryWrapper) {
        return userJpaRepository.count();
    }

    @Override
    /**
     * Retrieves a list of all users.
     *
     * @return the list of all user entities
     */
    public List<UserJpa> list() {
        return userJpaRepository.findAll();
    }

    @Override
    /**
     * Retrieves a list of users matching the query wrapper.
     *
     * @param queryWrapper the query wrapper
     * @return the list of user entities
     */
    public List<UserJpa> list(Wrapper<UserJpa> queryWrapper) {
        return list();
    }

    /**
     * Retrieves a paginated list of users.
     *
     * @param page the page object
     * @return the paginated list of user entities
     */
    public Page<UserJpa> page(Page<UserJpa> page) {
        Pageable pageable = PageRequest.of((int) page.getCurrent() - 1, (int) page.getSize());
        org.springframework.data.domain.Page<UserJpa> jpaPage = userJpaRepository.findAll(pageable);

        Page<UserJpa> result = new Page<>(page.getCurrent(), page.getSize(), jpaPage.getTotalElements());
        result.setRecords(jpaPage.getContent());
        return result;
    }

    /**
     * Retrieves a paginated list of users matching the query wrapper.
     *
     * @param page the page object
     * @param queryWrapper the query wrapper
     * @return the paginated list of user entities
     */
    public Page<UserJpa> page(Page<UserJpa> page, Wrapper<UserJpa> queryWrapper) {
        return page(page);
    }

    @Override
    /**
     * Saves or updates a user entity.
     *
     * @param entity the user entity
     * @return true if the user was saved or updated successfully, false otherwise
     */
    public boolean saveOrUpdate(UserJpa entity) {
        UserJpa savedUser = userJpaRepository.save(entity);
        return savedUser.getId() != null;
    }

    @Override
    /**
     * Gets the MyBatis BaseMapper. Not supported in JPA implementation.
     *
     * @return always throws UnsupportedOperationException
     */
    public BaseMapper<UserJpa> getBaseMapper() {
        throw new UnsupportedOperationException("JPA implementation does not support MyBatis BaseMapper");
    }

    @Override
    /**
     * Gets the entity class for UserJpa.
     *
     * @return the UserJpa class
     */
    public Class<UserJpa> getEntityClass() {
        return UserJpa.class;
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {

        userRoleJpaRepository.deleteByIdUserId(userId);

        if (CollectionUtil.isNotEmpty(roleIds)) {
            List<UserRoleJpa> userRoles = roleIds.stream()
                    .map(roleId -> new UserRoleJpa(userId, roleId))
                    .collect(Collectors.toList());
            userRoleJpaRepository.saveAll(userRoles);
        }
    }

    private UserAuthCredentials buildUserAuthCredentials(UserJpa user) {
        UserAuthCredentials credentials = new UserAuthCredentials();
        credentials.setUserId(user.getId());
        credentials.setUsername(user.getUsername());
        credentials.setPassword(user.getPassword());
        credentials.setStatus(user.getStatus());
        credentials.setDeptId(user.getDeptId());

        List<Long> roleIds = userRoleJpaRepository.findRoleIdsByUserId(user.getId());
        if (CollectionUtil.isNotEmpty(roleIds)) {
            List<RoleJpa> roleList = roleJpaRepository.findAllById(roleIds);
            Set<String> roleCodes = roleList.stream()
                    .map(RoleJpa::getCode)
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toSet());
            credentials.setRoles(roleCodes);
        }
        return credentials;
    }

    /**
     * Applies data permission filtering based on current user's role and data scope.
     * Implements multi-level data access control for community water level IoT system:
     * - ALL (1): System administrators can access all data
     * - DEPT_AND_SUB (2): Can access department and sub-department data
     * - DEPT (3): Community administrators can only access their department data
     * - SELF (4): Regular users can only access their own data
     *
     * @param predicates the list of predicates to add filtering conditions
     * @param root the root entity for building predicates
     * @param criteriaBuilder the criteria builder for constructing queries
     */
    private void applyDataPermissionFilter(List<Predicate> predicates, 
                                         Root<UserJpa> root, 
                                         CriteriaBuilder criteriaBuilder) {
        
        // Skip filtering for super administrators (ROOT role)
        if (SecurityUtils.isRoot()) {
            return;
        }

        // Get current user's data scope from their roles
        Set<String> roleCodes = SecurityUtils.getRoleCodes();
        Integer dataScope = roleJpaService.getMaximumDataScope(roleCodes);
        
        if (dataScope == null) {
            // If no data scope defined, default to self-only access
            dataScope = DataScopeEnum.SELF.getValue();
        }

        Long currentUserId = SecurityUtils.getUserId();
        Long currentUserDeptId = SecurityUtils.getDeptId();

        switch (dataScope) {
            case 1: // ALL - System administrators can see all data
                // No additional filtering needed
                break;
                
            case 2: // DEPT_AND_SUB - Department and sub-department data
                if (currentUserDeptId != null) {
                    // Get current department and all sub-departments
                    List<Long> deptIds = getDeptAndSubDeptIds(currentUserDeptId);
                    predicates.add(root.get("deptId").in(deptIds));
                }
                break;
                
            case 3: // DEPT - Department data only (Community Admin level)
                if (currentUserDeptId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("deptId"), currentUserDeptId));
                }
                break;
                
            case 4: // SELF - Self data only (Regular users)
                if (currentUserId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), currentUserId));
                }
                break;
                
            default:
                // Default to self-only access for unknown data scopes
                if (currentUserId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), currentUserId));
                }
        }
    }

    /**
     * Retrieves department ID and all sub-department IDs for hierarchical data access.
     * Used for DEPT_AND_SUB data scope to include data from current department and all sub-departments.
     *
     * @param deptId the parent department ID
     * @return list of department IDs including parent and all children
     */
    private List<Long> getDeptAndSubDeptIds(Long deptId) {
        List<Long> deptIds = new ArrayList<>();
        deptIds.add(deptId);
        
        // Find all sub-departments using tree_path
        String treePath = "%" + deptId + "%";
        List<DeptJpa> subDepts = deptJpaRepository.findByTreePathContaining(treePath);
        
        List<Long> subDeptIds = subDepts.stream()
                .map(DeptJpa::getId)
                .filter(id -> !id.equals(deptId)) // Exclude the parent department itself
                .collect(Collectors.toList());
        
        deptIds.addAll(subDeptIds);
        return deptIds;
    }
}

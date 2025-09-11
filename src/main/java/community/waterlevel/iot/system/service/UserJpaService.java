package community.waterlevel.iot.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.core.security.model.UserAuthCredentials;
import community.waterlevel.iot.system.model.dto.CurrentUserDTO;
import community.waterlevel.iot.system.model.dto.UserExportDTO;
import community.waterlevel.iot.system.model.entity.UserJpa;
import community.waterlevel.iot.system.model.form.*;
import community.waterlevel.iot.system.model.query.UserPageQuery;
import community.waterlevel.iot.system.model.vo.UserPageVO;
import community.waterlevel.iot.system.model.vo.UserProfileVO;

import java.util.List;

/**
 * Service interface for user-related business logic and operations.
 * <p>
 * Provides methods for user management, authentication, registration, profile updates,
 * password management, status changes, and user option listing. Integrates with MyBatis-Plus
 * and supports both standard and social (WeChat) authentication flows.
 * </p>
 *
 * @author Ray.Hao
 * @since 2022/1/14
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface UserJpaService extends IService<UserJpa> {

    /**
     * Retrieves a paginated list of users based on query parameters.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return a MyBatis-Plus IPage containing user page view objects
     */
    IPage<UserPageVO> getUserPage(UserPageQuery queryParams);

    /**
     * Retrieves the form data for a specific user by their ID.
     *
     * @param userId the user ID
     * @return the UserForm object
     */
    UserForm getUserFormData(Long userId);

    /**
     * Saves a new user based on the provided form data.
     *
     * @param userForm the user form data
     * @return true if the user was saved successfully
     */
    boolean saveUser(UserForm userForm);

    /**
     * Updates an existing user by their ID with the provided form data.
     *
     * @param userId   the user ID
     * @param userForm the updated user form data
     * @return true if the update was successful
     */
    boolean updateUser(Long userId, UserForm userForm);

    /**
     * Deletes one or more users by their IDs.
     *
     * @param idsStr a comma-separated string of user IDs to delete
     * @return true if all users were deleted successfully
     */
    boolean deleteUsers(String idsStr);

    /**
     * Retrieves authentication credentials for a user by their username.
     *
     * @param username the username
     * @return the UserAuthCredentials object
     */
    UserAuthCredentials getAuthCredentialsByUsername(String username);

    /**
     * Lists users for export based on query parameters.
     *
     * @param queryParams the query parameters for filtering
     * @return the list of UserExportDTO objects
     */
    List<UserExportDTO> listExportUsers(UserPageQuery queryParams);

    /**
     * Retrieves information about the current authenticated user.
     *
     * @return the CurrentUserDTO object
     */
    CurrentUserDTO getCurrentUserInfo();

    /**
     * Retrieves the profile information for a specific user by their ID.
     *
     * @param userId the user ID
     * @return the UserProfileVO object
     */
    UserProfileVO getUserProfile(Long userId);

    /**
     * Updates the profile information for a user.
     *
     * @param formData the user profile form data
     * @return true if the update was successful
     */
    boolean updateUserProfile(UserProfileForm formData);

    /**
     * Changes the password for a user by their ID.
     *
     * @param userId the user ID
     * @param data   the password update form data
     * @return true if the password was changed successfully
     */
    boolean changePassword(Long userId, PasswordUpdateForm data);

    /**
     * Resets the password for a user by their ID.
     *
     * @param userId   the user ID
     * @param password the new password
     * @return true if the password was reset successfully
     */
    boolean resetPassword(Long userId, String password);

    /**
     * Updates the status of a user by their ID.
     *
     * @param userId the user ID
     * @param status the new status
     * @return true if the update was successful
     */
    boolean updateUserStatus(Long userId, Integer status);

    /**
     * Lists all users as options for dropdowns.
     *
     * @return the list of user options
     */
    List<Option<String>> listUserOptions();
}

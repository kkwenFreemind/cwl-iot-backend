package community.waterlevel.iot.system.controller;

import community.waterlevel.iot.common.annotation.Log;
import community.waterlevel.iot.common.annotation.RepeatSubmit;
import community.waterlevel.iot.common.enums.LogModuleEnum;
import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.common.result.PageResult;
import community.waterlevel.iot.common.result.Result;
import community.waterlevel.iot.core.security.util.SecurityUtils;
import community.waterlevel.iot.system.model.dto.CurrentUserDTO;
import community.waterlevel.iot.system.model.form.*;
import community.waterlevel.iot.system.model.query.UserPageQuery;
import community.waterlevel.iot.system.model.vo.UserPageVO;
import community.waterlevel.iot.system.model.vo.UserProfileVO;
import community.waterlevel.iot.system.service.UserJpaService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * UserJpaController is a REST controller that provides endpoints for managing
 * users and their profiles in the system.
 * <p>
 * It exposes APIs for listing, creating, updating, deleting, importing, and
 * exporting users, as well as managing user status, roles, and contact
 * information. The controller delegates business logic to the
 * {@link UserJpaService} and returns standardized API responses.
 * <p>
 * Designed for use in the IoT backend's administration and user interfaces to
 * manage user accounts, authentication, and personal information.
 *
 * @author Ray.Hao
 * @since 2022/10/16
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Tag(name = "02.User Controller")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserJpaController {

    private final UserJpaService userService;

    /**
     * Retrieves a paginated list of users based on query parameters.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return PageResult containing user page data
     */
    @Operation(summary = "Retrieves a paginated list of users based on query parameters.")
    @GetMapping("/page")
    @Log(value = "Retrieves a paginated list of users based on query parameters.", module = LogModuleEnum.USER)
    public PageResult<UserPageVO> getUserPage(
            @Valid UserPageQuery queryParams) {
        IPage<UserPageVO> result = userService.getUserPage(queryParams);
        return PageResult.success(result);
    }

    /**
     * Creates a new user in the system.
     *
     * @param userForm the user form data
     * @return Result indicating success or failure
     */
    @Operation(summary = " Creates a new user in the system.")
    @PostMapping
    @RepeatSubmit
    @Log(value = " Creates a new user in the system.", module = LogModuleEnum.USER)
    public Result<?> saveUser(
            @RequestBody @Valid UserForm userForm) {
        boolean result = userService.saveUser(userForm);
        return Result.judge(result);
    }

    /**
     * Retrieves the form data for a specific user by user ID.
     *
     * @param userId the user ID
     * @return Result containing the user form data
     */
    @Operation(summary = "Retrieves the form data for a specific user by user ID.")
    @GetMapping("/{userId}/form")
    @Log(value = "Retrieves the form data for a specific user by user ID.", module = LogModuleEnum.USER)
    public Result<UserForm> getUserForm(
            @Parameter(description = "userId") @PathVariable Long userId) {
        UserForm formData = userService.getUserFormData(userId);
        return Result.success(formData);
    }

    /**
     * Updates an existing user by user ID.
     *
     * @param userId   the user ID
     * @param userForm the user form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Updates an existing user by user ID.")
    @PutMapping(value = "/{userId}")
    @Log(value = "Updates an existing user by user ID.", module = LogModuleEnum.USER)
    public Result<Void> updateUser(
            @Parameter(description = "userId") @PathVariable Long userId,
            @RequestBody @Valid UserForm userForm) {
        boolean result = userService.updateUser(userId, userForm);
        return Result.judge(result);
    }

    /**
     * Deletes one or more users by IDs.
     *
     * @param ids the user IDs, separated by commas
     * @return Result indicating success or failure
     */
    @Operation(summary = "Deletes one or more users by IDs.")
    @DeleteMapping("/{ids}")
    @Log(value = "Deletes one or more users by IDs.", module = LogModuleEnum.USER)
    public Result<Void> deleteUsers(
            @Parameter(description = "the user IDs, separated by commas") @PathVariable String ids) {
        boolean result = userService.deleteUsers(ids);
        return Result.judge(result);
    }

    /**
     * Updates the status of a user (enable/disable).
     *
     * @param userId the user ID
     * @param status the user status (1: enabled, 0: disabled)
     * @return Result indicating success or failure
     */
    @Operation(summary = "Updates the status of a user (enable/disable).")
    @PatchMapping(value = "/{userId}/status")
    @Log(value = "Updates the status of a user (enable/disable).", module = LogModuleEnum.USER)
    public Result<Void> updateUserStatus(
            @Parameter(description = "userId") @PathVariable Long userId,
            @Parameter(description = "User status (1: enabled; 0: disabled)") @RequestParam Integer status) {
        boolean result = userService.updateUserStatus(userId, status);
        return Result.judge(result);
    }

    /**
     * Retrieves information about the currently logged-in user.
     *
     * @return Result containing the current user DTO
     */
    @Operation(summary = "Retrieves information about the currently logged-in user.")
    @GetMapping("/me")
    @Log(value = "Me", module = LogModuleEnum.USER)
    public Result<CurrentUserDTO> getCurrentUser() {
        CurrentUserDTO currentUserDTO = userService.getCurrentUserInfo();
        return Result.success(currentUserDTO);
    }

    /**
     * Retrieves the profile information of the current user for the personal
     * center.
     *
     * @return Result containing the user profile value object
     */
    @Operation(summary = "Profile")
    @GetMapping("/profile")
    @Log(value = "Profile", module = LogModuleEnum.USER)
    public Result<UserProfileVO> getUserProfile() {
        Long userId = SecurityUtils.getUserId();
        UserProfileVO userProfile = userService.getUserProfile(userId);
        return Result.success(userProfile);
    }

    /**
     * Updates the profile information of the current user for the personal center.
     *
     * @param formData the user profile form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Updates the profile information of the current user for the personal center.")
    @PutMapping("/profile")
    @Log(value = "Updates the profile information of the current user for the personal center.", module = LogModuleEnum.USER)
    public Result<?> updateUserProfile(@RequestBody UserProfileForm formData) {
        boolean result = userService.updateUserProfile(formData);
        return Result.judge(result);
    }

    /**
     * Resets the password for a specific user by user ID.
     *
     * @param userId   the user ID
     * @param password the new password
     * @return Result indicating success or failure
     */
    @Operation(summary = "Resets the password for a specific user by user ID.")
    @PutMapping(value = "/{userId}/password/reset")
    public Result<?> resetPassword(
            @Parameter(description = "userId") @PathVariable Long userId,
            @RequestParam String password) {
        boolean result = userService.resetPassword(userId, password);
        return Result.judge(result);
    }

    /**
     * Changes the password for the current user.
     *
     * @param data the password update form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Changes the password for the current user.")
    @PutMapping(value = "/password")
    public Result<?> changePassword(
            @RequestBody PasswordUpdateForm data) {

        Long currUserId = SecurityUtils.getUserId();
        boolean result = userService.changePassword(currUserId, data);
        return Result.judge(result);
    }

    /**
     * Retrieves a list of user options for dropdown selection.
     *
     * @return Result containing a list of user options
     */
    @Operation(summary = "Retrieves a list of user options for dropdown selection.")
    @GetMapping("/options")
    public Result<List<Option<String>>> listUserOptions() {
        List<Option<String>> list = userService.listUserOptions();
        return Result.success(list);
    }
}

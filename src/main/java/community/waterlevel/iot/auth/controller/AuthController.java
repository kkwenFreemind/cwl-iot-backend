package community.waterlevel.iot.auth.controller;

import community.waterlevel.iot.auth.model.CaptchaInfo;
import community.waterlevel.iot.common.enums.LogModuleEnum;
import community.waterlevel.iot.common.result.Result;
import community.waterlevel.iot.auth.service.AuthService;
import community.waterlevel.iot.core.security.model.AuthenticationToken;
import community.waterlevel.iot.common.annotation.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * Authentication controller for Community Water Level IoT System.
 * Provides REST APIs for user authentication, login, logout and token
 * management.
 *
 * @author Ray.Hao
 * @since 2022/10/16
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Tag(name = "01. Authentication Center")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Get captcha verification code for authentication.
     *
     * @return Result containing captcha information
     * @since 1.0.0
     */
    @Operation(summary = "Get verification code")
    @GetMapping("/captcha")
    public Result<CaptchaInfo> getCaptcha() {
        CaptchaInfo captcha = authService.getCaptcha();
        return Result.success(captcha);
    }

    /**
     * Authenticate user with username and password.
     *
     * @param username the username for authentication
     * @param password the password for authentication
     * @return Result containing JWT authentication token
     * @since 1.0.0
     */
    @Operation(summary = "Login with username and password")
    @PostMapping("/login")
    @Log(value = "Login", module = LogModuleEnum.LOGIN)
    public Result<AuthenticationToken> login(
            @Parameter(description = "username", example = "admin") @RequestParam String username,
            @Parameter(description = "password", example = "123456") @RequestParam String password) {
        AuthenticationToken authenticationToken = authService.login(username, password);
        return Result.success(authenticationToken);
    }

    /**
     * Log out current user and invalidate session.
     *
     * @return Result indicating logout success
     * @since 1.0.0
     */
    @Operation(summary = "Log out")
    @DeleteMapping("/logout")
    @Log(value = "Log out", module = LogModuleEnum.LOGIN)
    public Result<?> logout() {
        authService.logout();
        return Result.success();
    }

    /**
     * Refresh JWT authentication token using refresh token.
     *
     * @param refreshToken the refresh token for generating new access token
     * @return Result containing new authentication token
     * @since 1.0.0
     */
    @Operation(summary = "Refresh Token")
    @PostMapping("/refresh-token")
    public Result<?> refreshToken(
            @Parameter(description = "Refresh Token", example = "xxx.xxx.xxx") @RequestParam String refreshToken) {
        AuthenticationToken authenticationToken = authService.refreshToken(refreshToken);
        return Result.success(authenticationToken);
    }

}

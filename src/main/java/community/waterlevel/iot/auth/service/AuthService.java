package community.waterlevel.iot.auth.service;

import community.waterlevel.iot.auth.model.CaptchaInfo;
import community.waterlevel.iot.core.security.model.AuthenticationToken;

/**
 * Authentication service interface for Community Water Level IoT System.
 * Defines contract for user authentication, login, logout, captcha, and token
 * management.
 *
 * @author Ray.Hao
 * @since 2.4.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface AuthService {

    /**
     * Authenticate user with username and password.
     *
     * @param username the username for authentication
     * @param password the password for authentication
     * @return JWT authentication token
     */
    AuthenticationToken login(String username, String password);

    /**
     * Log out the current user and invalidate session/token.
     */
    void logout();

    /**
     * Generate and return captcha information for verification.
     *
     * @return CaptchaInfo containing captcha key and image
     */
    CaptchaInfo getCaptcha();

    /**
     * Refresh JWT authentication token using a refresh token.
     *
     * @param refreshToken the refresh token
     * @return new JWT authentication token
     */
    AuthenticationToken refreshToken(String refreshToken);

}

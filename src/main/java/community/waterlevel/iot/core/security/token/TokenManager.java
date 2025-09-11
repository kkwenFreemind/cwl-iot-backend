package community.waterlevel.iot.core.security.token;

import org.springframework.security.core.Authentication;
import community.waterlevel.iot.core.security.model.AuthenticationToken;

/**
 * TokenManager defines the contract for managing authentication tokens in the
 * security module.
 * <p>
 * Implementations of this interface provide methods to generate, parse,
 * validate, refresh, and invalidate tokens for user authentication and session
 * management.
 * It supports both access and refresh tokens, and can be implemented using
 * different storage or token strategies (e.g., JWT, Redis).
 *
 * @author Ray.Hao
 * @since 2.16.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface TokenManager {

    /**
     * Generates an authentication token for the given authentication information.
     *
     * @param authentication the user authentication information
     * @return the authentication token response
     */
    AuthenticationToken generateToken(Authentication authentication);

    /**
     * Parses the token and retrieves the corresponding authentication information.
     *
     * @param token the token string
     * @return the user authentication information
     */
    Authentication parseToken(String token);

    /**
     * Validates whether the given token is valid.
     *
     * @param token the JWT token string
     * @return {@code true} if the token is valid; {@code false} otherwise
     */
    boolean validateToken(String token);

    /**
     * Validates whether the given refresh token is valid.
     *
     * @param refreshToken the JWT refresh token string
     * @return {@code true} if the refresh token is valid; {@code false} otherwise
     */
    boolean validateRefreshToken(String refreshToken);

    /**
     * Refreshes the access token using the given refresh token.
     *
     * @param token the refresh token string
     * @return the authentication token response
     */
    AuthenticationToken refreshToken(String token);

    /**
     * Invalidates the given token, making it unusable for future authentication.
     *
     * @param token the JWT token string
     */
    default void invalidateToken(String token) {
        // Default implementation can be empty or throw an unsupported operation
        // exception
        // throw new UnsupportedOperationException("Not implemented");
    }

}
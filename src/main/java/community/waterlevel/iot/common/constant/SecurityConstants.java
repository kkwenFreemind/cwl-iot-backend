package community.waterlevel.iot.common.constant;

/**
 * Defines constant values for security-related configuration and conventions.
 * Includes API endpoints, token prefixes, and authority naming standards
 * used throughout the authentication and authorization modules.
 *
 * @author Ray.Hao
 * @since 2023/11/24
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface SecurityConstants {

    /**
     * API path for user login endpoint.
     */
    String LOGIN_PATH = "/api/v1/auth/login";

    /**
     * Prefix for Bearer tokens in HTTP Authorization headers.
     * Example: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6..."
     */
    String BEARER_TOKEN_PREFIX = "Bearer ";

    /**
     * Prefix for role authorities in Spring Security.
     * Example: "ROLE_ADMIN"
     */
    String ROLE_PREFIX = "ROLE_";
}

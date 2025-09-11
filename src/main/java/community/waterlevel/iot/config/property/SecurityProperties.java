package community.waterlevel.iot.config.property;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for the security module.
 * Maps security-related settings from the application configuration (prefix "security"),
 * including session management, whitelist paths, unsecured endpoints, and nested JWT/Redis token settings.
 * Supports validation for required fields and value constraints.
 *
 * @author Ray.Hao
 * @since 2024/4/18
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * Session management configuration.
     */
    private SessionConfig session;

    /**
     * Security whitelist paths (completely bypass security filters).
     * <p>
     * Example: {@code /api/v1/auth/login/**}, {@code /ws/**}
     * </p>
     */
    @NotEmpty
    private String[] ignoreUrls;

    /**
     * Non-secure endpoint paths (APIs allowed for anonymous access).
     * <p>
     * Example: {@code /doc.html}, {@code /v3/api-docs/**}
     * </p>
     */
    @NotEmpty
    private String[] unsecuredUrls;

    /**
     * Session configuration nested class.
     */
    @Data
    public static class SessionConfig {
        /**
         * Authentication strategy type.
         * <ul>
         * <li>{@code jwt} - Stateless authentication based on JWT</li>
         * <li>{@code redis-token} - Stateful authentication based on Redis</li>
         * </ul>
         */
        @NotNull
        private String type;

        /**
         * Access token validity period (in seconds).
         * <p>
         * Default: 3600 (1 hour)
         * </p>
         * <p>
         * -1 means never expires
         * </p>
         */
        @Min(-1)
        private Integer accessTokenTimeToLive = 3600;

        /**
         * Refresh token validity period (in seconds).
         * <p>
         * Default: 604800 (7 days)
         * </p>
         * <p>
         * -1 means never expires
         * </p>
         */
        @Min(-1)
        private Integer refreshTokenTimeToLive = 604800;

        /**
         * JWT configuration options.
         */
        private JwtConfig jwt;

        /**
         * Redis token configuration options.
         */
        private RedisTokenConfig redisToken;
    }

    /**
     * JWT configuration nested class.
     */
    @Data
    public static class JwtConfig {
        /**
         * JWT signing secret key.
         * <p>
         * HS256 algorithm requires at least 32 characters.
         * </p>
         * <p>
         * Example: {@code SecretKey012345678901234567890123456789}
         * </p>
         */
        @NotNull
        private String secretKey;
    }

    /**
     * Redis token configuration nested class.
     */
    @Data
    public static class RedisTokenConfig {
        /**
         * Whether to allow multiple devices to log in simultaneously with the same
         * account.
         * <p>
         * {@code true} - Allows multi-device login for the same account (default).
         * </p>
         * <p>
         * {@code false} - New login will invalidate the old token.
         * </p>
         */
        private Boolean allowMultiLogin = true;
    }
}

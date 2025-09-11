package community.waterlevel.iot.common.constant;

/**
 * Defines constant values for Redis key patterns used throughout the application.
 * Organizes keys for rate limiting, distributed locks, authentication, captcha, and system configuration,
 * ensuring consistent and maintainable Redis operations.
 *
 * @author Theo
 * @since 2024-7-29 11:46:08
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
public interface RedisConstants {

    /**
     * Redis keys for rate limiting features.
     */
    interface RateLimiter {
        /**
         * Key for IP-based rate limiting.
         * Format: rate_limiter:ip:{ip}
         */
        String IP = "rate_limiter:ip:{}";
    }

    /**
     * Redis keys for distributed locks.
     */
    interface Lock {
        /**
         * Key for preventing repeated submissions (idempotency lock).
         * Format: lock:resubmit:{userId}:{action}
         */
        String RESUBMIT = "lock:resubmit:{}:{}";
    }

    /**
     * Redis keys for authentication and token management.
     */
    interface Auth {
        /**
         * Key for storing user's access token.
         * Format: auth:token:access:{userId}
         */
        String ACCESS_TOKEN_USER = "auth:token:access:{}";
        /**
         * Key for storing user's refresh token.
         * Format: auth:token:refresh:{userId}
         */
        String REFRESH_TOKEN_USER = "auth:token:refresh:{}";
        /**
         * Key for mapping user to access token.
         * Format: auth:user:access:{token}
         */
        String USER_ACCESS_TOKEN = "auth:user:access:{}";
        /**
         * Key for mapping user to refresh token.
         * Format: auth:user:refresh:{token}
         */
        String USER_REFRESH_TOKEN = "auth:user:refresh:{}";
        /**
         * Key for storing blacklisted (invalidated) tokens.
         * Format: auth:token:blacklist:{token}
         */
        String BLACKLIST_TOKEN = "auth:token:blacklist:{}";
    }

    /**
     * Redis keys for captcha and verification code management.
     */
    interface Captcha {
        /**
         * Key for image captcha code.
         * Format: captcha:image:{uuid}
         */
        String IMAGE_CODE = "captcha:image:{}";
        /**
         * Key for SMS login verification code.
         * Format: captcha:sms_login:{mobile}
         */
        String SMS_LOGIN_CODE = "captcha:sms_login:{}";
        /**
         * Key for SMS registration verification code.
         * Format: captcha:sms_register:{mobile}
         */
        String SMS_REGISTER_CODE = "captcha:sms_register:{}";
        /**
         * Key for mobile verification code.
         * Format: captcha:mobile:{mobile}
         */
        String MOBILE_CODE = "captcha:mobile:{}";
        /**
         * Key for email verification code.
         * Format: captcha:email:{email}
         */
        String EMAIL_CODE = "captcha:email:{}";
    }

    /**
     * Redis keys for system configuration and role permissions.
     */
    interface System {
        /**
         * Key for system configuration cache.
         */
        String CONFIG = "system:config";
        /**
         * Key for role permissions cache.
         */
        String ROLE_PERMS = "system:role:perms";
    }

}


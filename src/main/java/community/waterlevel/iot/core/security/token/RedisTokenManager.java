package community.waterlevel.iot.core.security.token;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import community.waterlevel.iot.common.constant.RedisConstants;
import community.waterlevel.iot.common.exception.BusinessException;
import community.waterlevel.iot.common.result.ResultCode;
import community.waterlevel.iot.config.property.SecurityProperties;
import community.waterlevel.iot.core.security.model.AuthenticationToken;
import community.waterlevel.iot.core.security.model.OnlineUser;
import community.waterlevel.iot.core.security.model.SysUserDetails;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis Token Manager responsible for managing authentication tokens using
 * Redis as the storage backend.
 * <p>
 * This service provides functionality to generate, parse, validate, refresh,
 * and invalidate access and refresh tokens.
 * It supports single-device login control, token-to-user mapping, and token
 * expiration management, leveraging Redis for fast and scalable session
 * handling.
 * <p>
 * The manager is typically used when the session type is set to "redis-token"
 * and integrates with security properties for configuration.
 *
 * @author Ray.Hao
 * @since 2024/11/15
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@ConditionalOnProperty(value = "security.session.type", havingValue = "redis-token")
@Service
public class RedisTokenManager implements TokenManager {

    /**
     * Security properties for token configuration.
     */
    private final SecurityProperties securityProperties;

    /**
     * Redis template for token storage and retrieval.
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Constructs a new {@code RedisTokenManager} with the given security properties and Redis template.
     *
     * @param securityProperties the security properties for token configuration
     * @param redisTemplate      the Redis template for token storage and retrieval
     */
    public RedisTokenManager(SecurityProperties securityProperties, RedisTemplate<String, Object> redisTemplate) {
        this.securityProperties = securityProperties;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Generates a new access and refresh token pair for the given authentication and stores them in Redis.
     *
     * @param authentication the user authentication information
     * @return the generated {@link AuthenticationToken} object
     */
    @Override
    public AuthenticationToken generateToken(Authentication authentication) {
        SysUserDetails user = (SysUserDetails) authentication.getPrincipal();
        String accessToken = IdUtil.fastSimpleUUID();
        String refreshToken = IdUtil.fastSimpleUUID();

        OnlineUser onlineUser = new OnlineUser(
                user.getUserId(),
                user.getUsername(),
                user.getDeptId(),
                user.getDataScope(),
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet())
        );

        // 存储访问令牌、刷新令牌和刷新令牌映射
        storeTokensInRedis(accessToken, refreshToken, onlineUser);

        // 单设备登录控制
        handleSingleDeviceLogin(user.getUserId(), accessToken);

        return AuthenticationToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(securityProperties.getSession().getAccessTokenTimeToLive())
                .build();
    }

    /**
     * Parses the given token and retrieves the corresponding user information from Redis.
     *
     * @param token the access token string
     * @return the constructed {@link Authentication} object, or {@code null} if not found
     */
    @Override
    public Authentication parseToken(String token) {
        OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue().get(formatTokenKey(token));
        if (onlineUser == null) return null;

        Set<SimpleGrantedAuthority> authorities = null;

        Set<String> roles = onlineUser.getRoles();
        if (CollectionUtil.isNotEmpty(roles)) {
            authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }

        SysUserDetails userDetails = buildUserDetails(onlineUser, authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    /**
     * Validates whether the given access token is still valid (exists in Redis).
     *
     * @param token the access token string
     * @return {@code true} if the token is valid; {@code false} otherwise
     */
    @Override
    public boolean validateToken(String token) {
        return redisTemplate.hasKey(formatTokenKey(token));
    }

    /**
     * Validates whether the given refresh token is still valid (exists in Redis).
     *
     * @param refreshToken the refresh token string
     * @return {@code true} if the refresh token is valid; {@code false} otherwise
     */
    @Override
    public boolean validateRefreshToken(String refreshToken) {
        return redisTemplate.hasKey(formatRefreshTokenKey(refreshToken));
    }

    /**
     * Refreshes the access token using the given refresh token.
     *
     * @param refreshToken the refresh token string
     * @return the newly generated {@link AuthenticationToken} object
     */
    @Override
    public AuthenticationToken refreshToken(String refreshToken) {
        OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue().get(StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken));
        if (onlineUser == null) {
            throw new BusinessException(ResultCode.REFRESH_TOKEN_INVALID);
        }

        String oldAccessToken = (String) redisTemplate.opsForValue().get(StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, onlineUser.getUserId()));

        if (oldAccessToken != null) {
            redisTemplate.delete(formatTokenKey(oldAccessToken));
        }

        String newAccessToken = IdUtil.fastSimpleUUID();
        storeAccessToken(newAccessToken, onlineUser);

        int accessTtl = securityProperties.getSession().getAccessTokenTimeToLive();
        return AuthenticationToken.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTtl)
                .build();
    }

    /**
     * Invalidates the given access token and its associated refresh token in Redis.
     *
     * @param token the access token string
     */
    @Override
    public void invalidateToken(String token) {
        OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue().get(formatTokenKey(token));
        if (onlineUser != null) {
            Long userId = onlineUser.getUserId();

            String userAccessKey = StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, userId);
            String accessToken = (String) redisTemplate.opsForValue().get(userAccessKey);
            if (accessToken != null) {
                redisTemplate.delete(formatTokenKey(accessToken));
                redisTemplate.delete(userAccessKey);
            }

            String userRefreshKey = StrUtil.format(RedisConstants.Auth.USER_REFRESH_TOKEN, userId);
            String refreshToken = (String) redisTemplate.opsForValue().get(userRefreshKey);
            if (refreshToken != null) {
                redisTemplate.delete(StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken));
                redisTemplate.delete(userRefreshKey);
            }
        }
    }

    /**
     * Stores the access token, refresh token, and user information in Redis.
     *
     * @param accessToken  the access token string
     * @param refreshToken the refresh token string
     * @param onlineUser   the online user information
     */
    private void storeTokensInRedis(String accessToken, String refreshToken, OnlineUser onlineUser) {
        setRedisValue(formatTokenKey(accessToken), onlineUser, securityProperties.getSession().getAccessTokenTimeToLive());
        String refreshTokenKey = StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken);
        setRedisValue(refreshTokenKey, onlineUser, securityProperties.getSession().getRefreshTokenTimeToLive());
        setRedisValue(StrUtil.format(RedisConstants.Auth.USER_REFRESH_TOKEN, onlineUser.getUserId()),
                refreshToken,
                securityProperties.getSession().getRefreshTokenTimeToLive());
    }

    /**
     * Handles single-device login control by removing old access tokens if multi-login is not allowed.
     *
     * @param userId      the user ID
     * @param accessToken the newly generated access token
     */
    private void handleSingleDeviceLogin(Long userId, String accessToken) {
        Boolean allowMultiLogin = securityProperties.getSession().getRedisToken().getAllowMultiLogin();
        String userAccessKey = StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, userId);

        if (!allowMultiLogin) {
            String oldAccessToken = (String) redisTemplate.opsForValue().get(userAccessKey);
            if (oldAccessToken != null) {
                redisTemplate.delete(formatTokenKey(oldAccessToken));
            }
        }

        setRedisValue(userAccessKey, accessToken, securityProperties.getSession().getAccessTokenTimeToLive());
    }

    /**
     * Stores a new access token and updates the user-to-token mapping in Redis.
     *
     * @param newAccessToken the new access token string
     * @param onlineUser     the online user information
     */
    private void storeAccessToken(String newAccessToken, OnlineUser onlineUser) {
        setRedisValue(StrUtil.format(RedisConstants.Auth.ACCESS_TOKEN_USER, newAccessToken), onlineUser, securityProperties.getSession().getAccessTokenTimeToLive());
        String userAccessKey = StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, onlineUser.getUserId());
        setRedisValue(userAccessKey, newAccessToken, securityProperties.getSession().getAccessTokenTimeToLive());
    }

    /**
     * Builds a {@link SysUserDetails} object from the given online user and authorities.
     *
     * @param onlineUser  the online user information
     * @param authorities the set of granted authorities
     * @return the constructed {@link SysUserDetails} object
     */
    private SysUserDetails buildUserDetails(OnlineUser onlineUser, Set<SimpleGrantedAuthority> authorities) {
        SysUserDetails userDetails = new SysUserDetails();
        userDetails.setUserId(onlineUser.getUserId());
        userDetails.setUsername(onlineUser.getUsername());
        userDetails.setDeptId(onlineUser.getDeptId());
        userDetails.setDataScope(onlineUser.getDataScope());
        userDetails.setAuthorities(authorities);
        return userDetails;
    }

    /**
     * Formats the Redis key for the given access token.
     *
     * @param token the access token string
     * @return the formatted Redis key
     */
    private String formatTokenKey(String token) {
        return StrUtil.format(RedisConstants.Auth.ACCESS_TOKEN_USER, token);
    }

    /**
     * Formats the Redis key for the given refresh token.
     *
     * @param refreshToken the refresh token string
     * @return the formatted Redis key
     */
    private String formatRefreshTokenKey(String refreshToken) {
        return StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken);
    }

    /**
     * Stores a value in Redis with the specified key and time-to-live.
     *
     * @param key   the Redis key
     * @param value the value to store
     * @param ttl   the expiration time in seconds; -1 means never expire
     */
    private void setRedisValue(String key, Object value, int ttl) {
        if (ttl != -1) {
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(key, value); 
        }
    }
}


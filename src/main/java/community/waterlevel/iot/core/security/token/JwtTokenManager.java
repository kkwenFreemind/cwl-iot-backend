package community.waterlevel.iot.core.security.token;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import community.waterlevel.iot.common.constant.JwtClaimConstants;
import community.waterlevel.iot.common.constant.RedisConstants;
import community.waterlevel.iot.common.constant.SecurityConstants;
import community.waterlevel.iot.common.exception.BusinessException;
import community.waterlevel.iot.common.result.ResultCode;
import community.waterlevel.iot.config.property.SecurityProperties;
import community.waterlevel.iot.core.security.model.AuthenticationToken;
import community.waterlevel.iot.core.security.model.SysUserDetails;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * JWT Token Manager responsible for handling all JWT-related operations in the
 * security module.
 * <p>
 * This service provides functionality to generate, parse, validate, refresh,
 * and invalidate JWT tokens for authentication and authorization.
 * It also manages token blacklisting (e.g., for logout or password change
 * scenarios) and supports both access and refresh tokens.
 * <p>
 * The manager integrates with Redis for token blacklisting and uses
 * configurable security properties for token settings.
 *
 * @author Ray.Hao
 * @since 2024/11/15
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@ConditionalOnProperty(value = "security.session.type", havingValue = "jwt")
@Service
public class JwtTokenManager implements TokenManager {

    /**
     * Security properties for token configuration.
     */
    private final SecurityProperties securityProperties;

    /**
     * Redis template for token blacklisting and cache operations.
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Secret key for signing and validating JWT tokens.
     */
    private final byte[] secretKey;

    /**
     * Constructs a new {@code JwtTokenManager} with the given security properties
     * and Redis template.
     *
     * @param securityProperties the security properties for token configuration
     * @param redisTemplate      the Redis template for token cache and blacklist
     */
    public JwtTokenManager(SecurityProperties securityProperties, RedisTemplate<String, Object> redisTemplate) {
        this.securityProperties = securityProperties;
        this.redisTemplate = redisTemplate;
        this.secretKey = securityProperties.getSession().getJwt().getSecretKey().getBytes();
    }

    /**
     * Generates a new access and refresh token pair for the given authentication.
     *
     * @param authentication the authentication information
     * @return the authentication token response object
     */
    @Override
    public AuthenticationToken generateToken(Authentication authentication) {
        int accessTokenTimeToLive = securityProperties.getSession().getAccessTokenTimeToLive();
        int refreshTokenTimeToLive = securityProperties.getSession().getRefreshTokenTimeToLive();

        String accessToken = generateToken(authentication, accessTokenTimeToLive);
        String refreshToken = generateToken(authentication, refreshTokenTimeToLive, true);

        return AuthenticationToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenTimeToLive)
                .build();
    }

    /**
     * Parses the JWT token and returns the corresponding {@link Authentication}
     * object.
     *
     * @param token the JWT token string
     * @return the authentication object parsed from the token
     */
    @Override
    public Authentication parseToken(String token) {

        JWT jwt = JWTUtil.parseToken(token);
        JSONObject payloads = jwt.getPayloads();
        SysUserDetails userDetails = new SysUserDetails();
        userDetails.setUserId(payloads.getLong(JwtClaimConstants.USER_ID)); // 用户ID
        userDetails.setDeptId(payloads.getLong(JwtClaimConstants.DEPT_ID)); // 部门ID
        userDetails.setDataScope(payloads.getInt(JwtClaimConstants.DATA_SCOPE)); // 数据权限范围

        userDetails.setUsername(payloads.getStr(JWTPayload.SUBJECT)); // 用户名

        Set<SimpleGrantedAuthority> authorities = payloads.getJSONArray(JwtClaimConstants.AUTHORITIES)
                .stream()
                .map(authority -> {
                    String authorityStr = Convert.toStr(authority);
                    System.out.println("Processing permissions: " + authority + " -> " + authorityStr);
                    return new SimpleGrantedAuthority(authorityStr);
                })
                .collect(Collectors.toSet());
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    /**
     * Validates the given JWT access token.
     *
     * @param token the JWT token string
     * @return {@code true} if the token is valid; {@code false} otherwise
     */
    @Override
    public boolean validateToken(String token) {
        return validateToken(token, false);
    }

    /**
     * Validates the given JWT refresh token.
     *
     * @param refreshToken the JWT refresh token string
     * @return {@code true} if the refresh token is valid; {@code false} otherwise
     */
    @Override
    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, true);
    }

    /**
     * Validates the given JWT token, optionally as a refresh token.
     *
     * @param token                the JWT token string
     * @param validateRefreshToken whether to validate as a refresh token
     * @return {@code true} if the token is valid; {@code false} otherwise
     */
    private boolean validateToken(String token, boolean validateRefreshToken) {
        try {
            JWT jwt = JWTUtil.parseToken(token);
            boolean isValid = jwt.setKey(secretKey).validate(0);

            if (isValid) {
                JSONObject payloads = jwt.getPayloads();
                String jti = payloads.getStr(JWTPayload.JWT_ID);
                if (validateRefreshToken) {
                    boolean isRefreshToken = payloads.getBool(JwtClaimConstants.TOKEN_TYPE);
                    if (!isRefreshToken) {
                        return false;
                    }
                }
                if (Boolean.TRUE
                        .equals(redisTemplate.hasKey(StrUtil.format(RedisConstants.Auth.BLACKLIST_TOKEN, jti)))) {
                    return false;
                }
            }
            return isValid;
        } catch (Exception gitignore) {

        }
        return false;
    }

    /**
     * Adds the given JWT token to the blacklist, making it invalid for future use.
     *
     * @param token the JWT token string
     */
    @Override
    public void invalidateToken(String token) {
        if (token.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)) {
            token = token.substring(SecurityConstants.BEARER_TOKEN_PREFIX.length());
        }
        JWT jwt = JWTUtil.parseToken(token);
        JSONObject payloads = jwt.getPayloads();
        Integer expirationAt = payloads.getInt(JWTPayload.EXPIRES_AT);

        String blacklistTokenKey = StrUtil.format(RedisConstants.Auth.BLACKLIST_TOKEN,
                payloads.getStr(JWTPayload.JWT_ID));

        if (expirationAt != null) {
            int currentTimeSeconds = Convert.toInt(System.currentTimeMillis() / 1000);
            if (expirationAt < currentTimeSeconds) {
                return;
            }
            int expirationIn = expirationAt - currentTimeSeconds;
            redisTemplate.opsForValue().set(blacklistTokenKey, null, expirationIn, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(blacklistTokenKey, null);
        }
        ;
    }

    /**
     * Refreshes the access token using the given refresh token.
     *
     * @param refreshToken the refresh token string
     * @return the new authentication token response object
     */
    @Override
    public AuthenticationToken refreshToken(String refreshToken) {
        boolean isValid = validateRefreshToken(refreshToken);
        if (!isValid) {
            throw new BusinessException(ResultCode.REFRESH_TOKEN_INVALID);
        }
        Authentication authentication = parseToken(refreshToken);
        int accessTokenExpiration = securityProperties.getSession().getAccessTokenTimeToLive();
        String newAccessToken = generateToken(authentication, accessTokenExpiration);
        return AuthenticationToken.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration)
                .build();
    }

    /**
     * Generates a JWT access token for the given authentication and time-to-live.
     *
     * @param authentication the authentication information
     * @param ttl            the token time-to-live in seconds
     * @return the generated JWT token string
     */
    private String generateToken(Authentication authentication, int ttl) {
        return generateToken(authentication, ttl, false);
    }

    /**
     * Generates a JWT token (access or refresh) for the given authentication and
     * time-to-live.
     *
     * @param authentication the authentication information
     * @param ttl            the token time-to-live in seconds
     * @param isRefreshToken whether the token is a refresh token
     * @return the generated JWT token string
     */
    private String generateToken(Authentication authentication, int ttl, boolean isRefreshToken) {
        SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
        Map<String, Object> payload = new HashMap<>();
        payload.put(JwtClaimConstants.USER_ID, userDetails.getUserId());
        payload.put(JwtClaimConstants.DEPT_ID, userDetails.getDeptId());
        payload.put(JwtClaimConstants.DATA_SCOPE, userDetails.getDataScope());

        Set<String> roles = authentication.getAuthorities().stream()
                .map(authority -> {
                    String authorityStr = authority.getAuthority();
                    System.out.println(
                            "Handling permissions when generating a token: " + authority + " -> " + authorityStr);
                    return authorityStr;
                })
                .collect(Collectors.toSet());

        payload.put(JwtClaimConstants.AUTHORITIES, roles);

        Date now = new Date();
        payload.put(JWTPayload.ISSUED_AT, now);
        payload.put(JwtClaimConstants.TOKEN_TYPE, false);
        if (isRefreshToken) {
            payload.put(JwtClaimConstants.TOKEN_TYPE, true);
        }

        if (ttl != -1) {
            Date expiresAt = DateUtil.offsetSecond(now, ttl);
            payload.put(JWTPayload.EXPIRES_AT, expiresAt);
        }
        payload.put(JWTPayload.SUBJECT, authentication.getName());
        payload.put(JWTPayload.JWT_ID, IdUtil.simpleUUID());

        return JWTUtil.createToken(payload, secretKey);
    }

}

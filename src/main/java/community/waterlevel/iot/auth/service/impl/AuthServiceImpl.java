package community.waterlevel.iot.auth.service.impl;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import community.waterlevel.iot.auth.enums.CaptchaTypeEnum;
import community.waterlevel.iot.auth.model.CaptchaInfo;
import community.waterlevel.iot.auth.service.AuthService;
import community.waterlevel.iot.common.constant.RedisConstants;
import community.waterlevel.iot.common.constant.SecurityConstants;
import community.waterlevel.iot.config.property.CaptchaProperties;
import community.waterlevel.iot.core.security.model.AuthenticationToken;
import community.waterlevel.iot.core.security.token.TokenManager;
import community.waterlevel.iot.core.security.util.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * Authentication service implementation for Community Water Level IoT System.
 * Provides user authentication, login, logout, captcha generation, and token
 * management.
 *
 * @author Ray.Hao
 * @since 2.4.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;

    private final Font captchaFont;
    private final CaptchaProperties captchaProperties;
    private final CodeGenerator codeGenerator;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Authenticate user with username and password.
     *
     * @param username the username for authentication
     * @param password the password for authentication
     * @return JWT authentication token
     */
    @Override
    public AuthenticationToken login(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username.trim(), password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        AuthenticationToken authenticationTokenResponse = tokenManager.generateToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authenticationTokenResponse;
    }

    /**
     * Log out the current user and invalidate session/token.
     */
    @Override
    public void logout() {
        String token = SecurityUtils.getTokenFromRequest();
        if (StrUtil.isNotBlank(token) && token.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)) {
            token = token.substring(SecurityConstants.BEARER_TOKEN_PREFIX.length());
            tokenManager.invalidateToken(token);
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Generate and return captcha information for verification.
     *
     * @return CaptchaInfo containing captcha key and image
     */
    @Override
    public CaptchaInfo getCaptcha() {
        String captchaType = captchaProperties.getType();
        int width = captchaProperties.getWidth();
        int height = captchaProperties.getHeight();
        int interfereCount = captchaProperties.getInterfereCount();
        int codeLength = captchaProperties.getCode().getLength();

        AbstractCaptcha captcha;
        if (CaptchaTypeEnum.CIRCLE.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createCircleCaptcha(width, height, codeLength, interfereCount);
        } else if (CaptchaTypeEnum.GIF.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createGifCaptcha(width, height, codeLength);
        } else if (CaptchaTypeEnum.LINE.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createLineCaptcha(width, height, codeLength, interfereCount);
        } else if (CaptchaTypeEnum.SHEAR.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createShearCaptcha(width, height, codeLength, interfereCount);
        } else {
            throw new IllegalArgumentException("Invalid captcha type: " + captchaType);
        }
        captcha.setGenerator(codeGenerator);
        captcha.setTextAlpha(captchaProperties.getTextAlpha());
        captcha.setFont(captchaFont);

        String captchaCode = captcha.getCode();
        String imageBase64Data = captcha.getImageBase64Data();

        String captchaKey = IdUtil.fastSimpleUUID();
        redisTemplate.opsForValue().set(
                StrUtil.format(RedisConstants.Captcha.IMAGE_CODE, captchaKey),
                captchaCode,
                captchaProperties.getExpireSeconds(),
                TimeUnit.SECONDS);

        return CaptchaInfo.builder()
                .captchaKey(captchaKey)
                .captchaBase64(imageBase64Data)
                .build();
    }

    /**
     * Refresh JWT authentication token using a refresh token.
     *
     * @param refreshToken the refresh token
     * @return new JWT authentication token
     */
    @Override
    public AuthenticationToken refreshToken(String refreshToken) {
        return tokenManager.refreshToken(refreshToken);
    }

}

package community.waterlevel.iot.core.security.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import community.waterlevel.iot.common.constant.SecurityConstants;
import community.waterlevel.iot.common.constant.SystemConstants;
import community.waterlevel.iot.core.security.model.SysUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SecurityUtils provides utility methods for accessing and managing
 * security-related information in the Spring Security context.
 * <p>
 * This class offers convenient static methods to retrieve the current user's
 * details, roles, department, data scope, and authentication token from the
 * security context or HTTP request.
 * It also includes helpers for role checks, such as determining if the current
 * user is a super administrator.
 * <p>
 * Designed for use throughout the application wherever security context
 * information is needed.
 *
 * @author Ray
 * @since 2021/1/10
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public class SecurityUtils {

    /**
     * Retrieves the current authenticated user's details from the Spring Security
     * context.
     *
     * @return an {@link Optional} containing {@link SysUserDetails} if present,
     *         otherwise empty
     */
    public static Optional<SysUserDetails> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof SysUserDetails) {
                return Optional.of((SysUserDetails) principal);
            }
        }
        return Optional.empty();
    }

    /**
     * Retrieves the user ID of the currently authenticated user.
     *
     * @return the user ID as a {@link Long}, or {@code null} if not available
     */
    public static Long getUserId() {
        return getUser().map(SysUserDetails::getUserId).orElse(null);
    }

    /**
     * Retrieves the username of the currently authenticated user.
     *
     * @return the username as a {@link String}, or {@code null} if not available
     */
    public static String getUsername() {
        return getUser().map(SysUserDetails::getUsername).orElse(null);
    }

    /**
     * Retrieves the department ID of the currently authenticated user.
     *
     * @return the department ID as a {@link Long}, or {@code null} if not available
     */
    public static Long getDeptId() {
        return getUser().map(SysUserDetails::getDeptId).orElse(null);
    }

    /**
     * Retrieves the data scope of the currently authenticated user.
     *
     * @return the data scope as an {@link Integer}, or {@code null} if not
     *         available
     */
    public static Integer getDataScope() {
        return getUser().map(SysUserDetails::getDataScope).orElse(null);
    }

    /**
     * Retrieves the set of role names assigned to the currently authenticated user.
     * <p>
     * Only authorities with the {@code ROLE_} prefix are considered as roles.
     * The prefix is removed in the returned set.
     * </p>
     *
     * @return a set of role names without the {@code ROLE_} prefix; never
     *         {@code null}
     */
    public static Set<String> getRoles() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getAuthorities)
                .filter(CollectionUtil::isNotEmpty)
                .stream()
                .flatMap(Collection::stream)
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(SecurityConstants.ROLE_PREFIX))
                .map(authority -> StrUtil.removePrefix(authority, SecurityConstants.ROLE_PREFIX))
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves the set of role codes assigned to the currently authenticated user.
     * <p>
     * Only authorities with the {@code ROLE_} prefix are considered as roles. The
     * prefix is removed
     * and the remaining string is treated as the role code (corresponding to the
     * {@code code} field in the role entity).
     * </p>
     *
     * @return a set of role codes; never {@code null}
     */
    public static Set<String> getRoleCodes() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Collections.emptySet();
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (CollectionUtil.isEmpty(authorities)) {
            return Collections.emptySet();
        }

        Set<String> roleCodes = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            String authorityName = authority.getAuthority();
            // 只處理角色權限（以 ROLE_ 開頭）
            if (authorityName.startsWith(SecurityConstants.ROLE_PREFIX)) {
                // 移除 ROLE_ 前綴，獲取角色代碼
                String roleCode = StrUtil.removePrefix(authorityName, SecurityConstants.ROLE_PREFIX);
                roleCodes.add(roleCode);
            }
        }

        return roleCodes;
    }

    /**
     * Determines whether the currently authenticated user is a super administrator.
     * <p>
     * Super administrators bypass all permission checks.
     * </p>
     *
     * @return {@code true} if the user has the root role code; {@code false}
     *         otherwise
     */
    public static boolean isRoot() {
        Set<String> roles = getRoles();
        return roles.contains(SystemConstants.ROOT_ROLE_CODE);
    }

    /**
     * Retrieves the authentication token from the current HTTP request's
     * {@code Authorization} header.
     *
     * @return the token string from the {@code Authorization} header, or
     *         {@code null} if not present
     */
    public static String getTokenFromRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

}

package community.waterlevel.iot.core.security.model;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import community.waterlevel.iot.common.constant.SecurityConstants;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * User authentication and authorization model for Spring Security.
 * Implements {@link UserDetails} to provide user identity, credentials, status, and authorities.
 * Encapsulates user information and roles for authentication and access control.
 *
 * @author Ray.Hao
 * @version 3.0.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
@NoArgsConstructor
public class SysUserDetails implements UserDetails {

    /**
     * Unique identifier of the user.
     */
    private Long userId;

    /**
     * Username of the user.
     */
    private String username;

    /**
     * Password of the user.
     */
    private String password;

    /**
     * Whether the account is enabled (true: enabled, false: disabled).
     */
    private Boolean enabled;

    /**
     * Department ID to which the user belongs.
     */
    private Long deptId;

    /**
     * Data access scope for the user.
     */
    private Integer dataScope;

    /**
     * Collection of user role authorities.
     */
    private Collection<SimpleGrantedAuthority> authorities;

    /**
     * Constructs a {@code SysUserDetails} object from the given user authentication
     * credentials.
     * <p>
     * Initializes user details and authorities for use by Spring Security.
     * </p>
     *
     * @param user the user authentication credentials ({@link UserAuthCredentials})
     */
    public SysUserDetails(UserAuthCredentials user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = ObjectUtil.equal(user.getStatus(), 1);
        this.deptId = user.getDeptId();
        this.dataScope = user.getDataScope();

        // Initialize role authorities collection
        this.authorities = CollectionUtil.isNotEmpty(user.getRoles())
                ? user.getRoles().stream()
                        // Prefix role names with "ROLE_" to distinguish roles (ROLE_ADMIN) from
                        // permissions (user:add)
                        .map(role -> new SimpleGrantedAuthority(SecurityConstants.ROLE_PREFIX + role))
                        .collect(Collectors.toSet())
                : Collections.emptySet();
    }

    /**
     * Returns the authorities granted to the user.
     *
     * @return the authorities, not null
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return the username
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return {@code true} if the user is enabled, {@code false} otherwise
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}

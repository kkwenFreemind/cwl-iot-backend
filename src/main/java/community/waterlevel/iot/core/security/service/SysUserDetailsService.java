package community.waterlevel.iot.core.security.service;

import community.waterlevel.iot.core.security.model.SysUserDetails;
import community.waterlevel.iot.core.security.model.UserAuthCredentials;
import community.waterlevel.iot.system.service.UserJpaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * UserDetailsService implementation for system user authentication.
 * Loads user details by username for Spring Security authentication,
 * converting user credentials to {@link SysUserDetails} for authorization and
 * access control.
 * Integrates with the application's user service for credential retrieval.
 *
 * @author Ray.Hao
 * @since 2021/10/19
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysUserDetailsService implements UserDetailsService {

    private final UserJpaService userService;

    /**
     * Loads user details by username for authentication.
     * Retrieves user credentials from the user service and constructs a
     * {@link SysUserDetails} object.
     * Throws {@link UsernameNotFoundException} if the user does not exist.
     *
     * @param username the username to look up
     * @return the user details for authentication
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            UserAuthCredentials userAuthCredentials = userService.getAuthCredentialsByUsername(username);
            if (userAuthCredentials == null) {
                throw new UsernameNotFoundException(username);
            }
            return new SysUserDetails(userAuthCredentials);
        } catch (Exception e) {
            log.error("Authentication exception:{}", e.getMessage());
            throw e;
        }
    }
}

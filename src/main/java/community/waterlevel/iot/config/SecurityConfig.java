package community.waterlevel.iot.config;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.ArrayUtil;
import community.waterlevel.iot.config.property.SecurityProperties;
import community.waterlevel.iot.core.filter.RateLimiterFilter;
import community.waterlevel.iot.core.security.exception.MyAccessDeniedHandler;
import community.waterlevel.iot.core.security.exception.MyAuthenticationEntryPoint;
import community.waterlevel.iot.core.security.filter.CaptchaValidationFilter;
import community.waterlevel.iot.core.security.filter.TokenAuthenticationFilter;
import community.waterlevel.iot.core.security.token.TokenManager;
import community.waterlevel.iot.core.security.service.SysUserDetailsService;
import community.waterlevel.iot.system.service.ConfigJpaService;
import community.waterlevel.iot.system.service.UserJpaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Main configuration class for Spring Security.
 * Sets up stateless, token-based authentication, custom filters (rate limiting, CAPTCHA, token validation),
 * and configures public and secured endpoints using application properties.
 * Integrates custom authentication providers and manages the security filter chain.
 *
 * @author Ray.Hao
 * @since 2023/2/17
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11 
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final TokenManager tokenManager;
    private final SysUserDetailsService userDetailsService;
    private final CodeGenerator codeGenerator;
    private final ConfigJpaService configService;
    private final SecurityProperties securityProperties;

    @Autowired(required = false)

    /**
     * Constructs the SecurityConfig with all required dependencies.
     *
     * @param redisTemplate      the Redis template for caching and session
     *                           management
     * @param passwordEncoder    the password encoder for authentication
     * @param tokenManager       the token manager for JWT or token-based
     *                           authentication
     * @param userService        the user service for user management
     * @param userDetailsService the user details service for loading user-specific
     *                           data
     * @param codeGenerator      the CAPTCHA code generator
     * @param configService      the configuration service for system settings
     * @param securityProperties the security configuration properties
     */
    public SecurityConfig(RedisTemplate<String, Object> redisTemplate,
            PasswordEncoder passwordEncoder,
            TokenManager tokenManager,
            UserJpaService userService,
            SysUserDetailsService userDetailsService,
            CodeGenerator codeGenerator,
            ConfigJpaService configService,
            SecurityProperties securityProperties) {
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;
        this.tokenManager = tokenManager;
        this.userDetailsService = userDetailsService;
        this.codeGenerator = codeGenerator;
        this.configService = configService;
        this.securityProperties = securityProperties;
    }

    /**
     * Configures the main Spring Security filter chain for the application.
     * <p>
     * Sets up stateless, token-based authentication, disables default form login
     * and CSRF, and adds custom filters for rate limiting, CAPTCHA validation, and
     * token authentication.
     * Public endpoints are configured via the security properties.
     *
     * @param http the {@link HttpSecurity} builder
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if a security configuration error occurs
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests(requestMatcherRegistry -> {
                    // Configure public endpoints that do not require authentication
                    String[] ignoreUrls = securityProperties.getIgnoreUrls();
                    if (ArrayUtil.isNotEmpty(ignoreUrls)) {
                        requestMatcherRegistry.requestMatchers(ignoreUrls).permitAll();
                    }
                    // All other requests require authentication
                    requestMatcherRegistry.anyRequest().authenticated();
                })
                .exceptionHandling(configurer -> configurer
                        .authenticationEntryPoint(new MyAuthenticationEntryPoint()) // Handler for unauthenticated
                                                                                    // access
                        .accessDeniedHandler(new MyAccessDeniedHandler()) // Handler for access denied exceptions
                )

                // Disable default Spring Security features for stateless, front-end/back-end
                // separated architecture
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless
                                                                                                                   // authentication,
                                                                                                                   // no
                                                                                                                   // session
                )
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                .formLogin(AbstractHttpConfigurer::disable) // Disable default form login
                .httpBasic(AbstractHttpConfigurer::disable) // Disable HTTP Basic authentication
                // Disable X-Frame-Options header to allow embedding in iframes
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                // Add rate limiting filter
                .addFilterBefore(new RateLimiterFilter(redisTemplate, configService),
                        UsernamePasswordAuthenticationFilter.class)
                // Add CAPTCHA validation filter
                .addFilterBefore(new CaptchaValidationFilter(redisTemplate, codeGenerator),
                        UsernamePasswordAuthenticationFilter.class)
                // Add token authentication filter
                .addFilterBefore(new TokenAuthenticationFilter(tokenManager),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Configures a customizer to ignore security checks for specified request
     * paths.
     * <p>
     * This is typically used to exclude static resources or documentation endpoints
     * from the security filter chain.
     *
     * @return the {@link WebSecurityCustomizer} bean
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            String[] unsecuredUrls = securityProperties.getUnsecuredUrls();
            if (ArrayUtil.isNotEmpty(unsecuredUrls)) {
                web.ignoring().requestMatchers(unsecuredUrls);
            }
        };
    }

    /**
     * Configures the default password authentication provider.
     * <p>
     * Uses the application's password encoder and user details service for
     * authentication.
     *
     * @return the {@link DaoAuthenticationProvider} bean
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    /**
     * Configures the authentication manager with the password authentication provider.
     * <p>
     * Creates an authentication manager with the default DAO-based password authentication.
     * The IoT system currently supports only username/password authentication.
     *
     * @param daoAuthenticationProvider the password authentication provider
     * @return the configured {@link AuthenticationManager}
     */
    @Bean
    public AuthenticationManager authenticationManager(
            DaoAuthenticationProvider daoAuthenticationProvider) {
        // Create a list with the password authentication provider
        java.util.List<org.springframework.security.authentication.AuthenticationProvider> providers = new java.util.ArrayList<>();
        providers.add(daoAuthenticationProvider);

        return new ProviderManager(providers);
    }
}

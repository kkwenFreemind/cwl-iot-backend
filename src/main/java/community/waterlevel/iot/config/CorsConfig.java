package community.waterlevel.iot.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

/**
 * Configuration class for Cross-Origin Resource Sharing (CORS).
 * Registers a global CORS filter to allow requests from any origin, header, and
 * method,
 * and supports credentials for cross-domain requests.
 *
 * @author haoxr
 * @since 2023/4/17
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Configuration
public class CorsConfig {

    /**
     * Registers a global CORS filter bean.
     * <p>
     * Allows any origin, header, and method, and supports credentials for all endpoints. The filter order is set to ensure it runs before Spring Security filters.
     *
     * @return the configured {@link FilterRegistrationBean} for CORS
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 1. Allow any origin
        corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
        // 2. Allow any request header
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        // 3. Allow any HTTP method
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        // 4. Allow credentials
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        CorsFilter corsFilter = new CorsFilter(source);

        FilterRegistrationBean<CorsFilter> filterRegistrationBean = new FilterRegistrationBean<>(corsFilter);
        // Set order less than Spring Security Filter's order (-100)
        filterRegistrationBean.setOrder(-101);

        return filterRegistrationBean;
    }
}
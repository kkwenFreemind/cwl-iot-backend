package community.waterlevel.iot.config;

import cn.hutool.core.util.ArrayUtil;
import community.waterlevel.iot.config.property.SecurityProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.util.AntPathMatcher;

import java.util.stream.Stream;

/**
 * OpenAPI documentation configuration for the management system.
 * <p>
 * Configures the OpenAPI (Swagger) documentation, including API metadata,
 * global security schemes, and custom extensions.
 * </p>
 *
 * @author Ray.Hao
 * @since 2023/2/17
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class OpenApiConfig {

    private final Environment environment;

    private final SecurityProperties securityProperties;

    /**
     * Defines the OpenAPI documentation metadata and global security scheme for the
     * management system APIs.
     * <p>
     * Sets up API title, description, version, license, contact, and configures
     * JWT-based authorization as a global security scheme.
     *
     * @return the configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI openApi() {

        String appVersion = environment.getProperty("project.version", "1.0.0");

        return new OpenAPI()
                .info(new Info()
                        .title("Community Water Level IOT API Documentation")
                        .description("This document covers all API endpoints.")
                        .version(appVersion)
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0"))
                        .contact(new Contact()
                                .name("Chang Xiu-Wen")
                                .email("kkwen.freemind@gmail.com")
                                .url("https://github.com/kkwenFreemind")))
                // Configure global authorization parameter
                .components(new Components()
                        .addSecuritySchemes(HttpHeaders.AUTHORIZATION,
                                new SecurityScheme()
                                        .name(HttpHeaders.AUTHORIZATION)
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .scheme("Bearer")
                                        .bearerFormat("JWT")));
    }

    /**
     * Global customizer for OpenAPI documentation.
     * <p>
     * Adds the {@code Authorization} security requirement to all API operations
     * except those matching the security whitelist.
     * This ensures that endpoints requiring authentication are properly documented
     * with security requirements in Swagger UI.
     *
     * @return the {@link GlobalOpenApiCustomizer} bean
     */
    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer() {
        return openApi -> {
            // Globally add Authorization except for whitelisted paths
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((path, pathItem) -> {

                    // Skip adding Authorization for whitelisted (ignored) paths
                    String[] ignoreUrls = securityProperties.getIgnoreUrls();
                    if (ArrayUtil.isNotEmpty(ignoreUrls)) {
                        // Use AntPathMatcher to match ignored paths
                        AntPathMatcher antPathMatcher = new AntPathMatcher();
                        if (Stream.of(ignoreUrls).anyMatch(ignoreUrl -> antPathMatcher.match(ignoreUrl, path))) {
                            return;
                        }
                    }

                    // Add Authorization requirement to all other operations
                    pathItem.readOperations()
                            .forEach(operation -> operation
                                    .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION)));
                });
            }
        };
    }

}

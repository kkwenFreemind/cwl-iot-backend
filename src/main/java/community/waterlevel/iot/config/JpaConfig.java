package community.waterlevel.iot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA configuration class for Spring Data JPA integration.
 * <p>
 * Enables JPA repositories, auditing, and transaction management for the
 * application.
 * Additional JPA-related configuration can be added here as needed.
 * </p>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Configuration
@EnableJpaRepositories(basePackages = {
    "community.waterlevel.iot.system.repository",
    "community.waterlevel.iot.module.device.repository"
})
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {

    // Additional JPA-related configuration can be added here

}

package community.waterlevel.iot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for password encoding.
 * Defines a bean for {@link PasswordEncoder} using BCrypt hashing algorithm,
 * enabling secure password storage and verification.
 *
 * @author Ray.Hao
 * @since 2024/12/3
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Bean for password encoding using BCrypt.
     * <p>
     * Returns a {@link PasswordEncoder} implementation that uses the BCrypt hashing
     * algorithm for secure password storage.
     *
     * @return the {@link PasswordEncoder} bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

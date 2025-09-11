package community.waterlevel.iot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Configuration class for Redis integration.
 * Defines a custom {@link RedisTemplate} bean with string and JSON serializers
 * for keys, values, and hash structures to improve interoperability and
 * readability.
 *
 * @author Ray.Hao
 * @since 2023/5/15
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Configuration
public class RedisConfig {

    /**
     * Defines a custom {@link RedisTemplate} bean.
     * <p>
     * Configures the template to use string serialization for keys and hash keys,
     * and JSON serialization for values and hash values, instead of the default JDK
     * serialization.
     *
     * @param redisConnectionFactory the Redis connection factory
     * @return the configured {@link RedisTemplate}
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.json());

        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}

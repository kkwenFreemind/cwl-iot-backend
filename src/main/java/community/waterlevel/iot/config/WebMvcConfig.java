package community.waterlevel.iot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

/**
 * Web MVC configuration class.
 * Customizes message converters for JSON serialization (including date/time and large number handling)
 * and configures the bean validation framework for fail-fast validation and Spring integration.
 *
 * @author Ray.Hao
 * @since 2020/10/16
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11 
 */
@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Configures custom message converters for HTTP requests and responses.
     * <p>
     * Sets up Jackson to handle Java 8 date/time types, global date format, time
     * zone, and serializes Long/BigInteger as strings to avoid precision loss in
     * JavaScript.
     *
     * @param converters the list of message converters to configure
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));
        objectMapper.registerModule(javaTimeModule);

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);

        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(1, jackson2HttpMessageConverter);
    }

    /**
     * Configures a custom bean validator for the application.
     * <p>
     * Uses Hibernate Validator with fail-fast mode enabled (returns on the first
     * validation failure) and integrates with Spring's dependency injection for
     * constraint validators.
     *
     * @param autowireCapableBeanFactory the bean factory for injecting dependencies
     *                                   into validators
     * @return the configured {@link Validator} instance
     */
    @Bean
    public Validator validator(final AutowireCapableBeanFactory autowireCapableBeanFactory) {
        try (ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(true) // failFast=true: return on first validation failure; false: validate all
                                // parameters
                .constraintValidatorFactory(new SpringConstraintValidatorFactory(autowireCapableBeanFactory))
                .buildValidatorFactory()) {

            // Ensure ValidatorFactory is properly closed using try-with-resources
            return validatorFactory.getValidator();
        }
    }
}

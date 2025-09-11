package community.waterlevel.iot.config;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import community.waterlevel.iot.config.property.CaptchaProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;

/**
 * Auto-configuration class for captcha generation.
 * Defines beans for captcha code generators and font configuration,
 * using properties from {@link CaptchaProperties}.
 * Supports both math and random captcha types.
 *
 * @author haoxr
 * @since 2023/11/24
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Configuration
public class CaptchaConfig {

    /**
     * CAPTCHA configuration properties injected from application settings.
     */
    @Autowired
    private CaptchaProperties captchaProperties;

    /**
     * Bean for CAPTCHA code generator.
     * <p>
     * Creates a code generator based on the configured type and length. Supports
     * arithmetic (math) and random string types.
     *
     * @return the configured {@link CodeGenerator}
     * @throws IllegalArgumentException if the code type is invalid
     */
    @Bean
    public CodeGenerator codeGenerator() {
        String codeType = captchaProperties.getCode().getType();
        int codeLength = captchaProperties.getCode().getLength();
        if ("math".equalsIgnoreCase(codeType)) {
            return new MathGenerator(codeLength);
        } else if ("random".equalsIgnoreCase(codeType)) {
            return new RandomGenerator(codeLength);
        } else {
            throw new IllegalArgumentException("Invalid captcha codegen type: " + codeType);
        }
    }

    /**
     * Bean for CAPTCHA font configuration.
     * <p>
     * Creates a {@link Font} instance based on the configured font name, style, and
     * size.
     *
     * @return the configured {@link Font}
     */
    @Bean
    public Font captchaFont() {
        String fontName = captchaProperties.getFont().getName();
        int fontSize = captchaProperties.getFont().getSize();
        int fontWight = captchaProperties.getFont().getWeight();
        return new Font(fontName, fontWight, fontSize);
    }

}
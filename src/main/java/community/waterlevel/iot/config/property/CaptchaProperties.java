package community.waterlevel.iot.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for captcha generation and validation.
 * Supports customization of captcha type, image size, interference, text style,
 * expiration,
 * and nested configuration for code and font properties.
 * Mapped from properties with the prefix "captcha" in the application
 * configuration.
 *
 * @author haoxr
 * @since 2023/11/24
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Component
@ConfigurationProperties(prefix = "captcha")
@Data
public class CaptchaProperties {

    /**
     * CAPTCHA type. Supported values: {@code circle} (circle interference),
     * {@code gif} (GIF CAPTCHA),
     * {@code line} (line interference), {@code shear} (distorted interference).
     */
    private String type;

    /**
     * CAPTCHA image width (in pixels).
     */
    private int width;
    /**
     * CAPTCHA image height (in pixels).
     */
    private int height;

    /**
     * Number of interference lines in the CAPTCHA image.
     */
    private int interfereCount;

    /**
     * Text opacity (alpha) for the CAPTCHA characters.
     */
    private Float textAlpha;

    /**
     * CAPTCHA expiration time in seconds.
     */
    private Long expireSeconds;

    /**
     * CAPTCHA code character configuration.
     */
    private CodeProperties code;

    /**
     * CAPTCHA font configuration.
     */
    private FontProperties font;

    /**
     * Configuration for CAPTCHA code characters.
     */
    @Data
    public static class CodeProperties {
        /**
         * CAPTCHA code type. Supported values: {@code math} (arithmetic),
         * {@code random} (random string).
         */
        private String type;
        /**
         * CAPTCHA code length. If {@code type=math}, represents the number of digits in
         * the arithmetic operation (1: single digit, 2: double digit).
         * If {@code type=random}, represents the number of characters.
         */
        private int length;
    }

    /**
     * Configuration for CAPTCHA font settings.
     */
    @Data
    public static class FontProperties {
        /**
         * Font name.
         */
        private String name;
        /**
         * Font style. {@code 0} - normal, {@code 1} - bold, {@code 2} - italic.
         */
        private int weight;
        /**
         * Font size (in points).
         */
        private int size;
    }

}

package community.waterlevel.iot.common.enums;

import community.waterlevel.iot.common.base.IBaseEnum;
import lombok.Getter;

/**
 * Enumeration representing application runtime environments.
 * Used to distinguish between different deployment stages such as development and production.
 * Facilitates environment-specific configuration and logic.
 *
 * @author Ray
 * @since 4.0.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Getter
public enum EnvEnum implements IBaseEnum<String> {

    /**
     * Development environment.
     */
    DEV("dev", "Development environment"),

    /**
     * Production environment.
     */
    PROD("prod", "Production environment");

    /**
     * String value representing the environment.
     */
    private final String value;

    /**
     * Label/description of the environment.
     */
    private final String label;

    /**
     * Constructor for EnvEnum.
     * 
     * @param value string value of the environment
     * @param label label/description of the environment
     */
    EnvEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }
}

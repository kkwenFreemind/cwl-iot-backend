package community.waterlevel.iot.common.enums;

import community.waterlevel.iot.common.base.IBaseEnum;
import lombok.Getter;

/**
 * Enumeration representing the enabled/disabled status of an entity.
 * Provides both a numeric value and a descriptive label for each status.
 * Commonly used for logical deletion, activation, or availability flags.
 *
 * @author haoxr
 * @since 2022/10/14
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Getter
public enum StatusEnum implements IBaseEnum<Integer> {

    /**
     * Enabled status.
     */
    ENABLE(1, "Enabled"),

    /**
     * Disabled status.
     */
    DISABLE(0, "Disabled");

    /**
     * Numeric value representing the status.
     */
    private final Integer value;

    /**
     * Label/description of the status.
     */
    private final String label;

    /**
     * Constructor for StatusEnum.
     * 
     * @param value numeric value of the status
     * @param label label/description of the status
     */
    StatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}

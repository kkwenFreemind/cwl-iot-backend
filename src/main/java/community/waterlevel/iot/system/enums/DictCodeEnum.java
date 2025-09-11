package community.waterlevel.iot.system.enums;

import community.waterlevel.iot.common.base.IBaseEnum;
import lombok.Getter;

/**
 * DictCodeEnum is an enumeration of dictionary codes used throughout the
 * system.
 * <p>
 * Each enum constant represents a specific dictionary type (e.g., gender,
 * notice type, notice level) with a unique value and label for display and
 * programmatic access.
 * Implements {@link IBaseEnum} for standardized enum handling.
 *
 * @author Ray.Hao
 * @since 2024/10/30
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Getter
public enum DictCodeEnum implements IBaseEnum<String> {

    /**
     * Gender classification dictionary.
     * Used for user profile gender options and demographic data.
     */
    GENDER("gender", "Gender"),

    /**
     * Notice type classification dictionary.
     * Categorizes different types of system notifications and announcements.
     */
    NOTICE_TYPE("notice_type", "Notice Type"),

    /**
     * Notice priority level dictionary.
     * Defines importance levels for notification prioritization and display.
     */
    NOTICE_LEVEL("notice_level", "Notice Priority");

    /**
     * Dictionary code identifier for database storage.
     * Unique string key used to reference this dictionary category.
     */
    private final String value;

    /**
     * Human-readable display label for the dictionary category.
     * Localized description shown in user interfaces.
     */
    private final String label;

    /**
     * Constructor for dictionary code enumeration values.
     *
     * @param value the unique code identifier for database storage
     * @param label the display label for user interface presentation
     */
    DictCodeEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

}

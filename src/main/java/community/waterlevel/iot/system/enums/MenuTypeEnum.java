package community.waterlevel.iot.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

import community.waterlevel.iot.common.base.IBaseEnum;
import lombok.Getter;

/**
 * MenuTypeEnum is an enumeration of menu types used in the system's navigation
 * and permission model.
 * <p>
 * Each enum constant represents a specific menu type (e.g., menu, catalog,
 * external link, button) with a unique value and label for display and
 * programmatic access.
 * Implements {@link IBaseEnum} for standardized enum handling and supports
 * MyBatis-Plus integration for database mapping.
 *
 * @author Ray.Hao
 * @since 2022/4/23 9:36
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Getter
public enum MenuTypeEnum implements IBaseEnum<Integer> {

    /**
     * Null/undefined menu type for default or unspecified cases.
     * Used as a placeholder when menu type is not applicable.
     */
    NULL(0, "NULL"),

    /**
     * Standard menu item for navigation.
     * Represents a clickable menu entry that navigates to a specific page or view.
     */
    MENU(1, "MENU"),

    /**
     * Directory/catalog for organizing menu structure.
     * Groups related menu items under a common category without direct navigation.
     */
    CATALOG(2, "CATALOG"),

    /**
     * External link menu item.
     * Links to external websites or resources outside the application.
     */
    EXTLINK(3, "EXTLINK"),

    /**
     * Action button for specific operations.
     * Triggers specific functions or commands rather than navigation.
     */
    BUTTON(4, "BUTTON");

    /**
     * Numeric type identifier for database storage.
     * MyBatis-Plus annotation ensures this value is stored in the database.
     */
    @EnumValue
    private final Integer value;

    /**
     * Human-readable display label for the menu type.
     * Localized description shown in administrative interfaces.
     */
    private final String label;

    /**
     * Constructor for menu type enumeration values.
     *
     * @param value the numeric identifier for database storage
     * @param label the display label for user interface presentation
     */
    MenuTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

}
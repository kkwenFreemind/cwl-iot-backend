package community.waterlevel.iot.system.enums;

import community.waterlevel.iot.common.base.IBaseEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * NoticeTargetEnum is an enumeration of target types for system notices and
 * announcements.
 * <p>
 * Each enum constant represents a specific target audience (e.g., all users,
 * specified users) with a unique value and label for display and programmatic
 * access.
 * Implements {@link IBaseEnum} for standardized enum handling and is annotated
 * for OpenAPI/Swagger documentation.
 *
 * @author Ray.Hao
 * @since 2024/10/14
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Getter
@Schema(enumAsRef = true)
public enum NoticeTargetEnum implements IBaseEnum<Integer> {

    /**
     * Broadcast delivery to all system users.
     * Notice will be delivered to every active user in the system.
     */
    ALL(1, "ALL"),

    /**
     * Targeted delivery to specified users only.
     * Notice will be delivered only to users listed in the target user IDs.
     */
    SPECIFIED(2, "SPECIFIED");

    /**
     * Numeric target type identifier for database storage.
     * Integer code representing the notification delivery scope.
     */
    private final Integer value;

    /**
     * Human-readable target type description for user interfaces.
     * Localized label displayed in notification management screens.
     */
    private final String label;

    /**
     * Constructor for notice target enumeration values.
     *
     * @param value the numeric target type code for database storage
     * @param label the display label for user interface presentation
     */
    NoticeTargetEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}


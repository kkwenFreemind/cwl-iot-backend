package community.waterlevel.iot.system.enums;

import community.waterlevel.iot.common.base.IBaseEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * NoticePublishStatusEnum is an enumeration of publish statuses for system
 * notices and announcements.
 * <p>
 * Each enum constant represents a specific publish state (e.g., unpublished,
 * published, revoked) with a unique value and label for display and
 * programmatic access.
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
public enum NoticePublishStatusEnum implements IBaseEnum<Integer> {

    /**
     * Unpublished/draft status for notices under preparation.
     * Notice is created but not yet made available to users.
     */
    UNPUBLISHED(0, "Unpublished"),

    /**
     * Published status for active notices.
     * Notice is live and visible to target users or all users.
     */
    PUBLISHED(1, "Published"),

    /**
     * Revoked status for withdrawn notices.
     * Previously published notice has been retracted from public view.
     */
    REVOKED(-1, "Revoked");

    /**
     * Numeric status code for database storage and API responses.
     * Integer identifier representing the publication state.
     */
    private final Integer value;

    /**
     * Human-readable status description for user interfaces.
     * Localized label displayed in administrative and user-facing screens.
     */
    private final String label;

    /**
     * Constructor for notice publication status enumeration values.
     *
     * @param value the numeric status code for database storage
     * @param label the display label for user interface presentation
     */
    NoticePublishStatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}

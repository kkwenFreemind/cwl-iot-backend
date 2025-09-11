package community.waterlevel.iot.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Enumeration representing different functional modules for system logging.
 * Used to categorize and identify the source or context of log entries,
 * such as exception handling, authentication, user management, and system configuration.
 *
 * @author Ray
 * @since 2.10.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(enumAsRef = true)
@Getter
public enum LogModuleEnum {

    /**
     * Exception log module.
     */
    EXCEPTION("Exception"),

    /**
     * Login log module.
     */
    LOGIN("Login"),

    /**
     * User management log module.
     */
    USER("User"),

    /**
     * Department management log module.
     */
    DEPT("Department"),

    /**
     * Role management log module.
     */
    ROLE("Role"),

    /**
     * Menu management log module.
     */
    MENU("Menu"),

    /**
     * Dictionary management log module.
     */
    DICT("Dictionary"),

    /**
     * System settings log module.
     */
    SETTING("System settings"),

    /**
     * Other log modules.
     */
    OTHER("Other");

    /**
     * Display name of the log module.
     */
    @JsonValue
    private final String moduleName;

    /**
     * Constructor for LogModuleEnum.
     * 
     * @param moduleName display name of the log module
     */
    LogModuleEnum(String moduleName) {
        this.moduleName = moduleName;
    }
}
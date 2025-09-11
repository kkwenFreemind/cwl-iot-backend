package community.waterlevel.iot.common.constant;

/**
 * Defines global system-wide constant values used throughout the application.
 * Includes default settings, root identifiers, role codes, and configuration keys
 * for consistent reference and maintainability.
 *
 * @author Ray.Hao
 * @since 1.0.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
public interface SystemConstants {

    /**
     * The root node ID for hierarchical data structures (e.g., organization tree
     * root).
     */
    Long ROOT_NODE_ID = 0L;

    /**
     * The default password for new users or system initialization.
     */
    String DEFAULT_PASSWORD = "123456";

    /**
     * The code for the root (super admin) role.
     */
    String ROOT_ROLE_CODE = "ROOT";

    /**
     * System configuration key for IP QPS (queries per second) threshold limit.
     */
    String SYSTEM_CONFIG_IP_QPS_LIMIT_KEY = "IP_QPS_THRESHOLD_LIMIT";

}

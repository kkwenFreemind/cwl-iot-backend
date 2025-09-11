package community.waterlevel.iot.system.service;

/**
 * Unified interface for system configuration services.
 * <p>
 * Supports implementations based on both MyBatis and JPA for retrieving and
 * managing system configuration values.
 * </p>
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface SystemConfigService {

    /**
     * Retrieves the configuration value associated with the specified configuration
     * key.
     *
     * @param configKey the configuration key
     * @return the configuration value as a {@link String}, or {@code null} if not
     *         found
     */
    String getConfigValue(String configKey);

    /**
     * Refreshes the configuration cache to ensure the latest values are loaded.
     *
     * @return {@code true} if the cache was refreshed successfully; {@code false}
     *         otherwise
     */
    boolean refreshCache();
}
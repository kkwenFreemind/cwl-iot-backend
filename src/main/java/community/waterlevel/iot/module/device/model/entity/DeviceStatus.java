package community.waterlevel.iot.module.device.model.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enumeration representing the operational status of IoT devices in the system.
 *
 * <p>This enum defines the possible states a device can be in, mapping to database
 * values and providing utility methods for status management and validation.
 *
 * <p>Database mapping:
 * <ul>
 *   <li>{@link #ACTIVE} - Device is operational and actively reporting data</li>
 *   <li>{@link #INACTIVE} - Device is registered but not currently active</li>
 *   <li>{@link #DISABLED} - Device is disabled and not allowed to operate</li>
 * </ul>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/15
 * 
 */
public enum DeviceStatus {

    /**
     * Device is operational and actively reporting data.
     * This is the normal working state for deployed devices.
     */
    ACTIVE("Active"),

    /**
     * Device is registered in the system but not currently active.
     * May indicate temporary disconnection or maintenance mode.
     */
    INACTIVE("Inactive");

    private final String displayName;

    // Cache for case-insensitive lookup
    private static final Map<String, DeviceStatus> NAME_TO_STATUS_MAP =
        Arrays.stream(values())
              .collect(Collectors.toMap(
                  status -> status.name().toLowerCase(),
                  Function.identity()
              ));

    DeviceStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns a human-readable display name for this status.
     *
     * @return the display name for UI presentation
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the lowercase string representation of this status.
     * This matches the database storage format.
     *
     * @return the status name in lowercase
     */
    public String getDatabaseValue() {
        return name().toLowerCase();
    }

    /**
     * Checks if this status represents an operational device.
     *
     * @return true if the device is active, false otherwise
     */
    public boolean isOperational() {
        return this == ACTIVE;
    }


    /**
     * Case-insensitive conversion from string to DeviceStatus enum.
     * Falls back to INACTIVE for unknown values.
     *
     * @param statusString the string representation of the status (case-insensitive)
     * @return the corresponding DeviceStatus, or INACTIVE if not found
     */
    public static DeviceStatus fromString(String statusString) {
        if (statusString == null || statusString.trim().isEmpty()) {
            return INACTIVE;
        }

        return NAME_TO_STATUS_MAP.getOrDefault(
            statusString.trim().toLowerCase(),
            INACTIVE
        );
    }

    /**
     * Validates if the given string represents a valid device status.
     *
     * @param statusString the string to validate
     * @return true if the string matches any enum value (case-insensitive), false otherwise
     */
    public static boolean isValidStatus(String statusString) {
        if (statusString == null || statusString.trim().isEmpty()) {
            return false;
        }
        return NAME_TO_STATUS_MAP.containsKey(statusString.trim().toLowerCase());
    }

    /**
     * Returns all valid status values as a comma-separated string.
     * Useful for validation error messages.
     *
     * @return comma-separated list of valid status values
     */
    public static String getValidValues() {
        return Arrays.stream(values())
                     .map(status -> status.name().toLowerCase())
                     .collect(Collectors.joining(", "));
    }
}

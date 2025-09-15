package community.waterlevel.iot.module.device.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enumeration representing the types of IoT devices in the water level monitoring system.
 *
 * <p>This enum defines the supported device types for the IoT water level monitoring
 * platform, providing type-safe device classification and human-readable descriptions.
 *
 * <p>Supported device types:
 * <ul>
 *   <li>{@link #WATER_LEVEL_SENSOR} - Ultrasonic or pressure-based water level sensors</li>
 *   <li>{@link #OTHER} - Other device types not specifically categorized</li>
 * </ul>
 *
 * <p>Usage examples:
 * <pre>
 * // Get device type from string
 * DeviceType type = DeviceType.fromString("WATER_LEVEL_SENSOR");
 *
 * // Check if device is a water level sensor
 * if (type == DeviceType.WATER_LEVEL_SENSOR) {
 *     // Handle water level sensor specific logic
 * }
 * </pre>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/15
 * @see community.waterlevel.iot.module.device.model.entity.IotDeviceJpa
 */
public enum DeviceModelEnum {

    /**
     * Water level sensor device.
     * <p>Represents ultrasonic, pressure, or other types of water level measurement sensors
     * used for monitoring water levels in rivers, reservoirs, tanks, and other water bodies.
     */
    WATER_LEVEL_SENSOR("Water Level Sensor"),

    /**
     * Other device types.
     * <p>Catch-all category for device types that don't fit into specific categories
     * or are not yet classified in the system.
     */
    OTHER("Other Device");

    private final String displayName;

    // Cache for case-insensitive lookup
    private static final Map<String, DeviceModelEnum> NAME_TO_TYPE_MAP =
        Arrays.stream(values())
              .collect(Collectors.toMap(
                  type -> type.name().toLowerCase(),
                  Function.identity()
              ));

    /**
     * Constructs a new device type with the specified display name.
     *
     * @param displayName the human-readable name for this device type
     */
    DeviceModelEnum(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this device type.
     *
     * @return the display name suitable for UI presentation
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the lowercase string representation of this device type.
     * This is useful for database storage and API communication.
     *
     * @return the device type name in lowercase format
     */
    public String getDatabaseValue() {
        return name().toLowerCase();
    }

    /**
     * Checks if this device type represents a water level sensor.
     *
     * @return {@code true} if this is a water level sensor, {@code false} otherwise
     */
    public boolean isWaterLevelSensor() {
        return this == WATER_LEVEL_SENSOR;
    }

    /**
     * Checks if this device type is categorized (not "OTHER").
     *
     * @return {@code true} if this is a specific device type, {@code false} if it's "OTHER"
     */
    public boolean isCategorized() {
        return this != OTHER;
    }

    /**
     * Case-insensitive conversion from string to DeviceType enum.
     * <p>This method provides flexible string-to-enum conversion with fallback
     * to {@link #OTHER} for unknown or invalid values.
     *
     * <p>Supports backward compatibility with legacy data formats:
     * <ul>
     *   <li>"water" → WATER_LEVEL_SENSOR (legacy format)</li>
     *   <li>"water_level_sensor" → WATER_LEVEL_SENSOR</li>
     *   <li>"other" → OTHER</li>
     * </ul>
     *
     * @param typeString the string representation of the device type (case-insensitive)
     * @return the corresponding DeviceType, or {@link #OTHER} if not found or null
     * @throws IllegalArgumentException if typeString is null (handled gracefully)
     */
    public static DeviceModelEnum fromString(String typeString) {
        if (typeString == null || typeString.trim().isEmpty()) {
            return OTHER;
        }

        String normalized = typeString.trim().toLowerCase();

        // Handle legacy data format: "water" should map to WATER_LEVEL_SENSOR
        if ("water".equals(normalized)) {
            return WATER_LEVEL_SENSOR;
        }

        return NAME_TO_TYPE_MAP.getOrDefault(normalized, OTHER);
    }

    /**
     * Validates if the given string represents a valid device type.
     *
     * @param typeString the string to validate
     * @return {@code true} if the string matches any enum value (case-insensitive),
     *         {@code false} otherwise
     */
    public static boolean isValidType(String typeString) {
        if (typeString == null || typeString.trim().isEmpty()) {
            return false;
        }
        return NAME_TO_TYPE_MAP.containsKey(typeString.trim().toLowerCase());
    }

    /**
     * Returns all valid device type values as a comma-separated string.
     * Useful for validation error messages and API documentation.
     *
     * @return comma-separated list of valid device type values
     */
    public static String getValidValues() {
        return Arrays.stream(values())
                     .map(type -> type.name().toLowerCase())
                     .collect(Collectors.joining(", "));
    }

    /**
     * Returns all device types as a formatted string with their display names.
     * Useful for generating dropdown options or selection lists.
     *
     * @return formatted string with all device types and their descriptions
     */
    public static String getFormattedOptions() {
        return Arrays.stream(values())
                     .map(type -> type.name().toLowerCase() + " - " + type.getDisplayName())
                     .collect(Collectors.joining("\n"));
    }
}

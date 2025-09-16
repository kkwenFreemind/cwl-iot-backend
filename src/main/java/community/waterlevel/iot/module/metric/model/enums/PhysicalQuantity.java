package community.waterlevel.iot.module.metric.model.enums;

/**
 * Enumeration representing physical quantities measured by IoT sensors.
 *
 * <p>This enum defines standardized physical quantities commonly measured in
 * water level monitoring systems. Each quantity includes a display name and category.
 *
 * <p>Supported categories:
 * <ul>
 *   <li>Length: Water Level measurements</li>
 *   <li>Temperature: Environmental and water temperature</li>
 *   <li>Pressure: Atmospheric and water pressure</li>
 *   <li>Electrical: Voltage, Current for sensor power</li>
 *   <li>Other: Battery level, Signal strength</li>
 * </ul>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/16
 * @see IotMetricDefinition
 * @see PhysicalQuantityCategory
 */
public enum PhysicalQuantity {

    // Primary Measurements for Water Level IoT
    /**
     * Water level measurement - the primary measurement for water level monitoring.
     */
    WATER_LEVEL("Water Level", PhysicalQuantityCategory.LENGTH),

    /**
     * Temperature measurement - ambient or water temperature.
     */
    TEMPERATURE("Temperature", PhysicalQuantityCategory.TEMPERATURE),

    /**
     * Humidity measurement - environmental humidity.
     */
    HUMIDITY("Humidity", PhysicalQuantityCategory.OTHER),

    /**
     * Pressure measurement - atmospheric or water pressure.
     */
    PRESSURE("Pressure", PhysicalQuantityCategory.PRESSURE),

    /**
     * Voltage measurement - battery or sensor voltage.
     */
    VOLTAGE("Voltage", PhysicalQuantityCategory.ELECTRICAL),

    /**
     * Current measurement - power consumption.
     */
    CURRENT("Current", PhysicalQuantityCategory.ELECTRICAL),

    /**
     * Battery level - remaining battery capacity percentage.
     */
    BATTERY_LEVEL("Battery Level", PhysicalQuantityCategory.OTHER),

    /**
     * Signal strength - wireless signal strength.
     */
    SIGNAL_STRENGTH("Signal Strength", PhysicalQuantityCategory.OTHER);

    private final String displayName;
    private final PhysicalQuantityCategory category;

    /**
     * Constructs a new physical quantity with the specified properties.
     *
     * @param displayName the human-readable name for this quantity
     * @param category    the category this quantity belongs to
     */
    PhysicalQuantity(String displayName, PhysicalQuantityCategory category) {
        this.displayName = displayName;
        this.category = category;
    }

    /**
     * Returns the human-readable display name for this physical quantity.
     *
     * @return the display name suitable for UI presentation
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the category this physical quantity belongs to.
     *
     * @return the category enum value
     */
    public PhysicalQuantityCategory getCategory() {
        return category;
    }

    /**
     * Case-insensitive conversion from string to PhysicalQuantity enum.
     *
     * @param quantityString the string representation of the physical quantity (case-insensitive)
     * @return the corresponding PhysicalQuantity, or null if not found
     */
    public static PhysicalQuantity fromString(String quantityString) {
        if (quantityString == null || quantityString.trim().isEmpty()) {
            return null;
        }

        for (PhysicalQuantity quantity : values()) {
            if (quantity.displayName.equalsIgnoreCase(quantityString.trim())) {
                return quantity;
            }
        }
        return null;
    }

    /**
     * Validates if the provided string represents a valid physical quantity.
     *
     * @param quantityString the string to validate
     * @return {@code true} if the string matches any enum value (case-insensitive), {@code false} otherwise
     */
    public static boolean isValidQuantity(String quantityString) {
        return fromString(quantityString) != null;
    }
}
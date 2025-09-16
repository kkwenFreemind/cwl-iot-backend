package community.waterlevel.iot.module.metric.model.enums;

/**
 * Enumeration representing categories of physical quantities.
 *
 * <p>This enum provides high-level categorization for physical quantities
 * measured by water level IoT sensors, enabling better organization and filtering
 * of metric definitions by measurement type.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/16
 * @see PhysicalQuantity
 */
public enum PhysicalQuantityCategory {

    /**
     * Length measurements.
     * <p>Includes water level measurements.
     */
    LENGTH("Length"),

    /**
     * Temperature measurements.
     * <p>Includes ambient and water temperature measurements.
     */
    TEMPERATURE("Temperature"),

    /**
     * Pressure measurements.
     * <p>Includes atmospheric and water pressure measurements.
     */
    PRESSURE("Pressure"),

    /**
     * Electrical measurements.
     * <p>Includes voltage and current measurements for sensor power.
     */
    ELECTRICAL("Electrical"),

    /**
     * Flow rate measurements.
     * <p>Includes water flow rate measurements.
     */
    FLOW_RATE("Flow Rate"),

    /**
     * Other measurements.
     * <p>Includes humidity, battery level, and signal strength measurements.
     */
    OTHER("Other");

    private final String displayName;

    /**
     * Constructs a new category with the specified display name.
     *
     * @param displayName the human-readable name for this category
     */
    PhysicalQuantityCategory(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this category.
     *
     * @return the display name suitable for UI presentation
     */
    public String getDisplayName() {
        return displayName;
    }
}
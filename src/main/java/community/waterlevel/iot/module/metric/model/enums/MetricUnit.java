package community.waterlevel.iot.module.metric.model.enums;

/**
 * Enumeration representing standard units of measurement for IoT metrics.
 *
 * <p>This enum defines commonly used units in water level monitoring systems.
 * Each unit includes its symbol, full name, and the physical quantity category it applies to.
 *
 * <p>Supported unit categories:
 * <ul>
 *   <li>Length: mm, cm, m (for water level measurements)</li>
 *   <li>Temperature: °C (Celsius)</li>
 *   <li>Pressure: Pa, kPa, bar</li>
 *   <li>Electrical: V, mV, A, mA</li>
 *   <li>Other: %, dBm (for battery, signal, humidity)</li>
 * </ul>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/16
 * @see IotMetricDefinition
 * @see PhysicalQuantity
 */
public enum MetricUnit {

    // Length Units (for water level)
    /**
     * Millimeter - most common for water level measurements.
     */
    MILLIMETER("mm", "Millimeter", PhysicalQuantityCategory.LENGTH),

    /**
     * Centimeter - common for water level measurements.
     */
    CENTIMETER("cm", "Centimeter", PhysicalQuantityCategory.LENGTH),

    /**
     * Meter - for larger water level measurements.
     */
    METER("m", "Meter", PhysicalQuantityCategory.LENGTH),

    // Temperature Units
    /**
     * Degree Celsius - standard temperature unit.
     */
    CELSIUS("°C", "Degree Celsius", PhysicalQuantityCategory.TEMPERATURE),

    // Pressure Units
    /**
     * Pascal - SI unit for pressure.
     */
    PASCAL("Pa", "Pascal", PhysicalQuantityCategory.PRESSURE),

    /**
     * Kilopascal - common pressure unit.
     */
    KILOPASCAL("kPa", "Kilopascal", PhysicalQuantityCategory.PRESSURE),

    /**
     * Bar - practical pressure unit.
     */
    BAR("bar", "Bar", PhysicalQuantityCategory.PRESSURE),

    // Electrical Units
    /**
     * Volt - for voltage measurements.
     */
    VOLT("V", "Volt", PhysicalQuantityCategory.ELECTRICAL),

    /**
     * Millivolt - for low voltage measurements.
     */
    MILLIVOLT("mV", "Millivolt", PhysicalQuantityCategory.ELECTRICAL),

    /**
     * Ampere - for current measurements.
     */
    AMPERE("A", "Ampere", PhysicalQuantityCategory.ELECTRICAL),

    /**
     * Milliampere - for low current measurements.
     */
    MILLIAMPERE("mA", "Milliampere", PhysicalQuantityCategory.ELECTRICAL),

    // Other Units
    /**
     * Percentage - for humidity, battery level, signal strength.
     */
    PERCENT("%", "Percent", PhysicalQuantityCategory.OTHER),

    /**
     * Decibel-milliwatt - for signal strength.
     */
    DBM("dBm", "Decibel-milliwatt", PhysicalQuantityCategory.OTHER);

    private final String symbol;
    private final String fullName;
    private final PhysicalQuantityCategory category;

    /**
     * Constructs a new unit with the specified properties.
     *
     * @param symbol   the unit symbol (e.g., "cm", "°C", "V")
     * @param fullName the full name of the unit
     * @param category the category this unit belongs to
     */
    MetricUnit(String symbol, String fullName, PhysicalQuantityCategory category) {
        this.symbol = symbol;
        this.fullName = fullName;
        this.category = category;
    }

    /**
     * Returns the symbol representation of this unit.
     *
     * @return the unit symbol (e.g., "cm", "°C")
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the full name of this unit.
     *
     * @return the complete unit name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Returns the category this unit belongs to.
     *
     * @return the category enum value
     */
    public PhysicalQuantityCategory getCategory() {
        return category;
    }

    /**
     * Returns the display representation, preferring symbol if available.
     *
     * @return the symbol if present, otherwise the full name
     */
    public String getDisplayName() {
        return (symbol != null && !symbol.trim().isEmpty()) ? symbol : fullName;
    }

    /**
     * Checks if this unit is compatible with the specified physical quantity.
     *
     * @param quantity the physical quantity to check compatibility with
     * @return {@code true} if compatible, {@code false} otherwise
     */
    public boolean isCompatibleWith(PhysicalQuantity quantity) {
        if (quantity == null) {
            return false;
        }
        return this.category == quantity.getCategory();
    }

    /**
     * Finds a unit by its symbol.
     *
     * @param symbol the unit symbol to search for
     * @return the corresponding MetricUnit, or null if not found
     */
    public static MetricUnit fromSymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return null;
        }

        for (MetricUnit unit : values()) {
            if (unit.symbol.equals(symbol.trim())) {
                return unit;
            }
        }
        return null;
    }

    /**
     * Finds a unit by its full name (case-insensitive).
     *
     * @param fullName the full name to search for
     * @return the corresponding MetricUnit, or null if not found
     */
    public static MetricUnit fromFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return null;
        }

        for (MetricUnit unit : values()) {
            if (unit.fullName.equalsIgnoreCase(fullName.trim())) {
                return unit;
            }
        }
        return null;
    }

    /**
     * Validates if the provided symbol represents a valid unit.
     *
     * @param symbol the symbol to validate
     * @return {@code true} if the symbol matches any unit, {@code false} otherwise
     */
    public static boolean isValidSymbol(String symbol) {
        return fromSymbol(symbol) != null;
    }
}
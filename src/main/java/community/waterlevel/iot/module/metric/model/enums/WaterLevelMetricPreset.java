package community.waterlevel.iot.module.metric.model.enums;

import community.waterlevel.iot.module.metric.model.entity.IotMetricDefinition;
import lombok.Getter;

/**
 * Predefined metric combinations for water level IoT systems.
 *
 * <p>This enum provides commonly used metric configurations to simplify
 * the setup process and ensure consistency across water level monitoring deployments.
 *
 * <p>Each preset includes the physical quantity, unit, and data type that work
 * well together for typical water level monitoring scenarios.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/16
 */
@Getter
public enum WaterLevelMetricPreset {

    /**
     * Water level measurement using centimeters (most common for water level sensors).
     */
    WATER_LEVEL_CM("Water Level (cm)", PhysicalQuantity.WATER_LEVEL, MetricUnit.CENTIMETER, MetricDataType.Int32),

    /**
     * Water level measurement using millimeters (for high precision sensors).
     */
    WATER_LEVEL_MM("Water Level (mm)", PhysicalQuantity.WATER_LEVEL, MetricUnit.MILLIMETER, MetricDataType.Int32),

    /**
     * Water level measurement using meters (for large reservoirs).
     */
    WATER_LEVEL_M("Water Level (m)", PhysicalQuantity.WATER_LEVEL, MetricUnit.METER, MetricDataType.Float),

    /**
     * Ambient temperature measurement.
     */
    TEMPERATURE_C("Temperature (°C)", PhysicalQuantity.TEMPERATURE, MetricUnit.CELSIUS, MetricDataType.Float),

    /**
     * Atmospheric pressure measurement.
     */
    PRESSURE_KPA("Pressure (kPa)", PhysicalQuantity.PRESSURE, MetricUnit.KILOPASCAL, MetricDataType.Float),

    /**
     * Battery voltage measurement.
     */
    BATTERY_VOLTAGE("Battery Voltage (V)", PhysicalQuantity.VOLTAGE, MetricUnit.VOLT, MetricDataType.Float),

    /**
     * Battery level percentage.
     */
    BATTERY_LEVEL("Battery Level (%)", PhysicalQuantity.BATTERY_LEVEL, MetricUnit.PERCENT, MetricDataType.UInt8),

    /**
     * Signal strength in dBm.
     */
    SIGNAL_STRENGTH("Signal Strength (dBm)", PhysicalQuantity.SIGNAL_STRENGTH, MetricUnit.DBM, MetricDataType.Int16),

    /**
     * Current consumption in milliamperes.
     */
    CURRENT_MA("Current (mA)", PhysicalQuantity.CURRENT, MetricUnit.MILLIAMPERE, MetricDataType.Float);

    private final String displayName;
    private final PhysicalQuantity physicalQuantity;
    private final MetricUnit unit;
    private final MetricDataType dataType;

    /**
     * Constructs a new metric preset with the specified properties.
     *
     * @param displayName      the human-readable name for this preset
     * @param physicalQuantity the physical quantity
     * @param unit            the unit of measurement
     * @param dataType        the data type
     */
    WaterLevelMetricPreset(String displayName, PhysicalQuantity physicalQuantity,
                          MetricUnit unit, MetricDataType dataType) {
        this.displayName = displayName;
        this.physicalQuantity = physicalQuantity;
        this.unit = unit;
        this.dataType = dataType;
    }

    /**
     * Creates an IotMetricDefinition instance from this preset.
     *
     * @param metricName the name for the metric definition
     * @param deptId     the department ID
     * @return a new IotMetricDefinition configured with this preset
     */
    public IotMetricDefinition createMetricDefinition(String metricName, Long deptId) {
        IotMetricDefinition definition = new IotMetricDefinition();
        definition.setMetricName(metricName);
        definition.setDeptId(deptId);
        definition.setPhysicalQuantity(this.physicalQuantity);
        definition.setUnit(this.unit);
        definition.setDataType(this.dataType);
        definition.setAlias(this.displayName);
        return definition;
    }

    /**
     * Finds a preset by its display name.
     *
     * @param displayName the display name to search for
     * @return the corresponding preset, or null if not found
     */
    public static WaterLevelMetricPreset fromDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return null;
        }

        for (WaterLevelMetricPreset preset : values()) {
            if (preset.displayName.equalsIgnoreCase(displayName.trim())) {
                return preset;
            }
        }
        return null;
    }

    /**
     * Returns all presets for a specific physical quantity.
     *
     * @param quantity the physical quantity to filter by
     * @return array of presets for the specified quantity
     */
    public static WaterLevelMetricPreset[] getPresetsByQuantity(PhysicalQuantity quantity) {
        return java.util.Arrays.stream(values())
                .filter(preset -> preset.physicalQuantity == quantity)
                .toArray(WaterLevelMetricPreset[]::new);
    }
}
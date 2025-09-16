package community.waterlevel.iot.module.metric.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enumeration representing supported data types for IoT metric values.
 *
 * <p>This enum defines the data types that can be used for storing metric values
 * in the IoT metric definitions system. The supported types are based on the
 * Sparkplug B specification and include various integer, floating-point, and
 * other data types commonly used in industrial IoT applications.
 *
 * <p>Supported data types:
 * <ul>
 *   <li>Signed integers: {@link #Int8}, {@link #Int16}, {@link #Int32}, {@link #Int64}</li>
 *   <li>Unsigned integers: {@link #UInt8}, {@link #UInt16}, {@link #UInt32}, {@link #UInt64}</li>
 *   <li>Floating-point: {@link #Float}, {@link #Double}</li>
 *   <li>Other: {@link #Boolean}, {@link #String}</li>
 * </ul>
 *
 * <p>Usage examples:
 * <pre>
 * // Get data type from string
 * MetricDataType type = MetricDataType.fromString("Int32");
 *
 * // Check if data type is numeric
 * if (type.isNumeric()) {
 *     // Handle numeric data processing
 * }
 * </pre>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/16
 * @see IotMetricDefinition
 */
public enum MetricDataType {

    /**
     * 8-bit signed integer (-128 to 127).
     * <p>Compact storage for small integer values.
     */
    Int8("Int8", true, 1),

    /**
     * 16-bit signed integer (-32,768 to 32,767).
     * <p>Suitable for sensor readings with moderate range.
     */
    Int16("Int16", true, 2),

    /**
     * 32-bit signed integer (-2,147,483,648 to 2,147,483,647).
     * <p>Most common integer type for general-purpose use.
     */
    Int32("Int32", true, 4),

    /**
     * 64-bit signed integer (-9,223,372,036,854,775,808 to 9,223,372,036,854,775,807).
     * <p>For large integer values requiring extended range.
     */
    Int64("Int64", true, 8),

    /**
     * 8-bit unsigned integer (0 to 255).
     * <p>Compact storage for non-negative small values.
     */
    UInt8("UInt8", true, 1),

    /**
     * 16-bit unsigned integer (0 to 65,535).
     * <p>Suitable for non-negative sensor readings.
     */
    UInt16("UInt16", true, 2),

    /**
     * 32-bit unsigned integer (0 to 4,294,967,295).
     * <p>Common for counters and non-negative large values.
     */
    UInt32("UInt32", true, 4),

    /**
     * 64-bit unsigned integer (0 to 18,446,744,073,709,551,615).
     * <p>For very large non-negative values.
     */
    UInt64("UInt64", true, 8),

    /**
     * 32-bit floating-point number (IEEE 754 single precision).
     * <p>For decimal values with moderate precision requirements.
     */
    Float("Float", true, 4),

    /**
     * 64-bit floating-point number (IEEE 754 double precision).
     * <p>For decimal values requiring high precision.
     */
    Double("Double", true, 8),

    /**
     * Boolean value (true/false).
     * <p>For binary states and flags.
     */
    Boolean("Boolean", false, 1),

    /**
     * Text string.
     * <p>For textual data and identifiers.
     */
    String("String", false, -1); // Variable length

    private final String typeName;
    private final boolean numeric;
    private final int byteSize; // -1 for variable length

    // Cache for case-insensitive lookup
    private static final Map<String, MetricDataType> NAME_TO_TYPE_MAP =
        Arrays.stream(values())
              .collect(Collectors.toMap(
                  type -> type.typeName.toLowerCase(),
                  Function.identity()
              ));

    /**
     * Constructs a new data type with the specified properties.
     *
     * @param typeName the string representation of the data type
     * @param numeric  whether this data type represents numeric values
     * @param byteSize the size in bytes, or -1 for variable length
     */
    MetricDataType(String typeName, boolean numeric, int byteSize) {
        this.typeName = typeName;
        this.numeric = numeric;
        this.byteSize = byteSize;
    }

    /**
     * Returns the string representation of this data type.
     *
     * @return the type name used in database and API
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Checks if this data type represents numeric values.
     *
     * @return {@code true} if numeric, {@code false} otherwise
     */
    public boolean isNumeric() {
        return numeric;
    }

    /**
     * Returns the size of this data type in bytes.
     *
     * @return the byte size, or -1 for variable length types
     */
    public int getByteSize() {
        return byteSize;
    }

    /**
     * Checks if this data type has a fixed byte size.
     *
     * @return {@code true} if fixed size, {@code false} for variable length
     */
    public boolean isFixedSize() {
        return byteSize > 0;
    }

    /**
     * Checks if this data type is an integer type (signed or unsigned).
     *
     * @return {@code true} if integer type, {@code false} otherwise
     */
    public boolean isInteger() {
        return this != Float && this != Double && this != Boolean && this != String;
    }

    /**
     * Checks if this data type is a floating-point type.
     *
     * @return {@code true} if floating-point, {@code false} otherwise
     */
    public boolean isFloatingPoint() {
        return this == Float || this == Double;
    }

    /**
     * Case-insensitive conversion from string to MetricDataType enum.
     * <p>This method provides flexible string-to-enum conversion with fallback
     * to null for unknown values.
     *
     * @param typeString the string representation of the data type (case-insensitive)
     * @return the corresponding MetricDataType, or null if not found
     */
    public static MetricDataType fromString(String typeString) {
        if (typeString == null || typeString.trim().isEmpty()) {
            return null;
        }

        return NAME_TO_TYPE_MAP.get(typeString.trim().toLowerCase());
    }

    /**
     * Validates if the provided string represents a valid data type.
     *
     * @param typeString the string to validate
     * @return {@code true} if the string matches any enum value (case-insensitive),
     *         {@code false} otherwise
     */
    public static boolean isValidType(String typeString) {
        return fromString(typeString) != null;
    }

    /**
     * Returns all valid data type names as a comma-separated string.
     * Useful for validation error messages and API documentation.
     *
     * @return comma-separated list of valid data type names
     */
    public static String getValidTypeNames() {
        return Arrays.stream(values())
                     .map(MetricDataType::getTypeName)
                     .collect(Collectors.joining(", "));
    }

    /**
     * Returns all numeric data types.
     *
     * @return array of numeric MetricDataType values
     */
    public static MetricDataType[] getNumericTypes() {
        return Arrays.stream(values())
                     .filter(MetricDataType::isNumeric)
                     .toArray(MetricDataType[]::new);
    }

    /**
     * Returns all integer data types.
     *
     * @return array of integer MetricDataType values
     */
    public static MetricDataType[] getIntegerTypes() {
        return Arrays.stream(values())
                     .filter(MetricDataType::isInteger)
                     .toArray(MetricDataType[]::new);
    }
}
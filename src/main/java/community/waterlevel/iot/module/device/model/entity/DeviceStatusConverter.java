package community.waterlevel.iot.module.device.model.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA AttributeConverter for seamless conversion between DeviceStatus enum and database string values.
 *
 * <p>This converter enables JPA to automatically handle the transformation between the strongly-typed
 * {@link DeviceStatus} enum used in the application domain model and the string values stored in the
 * database. It ensures data consistency and type safety while maintaining compatibility with the
 * database schema.
 *
 * <p>Key features:
 * <ul>
 *   <li>Case-insensitive conversion from database strings to enum values</li>
 *   <li>Graceful handling of null values and unknown database entries</li>
 *   <li>Fallback to {@link DeviceStatus#INACTIVE} for invalid database values</li>
 *   <li>Lowercase string storage to match database conventions</li>
 * </ul>
 *
 * <p>Database mapping:
 * <ul>
 *   <li>{@link DeviceStatus#ACTIVE} ↔ "active"</li>
 *   <li>{@link DeviceStatus#INACTIVE} ↔ "inactive"</li>
 *   <li>{@link DeviceStatus#DISABLED} ↔ "disabled"</li>
 * </ul>
 *
 * <p>This converter is explicitly configured with {@code autoApply = false} to ensure
 * it's only applied where explicitly annotated, preventing unintended conversions
 * on other string fields.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/15
 * @see DeviceStatus
 * @see AttributeConverter
 */
@Converter(autoApply = false)
public class DeviceStatusConverter implements AttributeConverter<DeviceStatus, String> {

    /**
     * Converts a DeviceStatus enum value to its database string representation.
     *
     * <p>This method transforms the enum value to a lowercase string that matches
     * the database storage format. The conversion ensures consistency between the
     * application's enum values and the database schema expectations.
     *
     * @param attribute the DeviceStatus enum value to convert; may be null
     * @return the lowercase string representation for database storage,
     *         or null if the input attribute is null
     */
    @Override
    public String convertToDatabaseColumn(DeviceStatus attribute) {
        return attribute == null ? null : attribute.name().toLowerCase();
    }

    /**
     * Converts a database string value to its corresponding DeviceStatus enum value.
     *
     * <p>This method performs a case-insensitive conversion from the database string
     * to the appropriate enum value. If the database contains an unknown or invalid
     * status value, it gracefully falls back to {@link DeviceStatus#INACTIVE} to
     * ensure application stability.
     *
     * <p>The conversion process:
     * <ol>
     *   <li>Handles null database values by returning null</li>
     *   <li>Attempts case-insensitive enum value lookup</li>
     *   <li>Falls back to INACTIVE for any conversion failures</li>
     * </ol>
     *
     * @param dbData the string value from the database; may be null
     * @return the corresponding DeviceStatus enum value, or null if dbData is null,
     *         or {@link DeviceStatus#INACTIVE} if the conversion fails
     */
    @Override
    public DeviceStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return DeviceStatus.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException ex) {
            // Unknown value in DB - default to INACTIVE to be safe
            return DeviceStatus.INACTIVE;
        }
    }
}

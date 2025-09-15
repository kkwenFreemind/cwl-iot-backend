package community.waterlevel.iot.module.device.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Custom JPA converter for DeviceModelEnum to handle backward compatibility
 * with legacy database values.
 *
 * <p>This converter ensures that:
 * <ul>
 *   <li>Legacy "water" values are converted to WATER_LEVEL_SENSOR</li>
 *   <li>Standard enum values are preserved</li>
 *   <li>Database storage uses lowercase format for consistency</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 * {@code
 * @Entity
 * public class IotDeviceJpa {
 *     @Convert(converter = DeviceModelEnumConverter.class)
 *     private DeviceModelEnum deviceModel;
 * }
 * }
 * </pre>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/15
 * @see DeviceModelEnum
 */
@Converter
public class DeviceModelEnumConverter implements AttributeConverter<DeviceModelEnum, String> {

    /**
     * Converts the DeviceModelEnum to its database representation.
     *
     * @param attribute the enum value to convert
     * @return the database string representation, or null if attribute is null
     */
    @Override
    public String convertToDatabaseColumn(DeviceModelEnum attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name().toLowerCase();
    }

    /**
     * Converts the database string to DeviceModelEnum.
     * <p>Handles backward compatibility by mapping legacy "water" values
     * to WATER_LEVEL_SENSOR enum constant.
     *
     * @param dbData the database string value
     * @return the corresponding DeviceModelEnum, or null if dbData is null
     */
    @Override
    public DeviceModelEnum convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }

        String normalized = dbData.trim().toLowerCase();

        // Handle legacy data format: "water" should map to WATER_LEVEL_SENSOR
        if ("water".equals(normalized)) {
            return DeviceModelEnum.WATER_LEVEL_SENSOR;
        }

        // Try to match standard enum values
        try {
            return DeviceModelEnum.valueOf(normalized.toUpperCase());
        } catch (IllegalArgumentException e) {
            // If no exact match, fall back to fromString method
            return DeviceModelEnum.fromString(normalized);
        }
    }
}
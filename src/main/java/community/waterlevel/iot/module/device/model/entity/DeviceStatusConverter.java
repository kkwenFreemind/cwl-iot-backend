package community.waterlevel.iot.module.device.model.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA AttributeConverter to map database string values (lowercase) to the
 * DeviceStatus enum and back. This handles cases where the DB stores values
 * like "active" and the enum constants are uppercase.
 */
@Converter(autoApply = false)
public class DeviceStatusConverter implements AttributeConverter<DeviceStatus, String> {

    @Override
    public String convertToDatabaseColumn(DeviceStatus attribute) {
        return attribute == null ? null : attribute.name().toLowerCase();
    }

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

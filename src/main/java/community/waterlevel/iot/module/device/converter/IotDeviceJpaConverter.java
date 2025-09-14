package community.waterlevel.iot.module.device.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import community.waterlevel.iot.module.device.model.entity.IotDeviceJpa;
import community.waterlevel.iot.module.device.model.form.IotDeviceForm;
import community.waterlevel.iot.module.device.model.vo.IotDeviceVO;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * IotDeviceJpaConverter is a MapStruct converter interface for transforming
 * IoT device-related objects between entity, form, and view representations.
 * Supports spatial coordinate mapping and business logic calculations.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/14
 */
@Mapper(componentModel = "spring")
public interface IotDeviceJpaConverter {

    /**
     * Converts an IotDeviceJpa entity to an IotDeviceVO (View Object).
     * Ignores deptName, createdByUsername, isOnline, and minutesSinceLastSeen 
     * as they require additional business logic.
     *
     * @param entity the device entity
     * @return the device view object
     */
    @Mapping(target = "deptName", ignore = true)
    @Mapping(target = "createdByUsername", ignore = true)
    @Mapping(target = "isOnline", ignore = true)
    @Mapping(target = "minutesSinceLastSeen", ignore = true)
    IotDeviceVO toVo(IotDeviceJpa entity);

    /**
     * Converts an IotDeviceForm to an IotDeviceJpa entity.
     * Ignores auto-managed fields: createdBy, createdAt, lastSeen, isDeleted, 
     * updatedAt, and the PostGIS geom field.
     *
     * @param form the device form data
     * @return the device entity
     */
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastSeen", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "geom", ignore = true)
    IotDeviceJpa toEntity(IotDeviceForm form);

    /**
     * Converts an IotDeviceJpa entity to an IotDeviceForm.
     *
     * @param entity the device entity
     * @return the device form data
     */
    IotDeviceForm toForm(IotDeviceJpa entity);

    /**
     * Enhanced conversion with business logic calculations.
     * Calculates online status and minutes since last seen.
     *
     * @param entity           the device entity
     * @param deptName         the department name (from join)
     * @param createdByUsername the creator username (from join)
     * @param onlineThresholdMinutes threshold for considering device online
     * @return the enhanced device view object
     */
    default IotDeviceVO toEnhancedVo(IotDeviceJpa entity, String deptName, 
                                   String createdByUsername, int onlineThresholdMinutes) {
        if (entity == null) {
            return null;
        }

        IotDeviceVO vo = toVo(entity);
        vo.setDeptName(deptName);
        vo.setCreatedByUsername(createdByUsername);

        // Calculate online status and minutes since last seen
        if (entity.getLastSeen() != null) {
            LocalDateTime now = LocalDateTime.now();
            long minutesSinceLastSeen = ChronoUnit.MINUTES.between(entity.getLastSeen(), now);
            vo.setMinutesSinceLastSeen(minutesSinceLastSeen);
            vo.setIsOnline(minutesSinceLastSeen <= onlineThresholdMinutes && "active".equals(entity.getStatus()));
        } else {
            vo.setMinutesSinceLastSeen(null);
            vo.setIsOnline(false);
        }

        return vo;
    }

}
package community.waterlevel.iot.module.device.converter;

import community.waterlevel.iot.module.device.model.entity.DeviceStatus;
import community.waterlevel.iot.module.device.model.entity.IotDeviceJpa;
import community.waterlevel.iot.module.device.model.form.IotDeviceForm;
import community.waterlevel.iot.module.device.model.vo.IotDeviceVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IotDeviceJpaConverter {

    @Mapping(target = "status", source = "status")
    @Mapping(target = "deptName", ignore = true) // Handled in service layer
    IotDeviceVO toVo(IotDeviceJpa entity);

    @Mapping(target = "deviceId", ignore = true)
    @Mapping(target = "createdBy", ignore = true) // Set in service layer
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastSeen", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "geom", ignore = true) // Handled by database trigger
    IotDeviceJpa toEntity(IotDeviceForm form);

    IotDeviceForm toForm(IotDeviceJpa entity);

    default DeviceStatus stringToDeviceStatus(String status) {
        if (status == null) return DeviceStatus.INACTIVE;
        try {
            return DeviceStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return DeviceStatus.INACTIVE;
        }
    }

    default String deviceStatusToString(DeviceStatus status) {
        return status == null ? null : status.name();
    }
}

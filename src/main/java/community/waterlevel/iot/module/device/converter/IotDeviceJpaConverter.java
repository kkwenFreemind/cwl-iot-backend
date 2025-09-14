package community.waterlevel.iot.module.device.converter;

import community.waterlevel.iot.module.device.model.entity.IotDeviceJpa;
import community.waterlevel.iot.module.device.model.form.IotDeviceForm;
import community.waterlevel.iot.module.device.model.vo.IotDeviceVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IotDeviceJpaConverter {

    @Mapping(target = "status", source = "status")
    IotDeviceVO toVo(IotDeviceJpa entity);

    @Mapping(target = "deviceId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastSeen", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    IotDeviceJpa toEntity(IotDeviceForm form);

    IotDeviceForm toForm(IotDeviceJpa entity);
}

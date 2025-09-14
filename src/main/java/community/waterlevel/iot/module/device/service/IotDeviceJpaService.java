package community.waterlevel.iot.module.device.service;

import community.waterlevel.iot.module.device.model.form.IotDeviceForm;
import community.waterlevel.iot.module.device.model.vo.IotDeviceVO;

import java.util.List;
import java.util.UUID;

public interface IotDeviceJpaService {

    List<IotDeviceVO> getDevicesByDept(Long deptId);

    List<IotDeviceVO> getDevicesByStatus(String status);

    boolean saveDevice(IotDeviceForm deviceForm);

    boolean updateDevice(UUID deviceId, IotDeviceForm deviceForm);

    boolean deleteDevices(String ids);

    boolean updateDeviceStatus(UUID deviceId, String status);

    boolean updateDeviceLastSeen(UUID deviceId);

    List<IotDeviceVO> getDevicesWithinRadius(Double centerLat, Double centerLng, Double radiusKm);

    List<IotDeviceVO> getNearestDevices(Double centerLat, Double centerLng, Integer limit);

    List<IotDeviceVO> listDevices(community.waterlevel.iot.module.device.model.query.IotDeviceQuery queryParams);

    java.util.List<IotDeviceVO> listDevices(org.springframework.data.jpa.domain.Specification<community.waterlevel.iot.module.device.model.entity.IotDeviceJpa> specification);
}

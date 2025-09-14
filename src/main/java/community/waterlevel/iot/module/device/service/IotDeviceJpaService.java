package community.waterlevel.iot.module.device.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import community.waterlevel.iot.module.device.model.entity.IotDeviceJpa;
import community.waterlevel.iot.module.device.model.form.IotDeviceForm;
import community.waterlevel.iot.module.device.model.query.IotDevicePageQuery;
import community.waterlevel.iot.module.device.model.vo.IotDeviceVO;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for IoT device-related business logic and operations.
 * Provides methods for device management, spatial queries, status tracking,
 * and department-based access control with comprehensive CRUD operations.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/14
 */
public interface IotDeviceJpaService {

    /**
     * Retrieves a paginated list of devices based on query parameters.
     * Supports department-based filtering through @DataPermission.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return a MyBatis-Plus IPage containing device view objects
     */
    IPage<IotDeviceVO> getDevicePage(IotDevicePageQuery queryParams);

    /**
     * Retrieves the form data for a specific device by its ID.
     *
     * @param deviceId the device ID (UUID)
     * @return the IotDeviceForm object
     */
    IotDeviceForm getDeviceFormData(UUID deviceId);

    /**
     * Saves a new device based on the provided form data.
     *
     * @param deviceForm the device form data
     * @return true if the device was saved successfully
     */
    boolean saveDevice(IotDeviceForm deviceForm);

    /**
     * Updates an existing device by its ID with the provided form data.
     *
     * @param deviceId   the device ID (UUID)
     * @param deviceForm the updated device form data
     * @return true if the update was successful
     */
    boolean updateDevice(UUID deviceId, IotDeviceForm deviceForm);

    /**
     * Deletes one or more devices by their IDs.
     *
     * @param idsStr a comma-separated string of device IDs (UUIDs) to delete
     * @return true if all devices were deleted successfully
     */
    boolean deleteDevices(String idsStr);

    /**
     * Retrieves all devices belonging to a specific department.
     * Subject to @DataPermission filtering based on user's access rights.
     *
     * @param deptId the department ID
     * @return list of devices in the department
     */
    List<IotDeviceVO> getDevicesByDepartment(Long deptId);

    /**
     * Retrieves devices by status (active, inactive, disabled).
     *
     * @param status the device status
     * @return list of devices with the specified status
     */
    List<IotDeviceVO> getDevicesByStatus(String status);

    /**
     * Updates the status of a device by its ID.
     *
     * @param deviceId the device ID (UUID)
     * @param status   the new status
     * @return true if the update was successful
     */
    boolean updateDeviceStatus(UUID deviceId, String status);

    /**
     * Updates the last seen timestamp for a device.
     * Called when device sends data or heartbeat.
     *
     * @param deviceId the device ID (UUID)
     * @return true if the update was successful
     */
    boolean updateDeviceLastSeen(UUID deviceId);

    /**
     * Retrieves online devices (communicated within threshold).
     *
     * @param minutesThreshold minutes threshold for considering device online
     * @return list of online devices
     */
    List<IotDeviceVO> getOnlineDevices(int minutesThreshold);

    /**
     * Retrieves offline devices in a specific department.
     *
     * @param deptId           the department ID
     * @param minutesThreshold minutes threshold for considering device offline
     * @return list of offline devices in the department
     */
    List<IotDeviceVO> getOfflineDevicesInDept(Long deptId, int minutesThreshold);

    /**
     * Spatial query: Find devices within a radius from a center point.
     *
     * @param centerLat the center latitude
     * @param centerLng the center longitude
     * @param radiusKm  the search radius in kilometers
     * @return list of devices within the specified radius
     */
    List<IotDeviceVO> getDevicesWithinRadius(Double centerLat, Double centerLng, Double radiusKm);

    /**
     * Spatial query: Find nearest devices to a point.
     *
     * @param centerLat the center latitude
     * @param centerLng the center longitude
     * @param limit     the maximum number of devices to return
     * @return list of nearest devices ordered by distance
     */
    List<IotDeviceVO> getNearestDevices(Double centerLat, Double centerLng, Integer limit);

    /**
     * Gets device statistics for a department.
     *
     * @param deptId the department ID
     * @return device statistics (total, active, inactive, etc.)
     */
    DeviceStatsVO getDeviceStats(Long deptId);

    /**
     * Device statistics value object.
     */
    class DeviceStatsVO {
        private Long totalDevices;
        private Long activeDevices;
        private Long inactiveDevices;
        private Long disabledDevices;
        private Long onlineDevices;
        private Long offlineDevices;

        // Constructors, getters, setters
        public DeviceStatsVO() {}

        public DeviceStatsVO(Long totalDevices, Long activeDevices, Long inactiveDevices, 
                           Long disabledDevices, Long onlineDevices, Long offlineDevices) {
            this.totalDevices = totalDevices;
            this.activeDevices = activeDevices;
            this.inactiveDevices = inactiveDevices;
            this.disabledDevices = disabledDevices;
            this.onlineDevices = onlineDevices;
            this.offlineDevices = offlineDevices;
        }

        // Getters and setters
        public Long getTotalDevices() { return totalDevices; }
        public void setTotalDevices(Long totalDevices) { this.totalDevices = totalDevices; }
        
        public Long getActiveDevices() { return activeDevices; }
        public void setActiveDevices(Long activeDevices) { this.activeDevices = activeDevices; }
        
        public Long getInactiveDevices() { return inactiveDevices; }
        public void setInactiveDevices(Long inactiveDevices) { this.inactiveDevices = inactiveDevices; }
        
        public Long getDisabledDevices() { return disabledDevices; }
        public void setDisabledDevices(Long disabledDevices) { this.disabledDevices = disabledDevices; }
        
        public Long getOnlineDevices() { return onlineDevices; }
        public void setOnlineDevices(Long onlineDevices) { this.onlineDevices = onlineDevices; }
        
        public Long getOfflineDevices() { return offlineDevices; }
        public void setOfflineDevices(Long offlineDevices) { this.offlineDevices = offlineDevices; }
    }

}
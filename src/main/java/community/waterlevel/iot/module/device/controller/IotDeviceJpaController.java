package community.waterlevel.iot.module.device.controller;

import community.waterlevel.iot.common.annotation.Log;
import community.waterlevel.iot.common.annotation.RepeatSubmit;
import community.waterlevel.iot.common.enums.LogModuleEnum;
import community.waterlevel.iot.common.result.PageResult;
import community.waterlevel.iot.common.result.Result;
import community.waterlevel.iot.module.device.model.form.IotDeviceForm;
import community.waterlevel.iot.module.device.model.query.IotDevicePageQuery;
import community.waterlevel.iot.module.device.model.vo.IotDeviceVO;
import community.waterlevel.iot.module.device.service.IotDeviceJpaService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * IotDeviceJpaController is a REST controller that provides endpoints for managing
 * IoT devices in the water level monitoring system.
 * Supports comprehensive device management including spatial queries, status tracking,
 * and department-based access control with CRUD operations.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/14
 */
@Tag(name = "03.IoT Device Controller")
@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class IotDeviceJpaController {

    private final IotDeviceJpaService deviceService;

    /**
     * Retrieves a paginated list of IoT devices based on query parameters.
     * Supports filtering by department, status, model, location, and spatial proximity.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return PageResult containing device page data
     */
    @Operation(summary = "Get paginated IoT device list")
    @GetMapping("/page")
    @Log(value = "Get paginated IoT device list", module = LogModuleEnum.OTHER)
    public PageResult<IotDeviceVO> getDevicePage(@Valid IotDevicePageQuery queryParams) {
        IPage<IotDeviceVO> result = deviceService.getDevicePage(queryParams);
        return PageResult.success(result);
    }

    /**
     * Creates a new IoT device in the system.
     *
     * @param deviceForm the device form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Create new IoT device")
    @PostMapping
    @RepeatSubmit
    @Log(value = "Create new IoT device", module = LogModuleEnum.OTHER)
    public Result<?> saveDevice(@RequestBody @Valid IotDeviceForm deviceForm) {
        boolean result = deviceService.saveDevice(deviceForm);
        return Result.judge(result);
    }

    /**
     * Retrieves the form data for a specific device by its ID.
     *
     * @param deviceId the device ID (UUID)
     * @return Result containing the device form data
     */
    @Operation(summary = "Get device form data")
    @GetMapping("/{deviceId}/form")
    @Log(value = "Get device form data", module = LogModuleEnum.OTHER)
    public Result<IotDeviceForm> getDeviceForm(
            @Parameter(description = "Device ID (UUID)") @PathVariable UUID deviceId) {
        IotDeviceForm formData = deviceService.getDeviceFormData(deviceId);
        return Result.success(formData);
    }

    /**
     * Updates an existing device by its ID.
     *
     * @param deviceId   the device ID (UUID)
     * @param deviceForm the device form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Update IoT device")
    @PutMapping("/{deviceId}")
    @Log(value = "Update IoT device", module = LogModuleEnum.OTHER)
    public Result<Void> updateDevice(
            @Parameter(description = "Device ID (UUID)") @PathVariable UUID deviceId,
            @RequestBody @Valid IotDeviceForm deviceForm) {
        boolean result = deviceService.updateDevice(deviceId, deviceForm);
        return Result.judge(result);
    }

    /**
     * Deletes one or more devices by their IDs.
     *
     * @param ids the device IDs (UUIDs), separated by commas
     * @return Result indicating success or failure
     */
    @Operation(summary = "Delete IoT devices")
    @DeleteMapping("/{ids}")
    @Log(value = "Delete IoT devices", module = LogModuleEnum.OTHER)
    public Result<Void> deleteDevices(
            @Parameter(description = "Device IDs (UUIDs), separated by commas") @PathVariable String ids) {
        boolean result = deviceService.deleteDevices(ids);
        return Result.judge(result);
    }

    /**
     * Updates the status of a device (active/inactive/disabled).
     *
     * @param deviceId the device ID (UUID)
     * @param status   the device status
     * @return Result indicating success or failure
     */
    @Operation(summary = "Update device status")
    @PatchMapping("/{deviceId}/status")
    @Log(value = "Update device status", module = LogModuleEnum.OTHER)
    public Result<Void> updateDeviceStatus(
            @Parameter(description = "Device ID (UUID)") @PathVariable UUID deviceId,
            @Parameter(description = "Device status (active/inactive/disabled)") @RequestParam String status) {
        boolean result = deviceService.updateDeviceStatus(deviceId, status);
        return Result.judge(result);
    }

    /**
     * Updates the last seen timestamp for a device (called by IoT devices).
     *
     * @param deviceId the device ID (UUID)
     * @return Result indicating success or failure
     */
    @Operation(summary = "Update device last seen (heartbeat)")
    @PatchMapping("/{deviceId}/heartbeat")
    @Log(value = "Update device heartbeat", module = LogModuleEnum.OTHER)
    public Result<Void> updateDeviceHeartbeat(
            @Parameter(description = "Device ID (UUID)") @PathVariable UUID deviceId) {
        boolean result = deviceService.updateDeviceLastSeen(deviceId);
        return Result.judge(result);
    }

    /**
     * Retrieves devices belonging to a specific department.
     *
     * @param deptId the department ID
     * @return Result containing the list of devices
     */
    @Operation(summary = "Get devices by department")
    @GetMapping("/department/{deptId}")
    @Log(value = "Get devices by department", module = LogModuleEnum.OTHER)
    public Result<List<IotDeviceVO>> getDevicesByDepartment(
            @Parameter(description = "Department ID") @PathVariable Long deptId) {
        List<IotDeviceVO> devices = deviceService.getDevicesByDepartment(deptId);
        return Result.success(devices);
    }

    /**
     * Retrieves devices by status.
     *
     * @param status the device status (active/inactive/disabled)
     * @return Result containing the list of devices
     */
    @Operation(summary = "Get devices by status")
    @GetMapping("/status/{status}")
    @Log(value = "Get devices by status", module = LogModuleEnum.OTHER)
    public Result<List<IotDeviceVO>> getDevicesByStatus(
            @Parameter(description = "Device status") @PathVariable String status) {
        List<IotDeviceVO> devices = deviceService.getDevicesByStatus(status);
        return Result.success(devices);
    }

    /**
     * Retrieves currently online devices.
     *
     * @param minutesThreshold minutes threshold for considering device online (default: 30)
     * @return Result containing the list of online devices
     */
    @Operation(summary = "Get online devices")
    @GetMapping("/online")
    @Log(value = "Get online devices", module = LogModuleEnum.OTHER)
    public Result<List<IotDeviceVO>> getOnlineDevices(
            @Parameter(description = "Minutes threshold for online status") @RequestParam(defaultValue = "30") int minutesThreshold) {
        List<IotDeviceVO> devices = deviceService.getOnlineDevices(minutesThreshold);
        return Result.success(devices);
    }

    /**
     * Retrieves offline devices in a specific department.
     *
     * @param deptId           the department ID
     * @param minutesThreshold minutes threshold for considering device offline (default: 30)
     * @return Result containing the list of offline devices
     */
    @Operation(summary = "Get offline devices in department")
    @GetMapping("/department/{deptId}/offline")
    @Log(value = "Get offline devices in department", module = LogModuleEnum.OTHER)
    public Result<List<IotDeviceVO>> getOfflineDevicesInDept(
            @Parameter(description = "Department ID") @PathVariable Long deptId,
            @Parameter(description = "Minutes threshold for offline status") @RequestParam(defaultValue = "30") int minutesThreshold) {
        List<IotDeviceVO> devices = deviceService.getOfflineDevicesInDept(deptId, minutesThreshold);
        return Result.success(devices);
    }

    /**
     * Spatial query: Find devices within a radius from a center point.
     *
     * @param centerLat the center latitude
     * @param centerLng the center longitude
     * @param radiusKm  the search radius in kilometers
     * @return Result containing devices within the radius
     */
    @Operation(summary = "Find devices within radius (spatial query)")
    @GetMapping("/spatial/within-radius")
    @Log(value = "Spatial query: devices within radius", module = LogModuleEnum.OTHER)
    public Result<List<IotDeviceVO>> getDevicesWithinRadius(
            @Parameter(description = "Center latitude") @RequestParam Double centerLat,
            @Parameter(description = "Center longitude") @RequestParam Double centerLng,
            @Parameter(description = "Search radius in kilometers") @RequestParam Double radiusKm) {
        List<IotDeviceVO> devices = deviceService.getDevicesWithinRadius(centerLat, centerLng, radiusKm);
        return Result.success(devices);
    }

    /**
     * Spatial query: Find nearest devices to a point.
     *
     * @param centerLat the center latitude
     * @param centerLng the center longitude
     * @param limit     the maximum number of devices to return (default: 10)
     * @return Result containing nearest devices
     */
    @Operation(summary = "Find nearest devices (spatial query)")
    @GetMapping("/spatial/nearest")
    @Log(value = "Spatial query: nearest devices", module = LogModuleEnum.OTHER)
    public Result<List<IotDeviceVO>> getNearestDevices(
            @Parameter(description = "Center latitude") @RequestParam Double centerLat,
            @Parameter(description = "Center longitude") @RequestParam Double centerLng,
            @Parameter(description = "Maximum number of devices to return") @RequestParam(defaultValue = "10") Integer limit) {
        List<IotDeviceVO> devices = deviceService.getNearestDevices(centerLat, centerLng, limit);
        return Result.success(devices);
    }

    /**
     * Gets device statistics for a department.
     *
     * @param deptId the department ID
     * @return Result containing device statistics
     */
    @Operation(summary = "Get device statistics for department")
    @GetMapping("/department/{deptId}/stats")
    @Log(value = "Get department device statistics", module = LogModuleEnum.OTHER)
    public Result<IotDeviceJpaService.DeviceStatsVO> getDeviceStats(
            @Parameter(description = "Department ID") @PathVariable Long deptId) {
        IotDeviceJpaService.DeviceStatsVO stats = deviceService.getDeviceStats(deptId);
        return Result.success(stats);
    }

}
package community.waterlevel.iot.module.device.controller;

import community.waterlevel.iot.common.annotation.Log;
import community.waterlevel.iot.common.annotation.RepeatSubmit;
import community.waterlevel.iot.common.enums.LogModuleEnum;
import community.waterlevel.iot.common.result.Result;
import community.waterlevel.iot.module.device.model.form.IotDeviceForm;
import community.waterlevel.iot.module.device.model.vo.IotDeviceVO;
import community.waterlevel.iot.module.device.service.IotDeviceJpaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/* 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/14
 */

/**
 * REST controller for managing IoT devices.
 *
 * <p>
 * Provides endpoints to perform CRUD operations on IoT devices, list devices by
 * department or status, update device status and heartbeat, and perform simple
 * spatial queries (devices within a radius and nearest devices).
 * </p>
 *
 * <p>
 * All endpoints operate on `IotDeviceForm` for input and return `IotDeviceVO`
 * for output where applicable. The controller delegates business logic to
 * `IotDeviceJpaService`.
 * </p>
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
     * Get a list of devices belonging to the specified department.
     *
     * @param deptId the department identifier
     * @return a Result wrapping a list of `IotDeviceVO` for the department
     */
    @Operation(summary = "Get devices by department")
    @GetMapping("/department/{deptId}")
    @Log(value = "Get devices by department", module = LogModuleEnum.OTHER)
    public Result<List<IotDeviceVO>> getDevicesByDepartment(
            @Parameter(description = "Department ID") @PathVariable Long deptId) {
        List<IotDeviceVO> devices = deviceService.getDevicesByDept(deptId);
        return Result.success(devices);
    }

    /**
     * Get devices filtered by their status.
     *
     * @param status the status string (e.g. "active", "inactive", "disabled")
     * @return a Result wrapping a list of `IotDeviceVO` matching the status
     */
    @Operation(summary = "Get devices by status")
    @GetMapping("/status/{status}")
    @Log(value = "Get devices by status", module = LogModuleEnum.OTHER)
    public Result<List<IotDeviceVO>> getDevicesByStatus(
            @Parameter(description = "Status") @PathVariable String status) {
        List<IotDeviceVO> devices = deviceService.getDevicesByStatus(status);
        return Result.success(devices);
    }

    /**
     * Create a new IoT device record.
     *
     * @param device the device form payload
     * @return a Result indicating success or failure
     */
    @Operation(summary = "Create new IoT device")
    @PostMapping
    @RepeatSubmit
    @Log(value = "Create new IoT device", module = LogModuleEnum.OTHER)
    public Result<?> saveDevice(@RequestBody IotDeviceForm device) {
        boolean r = deviceService.saveDevice(device);
        return Result.judge(r);
    }

    /**
     * Update an existing IoT device.
     *
     * @param deviceId the UUID of the device to update
     * @param device   the device form payload with updated fields
     * @return a Result indicating success or failure
     */
    @Operation(summary = "Update IoT device")
    @PutMapping("/{deviceId}")
    @Log(value = "Update IoT device", module = LogModuleEnum.OTHER)
    public Result<Void> updateDevice(@Parameter(description = "Device ID") @PathVariable UUID deviceId,
            @RequestBody IotDeviceForm device) {
        boolean r = deviceService.updateDevice(deviceId, device);
        return Result.judge(r);
    }

    /**
     * Delete (soft-delete) one or more IoT devices by comma-separated UUIDs.
     *
     * @param ids comma-separated UUID string of devices to delete
     * @return a Result indicating success or failure
     */
    @Operation(summary = "Delete IoT devices")
    @DeleteMapping("/{ids}")
    @Log(value = "Delete IoT devices", module = LogModuleEnum.OTHER)
    public Result<Void> deleteDevices(@Parameter(description = "Comma-separated UUIDs") @PathVariable String ids) {
        boolean r = deviceService.deleteDevices(ids);
        return Result.judge(r);
    }

    /**
     * Update the status of a device (e.g. activate, deactivate, disable).
     *
     * @param deviceId the UUID of the device
     * @param status   the new status string
     * @return a Result indicating success or failure
     */
    @Operation(summary = "Update device status")
    @PatchMapping("/{deviceId}/status")
    @Log(value = "Update device status", module = LogModuleEnum.OTHER)
    public Result<Void> updateDeviceStatus(@Parameter(description = "Device ID") @PathVariable UUID deviceId,
            @RequestParam String status) {
        boolean r = deviceService.updateDeviceStatus(deviceId, status);
        return Result.judge(r);
    }

    /**
     * Update the device heartbeat / last seen timestamp to the current time.
     *
     * @param deviceId the UUID of the device
     * @return a Result indicating success or failure
     */
    @Operation(summary = "Update device heartbeat")
    @PatchMapping("/{deviceId}/heartbeat")
    @Log(value = "Update device heartbeat", module = LogModuleEnum.OTHER)
    public Result<Void> updateDeviceHeartbeat(@Parameter(description = "Device ID") @PathVariable UUID deviceId) {
        boolean r = deviceService.updateDeviceLastSeen(deviceId);
        return Result.judge(r);
    }

    /**
     * Find devices located within a given radius from the specified center point.
     *
     * <p>
     * Parameters are provided in decimal degrees for latitude and longitude,
     * and the radius is specified in kilometers. The implementation uses a
     * bounding-box filter followed by precise distance calculation in the
     * service layer.
     * </p>
     *
     * @param centerLat center latitude in decimal degrees
     * @param centerLng center longitude in decimal degrees
     * @param radiusKm  radius in kilometers
     * @return a Result wrapping a list of `IotDeviceVO` within the radius
     */
    @Operation(summary = "Find devices within radius (spatial)")
    @GetMapping("/spatial/within-radius")
    @Log(value = "Spatial: within radius", module = LogModuleEnum.OTHER)
    public Result<List<IotDeviceVO>> getDevicesWithinRadius(@RequestParam Double centerLat,
            @RequestParam Double centerLng, @RequestParam Double radiusKm) {
        List<IotDeviceVO> devices = deviceService.getDevicesWithinRadius(centerLat, centerLng, radiusKm);
        return Result.success(devices);
    }

    /**
     * Return the nearest devices to the provided center point, limited by the
     * `limit` parameter.
     *
     * @param centerLat center latitude in decimal degrees
     * @param centerLng center longitude in decimal degrees
     * @param limit     maximum number of nearest devices to return
     * @return a Result wrapping a list of `IotDeviceVO` sorted by ascending
     *         distance
     */
    @Operation(summary = "Find nearest devices (spatial)")
    @GetMapping("/spatial/nearest")
    @Log(value = "Spatial: nearest", module = LogModuleEnum.OTHER)
    public Result<List<IotDeviceVO>> getNearestDevices(@RequestParam Double centerLat, @RequestParam Double centerLng,
            @RequestParam(defaultValue = "10") Integer limit) {
        List<IotDeviceVO> devices = deviceService.getNearestDevices(centerLat, centerLng, limit);
        return Result.success(devices);
    }

    /**
     * Retrieves a list of devices based on query parameters. Data permission filtering
     * (role + department scope) is automatically applied by the AOP aspect when the
     * method receives a JPA Specification or is annotated with @DataPermission. This
     * endpoint mirrors the style of `MenuJpaController.listMenus`.
     *
     * @param queryParams filtering and search parameters for devices
     * @return Result containing a list of `IotDeviceVO` matching the query and data scope
     */
    @Operation(summary = "Retrieves a list of devices based on query parameters")
    @GetMapping
    public Result<List<IotDeviceVO>> listDevices(community.waterlevel.iot.module.device.model.query.IotDeviceQuery queryParams) {
        org.springframework.data.jpa.domain.Specification<community.waterlevel.iot.module.device.model.entity.IotDeviceJpa> spec = (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> preds = new java.util.ArrayList<>();

            if (queryParams.getKeywords() != null && !queryParams.getKeywords().isEmpty()) {
                preds.add(cb.like(root.get("deviceName"), "%" + queryParams.getKeywords() + "%"));
            }

            if (queryParams.getStatus() != null && !queryParams.getStatus().isEmpty()) {
                preds.add(cb.equal(root.get("status"), queryParams.getStatus()));
            }

            if (queryParams.getDeptId() != null) {
                preds.add(cb.equal(root.get("deptId"), queryParams.getDeptId()));
            }

            return preds.isEmpty() ? null : cb.and(preds.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        List<IotDeviceVO> devices = deviceService.listDevices(spec);
        return Result.success(devices);
    }

}

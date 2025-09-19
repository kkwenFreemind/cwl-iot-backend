package community.waterlevel.iot.module.device.service;

import community.waterlevel.iot.module.device.model.form.IotDeviceForm;
import community.waterlevel.iot.module.device.model.vo.IotDeviceVO;
import community.waterlevel.iot.module.device.repository.IotDeviceJpaRepository;
import community.waterlevel.iot.module.device.service.impl.IotDeviceJpaServiceImpl;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for IoT device management operations.
 *
 * <p>This interface defines the contract for comprehensive IoT device management
 * within the water level monitoring system. It provides methods for device lifecycle
 * management, geographic queries, status monitoring, and data access control.
 *
 * <p>Core functionality includes:
 * <ul>
 *   <li>Device CRUD operations with validation and audit trails</li>
 *   <li>Department-based data filtering and multi-tenant support</li>
 *   <li>Geographic queries for location-based device management</li>
 *   <li>Status management and operational monitoring</li>
 *   <li>Flexible querying with dynamic criteria support</li>
 *   <li>Data permission integration for security</li>
 * </ul>
 *
 * <p>Implementation requirements:
 * <ul>
 *   <li>All methods should handle data permission filtering</li>
 *   <li>Geographic operations should use appropriate spatial algorithms</li>
 *   <li>Status operations should validate against defined enums</li>
 *   <li>Batch operations should be atomic where possible</li>
 *   <li>Soft delete functionality should be implemented</li>
 * </ul>
 *
 * <p>Performance considerations:
 * <ul>
 *   <li>Geographic queries should be optimized for large datasets</li>
 *   <li>Database queries should leverage proper indexing</li>
 *   <li>Batch operations should minimize database round trips</li>
 *   <li>Caching should be considered for frequently accessed data</li>
 * </ul>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/15
 * @see IotDeviceJpaServiceImpl
 * @see IotDeviceJpaRepository
 * @see IotDeviceVO
 * @see IotDeviceForm
 */
public interface IotDeviceJpaService {

    /**
     * Retrieves all IoT devices belonging to a specific department.
     *
     * <p>This method provides department-scoped device access, supporting
     * organizational hierarchy and multi-tenant data isolation. The returned
     * devices should be enriched with department names for improved user experience.
     *
     * @param deptId the department identifier to filter devices
     * @return a list of device view objects with resolved department names,
     *         or an empty list if no devices are found
     * @throws IllegalArgumentException if deptId is null
     */
    List<IotDeviceVO> getDevicesByDept(Long deptId);

    /**
     * Retrieves all IoT devices with a specific operational status.
     *
     * <p>This method enables status-based device filtering for operational
     * monitoring and maintenance workflows. It supports all valid device
     * statuses and should enrich results with department names.
     *
     * <p>Supported status values:
     * <ul>
     *   <li>{@code "ACTIVE"} - Operational devices</li>
     *   <li>{@code "INACTIVE"} - Temporarily inactive devices</li>
     *   <li>{@code "DISABLED"} - Permanently disabled devices</li>
     * </ul>
     *
     * @param status the operational status to filter by (case-sensitive)
     * @return a list of device view objects with the specified status,
     *         or an empty list if no devices match
     * @throws IllegalArgumentException if status is null
     */
    List<IotDeviceVO> getDevicesByStatus(String status);

    /**
     * Creates a new IoT device with the provided form data.
     *
     * <p>This method handles the complete device creation process including
     * data validation, audit trail setup, and database persistence. It should
     * ensure that all required fields are properly initialized and business
     * rules are enforced.
     *
     * @param deviceForm the form data containing device information
     * @return {@code true} if the device was created successfully,
     *         {@code false} otherwise
     */
    boolean saveDevice(IotDeviceForm deviceForm);

    /**
     * Updates an existing IoT device with the provided form data.
     *
     * <p>This method performs partial or full updates to device information
     * while preserving data integrity and maintaining audit trails. It should
     * support flexible updates where only provided fields are modified.
     *
     * @param deviceId the UUID of the device to update
     * @param deviceForm the form data with updated information
     * @return {@code true} if the device was updated successfully,
     *         {@code false} if the device was not found
     */
    boolean updateDevice(UUID deviceId, IotDeviceForm deviceForm);

    /**
     * Deletes multiple IoT devices by their IDs.
     *
     * <p>This method performs bulk deletion of devices. It should process
     * a comma-separated string of device IDs and handle invalid IDs gracefully.
     * Implementation should consider using soft delete for data integrity.
     *
     * @param ids comma-separated string of device UUIDs to delete
     * @return {@code true} indicating the operation completed
     *         (individual failures should be logged but not stop the process)
     */
    boolean deleteDevices(String ids);

    /**
     * Updates the operational status of a specific IoT device.
     *
     * <p>This method provides targeted status updates for device management
     * and operational control. It's commonly used for enabling/disabling
     * devices or updating their operational state.
     *
     * @param deviceId the UUID of the device to update
     * @param status the new operational status (ACTIVE, INACTIVE, DISABLED)
     * @return {@code true} if the status was updated successfully,
     *         {@code false} if the device was not found
     */
    boolean updateDeviceStatus(UUID deviceId, String status);

    /**
     * Updates the last seen timestamp for a device.
     *
     * <p>This method is typically called when a device sends a heartbeat
     * or data transmission, indicating that the device is active and
     * communicating properly. It helps monitor device connectivity
     * and identify offline devices.
     *
     * @param deviceId the UUID of the device that was seen
     * @return {@code true} if the timestamp was updated successfully,
     *         {@code false} if the device was not found
     */
    boolean updateDeviceLastSeen(UUID deviceId);

    /**
     * Retrieves IoT devices within a specified radius of a center point.
     *
     * <p>This method performs geographic filtering to find devices within
     * a circular area defined by a center coordinate and radius. The
     * implementation should use efficient spatial algorithms for performance.
     *
     * @param centerLat the latitude of the center point
     * @param centerLng the longitude of the center point
     * @param radiusKm the search radius in kilometers
     * @return a list of devices within the specified radius,
     *         enriched with department names
     */
    List<IotDeviceVO> getDevicesWithinRadius(Double centerLat, Double centerLng, Double radiusKm);

    /**
     * Retrieves the nearest IoT devices to a specified location.
     *
     * <p>This method finds devices closest to a given coordinate using
     * appropriate distance calculation algorithms. Results should be
     * sorted by distance and limited to the specified count.
     *
     * @param centerLat the latitude of the reference point
     * @param centerLng the longitude of the reference point
     * @param limit the maximum number of devices to return
     * @return a list of nearest devices sorted by distance,
     *         enriched with department names
     */
    List<IotDeviceVO> getNearestDevices(Double centerLat, Double centerLng, Integer limit);

    /**
     * Retrieves IoT devices based on query parameters with data permission filtering.
     *
     * <p>This method provides flexible device querying with support for keyword
     * search, status filtering, and department-based access control. It should
     * apply data permission annotations for security and support dynamic criteria.
     *
     * <p>Query capabilities:
     * <ul>
     *   <li>Keyword search across device names (partial matching)</li>
     *   <li>Status-based filtering (exact match)</li>
     *   <li>Department-based filtering (exact match)</li>
     *   <li>Combined criteria using AND logic</li>
     *   <li>Automatic data permission filtering</li>
     * </ul>
     *
     * @param queryParams the query parameters for filtering devices
     * @return a list of device view objects matching the criteria,
     *         enriched with department names
     */
    List<IotDeviceVO> listDevices(community.waterlevel.iot.module.device.model.query.IotDeviceQuery queryParams);

    /**
     * Retrieves IoT devices based on a JPA Specification with data permission filtering.
     *
     * <p>This method executes dynamic queries constructed using JPA Criteria API
     * and applies department-based data permission filtering. It serves as the
     * core method for flexible device querying with advanced filtering capabilities.
     *
     * <p>Features:
     * <ul>
     *   <li>Dynamic query execution via Specification</li>
     *   <li>Automatic data permission filtering</li>
     *   <li>Department name resolution for enriched responses</li>
     *   <li>Soft delete filtering (automatic)</li>
     *   <li>Complex query composition support</li>
     * </ul>
     *
     * @param specification the JPA specification defining query criteria
     * @return a list of device view objects matching the specification,
     *         enriched with department names
     */
    java.util.List<IotDeviceVO> listDevices(org.springframework.data.jpa.domain.Specification<community.waterlevel.iot.module.device.model.entity.IotDeviceJpa> specification);

    /**
     * Retrieves EMQX configuration for a specific IoT device.
     *
     * <p>This method provides access to the EMQX-specific configuration data
     * for a device, including MQTT authentication credentials and topic information.
     * This information is essential for device connectivity and MQTT communication.
     *
     * <p>Configuration includes:
     * <ul>
     *   <li>EMQX username and password for MQTT authentication</li>
     *   <li>MQTT client identifier for unique device identification</li>
     *   <li>Telemetry topic for data publishing</li>
     *   <li>Command topic for receiving control commands</li>
     * </ul>
     *
     * <p>Security considerations:
     * <ul>
     *   <li>Data permission filtering is applied based on department access</li>
     *   <li>Credentials should be transmitted over secure channels</li>
     *   <li>Access should be restricted to authorized users only</li>
     * </ul>
     *
     * @param deviceId the UUID of the device to retrieve EMQX configuration for
     * @return the EMQX device configuration if found, null otherwise
     * @throws IllegalArgumentException if deviceId is null
     */
    community.waterlevel.iot.module.device.model.vo.EmqxDeviceConfigVO getDeviceEmqxConfig(java.util.UUID deviceId);
}

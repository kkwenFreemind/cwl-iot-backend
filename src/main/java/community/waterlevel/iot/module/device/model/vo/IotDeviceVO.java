package community.waterlevel.iot.module.device.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * View Object (VO) for IoT device data presentation in API responses.
 *
 * <p>This class serves as the data transfer object for presenting IoT device
 * information to API clients. It provides a client-friendly representation
 * of device data with resolved relationships and formatted information
 * suitable for user interfaces and API consumers.
 *
 * <p>Key characteristics:
 * <ul>
 *   <li>Client-optimized data structure for API responses</li>
 *   <li>Resolved department name from ID for better UX</li>
 *   <li>String-based status for flexible client handling</li>
 *   <li>Comprehensive device information including metadata</li>
 *   <li>Direct mapping from {@link community.waterlevel.iot.module.device.model.entity.IotDeviceJpa} entity</li>
 * </ul>
 *
 * <p>Data enrichment features:
 * <ul>
 *   <li>Department name resolution from deptId</li>
 *   <li>Status conversion to user-friendly string format</li>
 *   <li>Audit timestamps for tracking and monitoring</li>
 *   <li>Geographic coordinates for mapping applications</li>
 * </ul>
 *
 * <p>Usage scenarios:
 * <ul>
 *   <li>Device listing APIs with resolved department names</li>
 *   <li>Device detail views with complete information</li>
 *   <li>Dashboard displays with status and location data</li>
 *   <li>Mobile applications requiring lightweight data structures</li>
 *   <li>Reporting systems with enriched device information</li>
 * </ul>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/15
 * @see community.waterlevel.iot.module.device.model.entity.IotDeviceJpa
 * @see community.waterlevel.iot.module.device.model.form.IotDeviceForm
 * @see community.waterlevel.iot.module.device.converter.IotDeviceJpaConverter
 */
@Data
public class IotDeviceVO {

    /**
     * Unique identifier for the IoT device.
     *
     * <p>This UUID serves as the primary key and unique identifier for
     * the device across the entire system. It is used for device-specific
     * operations like updates, status changes, and detailed queries.
     *
     * <p>Format: RFC 4122 UUID
     * <p>Usage: Primary identifier in API responses and client applications
     */
    private UUID deviceId;

    /**
     * Human-readable name of the IoT device.
     *
     * <p>This field contains the display name of the device as configured
     * by administrators. It provides a meaningful identifier for users
     * to recognize and manage their devices.
     *
     * <p>Examples: "Baseball Stadium South Side", "River Monitoring Station #1"
     * <p>Usage: UI display, device selection, and user identification
     */
    private String deviceName;

    /**
     * Department identifier that owns the device.
     *
     * <p>Numeric identifier linking the device to its managing department.
     * This field supports organizational hierarchy and access control
     * but is typically supplemented by the resolved department name.
     *
     * <p>Usage: Internal processing, API filtering, and relationship mapping
     * <p>Foreign Key: References department table
     */
    private Long deptId;

    /**
     * Resolved department name for user-friendly display.
     *
     * <p>Human-readable name of the department that owns this device.
     * This field is populated dynamically in the service layer by
     * resolving the deptId against the department repository.
     *
     * <p>This field enhances user experience by providing immediate
     * department context without requiring additional API calls.
     *
     * <p>Examples: "Community_A", "Operations Department"
     * <p>Population: Resolved in service layer from department repository
     * <p>Usage: UI display, reporting, and user-friendly interfaces
     */
    private String deptName;

    /**
     * Model or type specification of the IoT device.
     *
     * <p>Describes the hardware model, sensor type, or device category.
     * This information helps users understand the device's capabilities
     * and maintenance requirements.
     *
     * <p>Examples: "water", "temperature", "pressure", "multi-sensor"
     * <p>Usage: Device categorization, maintenance planning, and analytics
     */
    private String deviceModel;

    /**
     * Latitude coordinate of the device's physical location.
     *
     * <p>WGS84 latitude coordinate where the device is deployed.
     * Used by mapping applications and location-based services
     * for device visualization and proximity calculations.
     *
     * <p>Range: -90.0 to 90.0 degrees
     * <p>Format: Decimal degrees
     * <p>Usage: Geographic mapping, location services, and spatial queries
     */
    private Double latitude;

    /**
     * Longitude coordinate of the device's physical location.
     *
     * <p>WGS84 longitude coordinate where the device is deployed.
     * Used by mapping applications and location-based services
     * for device visualization and proximity calculations.
     *
     * <p>Range: -180.0 to 180.0 degrees
     * <p>Format: Decimal degrees
     * <p>Usage: Geographic mapping, location services, and spatial queries
     */
    private Double longitude;

    /**
     * Human-readable description of the device's location.
     *
     * <p>Textual description providing context about where the device
     * is physically located. This complements the coordinate data
     * with descriptive information for better user understanding.
     *
     * <p>Examples: "Taipei Municipal Tianmu Baseball Stadium", "Near downtown bridge"
     * <p>Usage: UI display, location identification, and user context
     */
    private String location;

    /**
     * Current operational status of the IoT device.
     *
     * <p>String representation of the device's operational state.
     * This field indicates whether the device is actively working,
     * temporarily inactive, or permanently disabled.
     *
     * <p>Valid values: "ACTIVE", "INACTIVE", "DISABLED"
     * <p>Usage: Status monitoring, operational dashboards, and device management
     * <p>Display: User-friendly status indicators in interfaces
     */
    private String status;

    /**
     * Timestamp of the device's last communication or data transmission.
     *
     * <p>Records when the device was last seen active and transmitting data.
     * This field is crucial for monitoring device health, detecting
     * connectivity issues, and identifying offline devices.
     *
     * <p>Usage: Device health monitoring, connectivity alerts, and maintenance scheduling
     * <p>Updates: Automatically updated on successful data transmission
     * <p>Format: ISO 8601 timestamp with timezone information
     */
    private LocalDateTime lastSeen;

    /**
     * Timestamp when the device record was created in the system.
     *
     * <p>Records the exact date and time when the device was first
     * registered in the system. Used for audit trails and historical
     * analysis of device deployment.
     *
     * <p>Usage: Audit trails, device lifecycle tracking, and historical reporting
     * <p>Format: ISO 8601 timestamp with timezone information
     * <p>Note: Set automatically during device registration
     */
    private LocalDateTime createdAt;
}

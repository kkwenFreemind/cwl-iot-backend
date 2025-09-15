package community.waterlevel.iot.module.device.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Query object for filtering and searching IoT devices in the system.
 *
 * <p>This class encapsulates the search criteria used for device listing and
 * filtering operations. It provides a structured way to specify search parameters
 * that are applied to device queries, supporting both simple and advanced filtering
 * based on device attributes and organizational hierarchy.
 *
 * <p>The query object supports the following filtering capabilities:
 * <ul>
 *   <li>Text search across device names using keywords</li>
 *   <li>Status-based filtering for operational state management</li>
 *   <li>Department-based filtering for organizational access control</li>
 * </ul>
 *
 * <p>Usage scenarios:
 * <ul>
 *   <li>Device management dashboards with search and filter capabilities</li>
 *   <li>API endpoints for device listing with pagination and sorting</li>
 *   <li>Administrative interfaces for device monitoring and control</li>
 *   <li>Reporting systems requiring filtered device datasets</li>
 * </ul>
 *
 * <p>All fields are optional, allowing for flexible query construction. When
 * multiple criteria are provided, they are combined using AND logic in the
 * database query layer.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/15
 * @see community.waterlevel.iot.module.device.service.IotDeviceJpaService#listDevices(IotDeviceQuery)
 * @see community.waterlevel.iot.module.device.controller.IotDeviceJpaController
 */
@Schema(description = "IoT device query object")
@Data
public class IotDeviceQuery {

    /**
     * Keyword search parameter for device name filtering.
     *
     * <p>This field enables text-based search across device names using partial
     * matching. The search is case-insensitive and supports substring matching,
     * allowing users to find devices by typing partial names or descriptions.
     *
     * <p>Search behavior:
     * <ul>
     *   <li>Case-insensitive matching</li>
     *   <li>Partial string matching (contains operation)</li>
     *   <li>Supports multiple words or phrases</li>
     *   <li>Applied to device_name database column</li>
     * </ul>
     *
     * <p>Examples:
     * <ul>
     *   <li>"Baseball" matches "Baseball Stadium South Side"</li>
     *   <li>"River" matches "River Monitoring Station"</li>
     *   <li>"Station" matches any device with "station" in the name</li>
     * </ul>
     *
     * <p>Validation: Optional field, no length restrictions
     * <p>Database impact: Uses LIKE operator with wildcards
     */
    @Schema(description = "Keyword (device name)")
    private String keywords;

    /**
     * Device operational status filter.
     *
     * <p>Filters devices based on their current operational status. This field
     * allows administrators and users to view devices in specific states,
     * supporting operational monitoring and maintenance workflows.
     *
     * <p>Valid status values:
     * <ul>
     *   <li>{@code "ACTIVE"} - Devices that are operational and transmitting data</li>
     *   <li>{@code "INACTIVE"} - Devices that are registered but not currently active</li>
     *   <li>{@code "DISABLED"} - Devices that are disabled and not operational</li>
     * </ul>
     *
     * <p>Search behavior:
     * <ul>
     *   <li>Exact string matching (case-sensitive)</li>
     *   <li>Applied to status database column</li>
     *   <li>Supports single status selection</li>
     * </ul>
     *
     * <p>Use cases:
     * <ul>
     *   <li>Monitor active devices for real-time data</li>
     *   <li>Identify inactive devices for maintenance</li>
     *   <li>Review disabled devices for reactivation decisions</li>
     * </ul>
     *
     * <p>Validation: Optional field, validated against allowed status values
     * <p>Database impact: Uses equality comparison
     */
    @Schema(description = "Status (active/inactive/disabled)")
    private String status;

    /**
     * Department identifier for organizational filtering.
     *
     * <p>Restricts the device search to a specific department, implementing
     * organizational hierarchy and access control. This field is crucial for
     * multi-tenant environments where users should only see devices from
     * their authorized departments.
     *
     * <p>Security implications:
     * <ul>
     *   <li>Combined with data permission annotations</li>
     *   <li>Supports department-based access control</li>
     *   <li>Prevents unauthorized access to other departments' devices</li>
     * </ul>
     *
     * <p>Search behavior:
     * <ul>
     *   <li>Exact numeric matching</li>
     *   <li>Applied to dept_id database column</li>
     *   <li>Supports hierarchical department structures</li>
     * </ul>
     *
     * <p>Use cases:
     * <ul>
     *   <li>Department-specific device dashboards</li>
     *   <li>Organizational reporting and analytics</li>
     *   <li>Access control for multi-department systems</li>
     * </ul>
     *
     * <p>Validation: Optional field, must reference valid department
     * <p>Database impact: Uses equality comparison with foreign key
     * <p>Foreign Key: References sys_dept(id)
     */
    @Schema(description = "Department ID to filter devices")
    private Long deptId;
}

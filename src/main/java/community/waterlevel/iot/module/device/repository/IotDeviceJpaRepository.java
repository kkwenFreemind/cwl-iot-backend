package community.waterlevel.iot.module.device.repository;

import community.waterlevel.iot.module.device.model.entity.IotDeviceJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository interface for IoT device data access operations.
 *
 * <p>This repository provides comprehensive data access functionality for IoT devices,
 * supporting both standard CRUD operations and custom query methods. It extends
 * Spring Data JPA's core interfaces to provide type-safe database operations
 * with automatic query generation and execution.
 *
 * <p>Key capabilities:
 * <ul>
 *   <li>Standard CRUD operations (Create, Read, Update, Delete)</li>
 *   <li>Custom query methods for department and status filtering</li>
 *   <li>Specification-based dynamic queries for complex filtering</li>
 *   <li>Soft delete support through entity-level configuration</li>
 *   <li>Automatic query optimization and caching</li>
 * </ul>
 *
 * <p>Inheritance hierarchy:
 * <ul>
 *   <li>{@link JpaRepository} - Basic CRUD operations and query methods</li>
 *   <li>{@link JpaSpecificationExecutor} - Dynamic query construction support</li>
 * </ul>
 *
 * <p>The repository automatically handles:
 * <ul>
 *   <li>Entity mapping to/from database tables</li>
 *   <li>Transaction management for data operations</li>
 *   <li>Query optimization and execution planning</li>
 *   <li>Soft delete filtering through {@code @SQLRestriction}</li>
 * </ul>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/15
 * @see IotDeviceJpa
 * @see JpaRepository
 * @see JpaSpecificationExecutor
 */
@Repository
public interface IotDeviceJpaRepository extends JpaRepository<IotDeviceJpa, UUID>, JpaSpecificationExecutor<IotDeviceJpa> {

    /**
     * Retrieves all IoT devices belonging to a specific department.
     *
     * <p>This method queries devices based on their department affiliation,
     * supporting organizational hierarchy and access control requirements.
     * The query automatically respects soft delete constraints through
     * the entity's {@code @SQLRestriction} configuration.
     *
     * <p>Query behavior:
     * <ul>
     *   <li>Filters by exact department ID match</li>
     *   <li>Excludes soft-deleted devices automatically</li>
     *   <li>Orders results by default entity sorting (if configured)</li>
     *   <li>Supports department-based security filtering</li>
     * </ul>
     *
     * <p>Performance considerations:
     * <ul>
     *   <li>Uses indexed {@code dept_id} column for efficient lookup</li>
     *   <li>Suitable for department-specific dashboards and reports</li>
     *   <li>Can be combined with other filtering criteria</li>
     * </ul>
     *
     * @param deptId the department identifier to filter devices by
     * @return a list of IoT devices belonging to the specified department,
     *         or an empty list if no devices are found
     * @throws IllegalArgumentException if deptId is null
     *
     * @see IotDeviceJpa#getDeptId()
     * @see community.waterlevel.iot.module.device.service.IotDeviceJpaService#getDevicesByDept(Long)
     */
    @Query("SELECT d FROM IotDeviceJpa d WHERE d.deptId = :deptId")
    List<IotDeviceJpa> findByDeptId(@Param("deptId") Long deptId);

    /**
     * Retrieves all IoT devices with a specific operational status.
     *
     * <p>This method queries devices based on their current operational status,
     * enabling status-based monitoring, maintenance, and operational workflows.
     * The query automatically respects soft delete constraints and supports
     * device lifecycle management.
     *
     * <p>Query behavior:
     * <ul>
     *   <li>Filters by exact status string match</li>
     *   <li>Excludes soft-deleted devices automatically</li>
     *   <li>Supports all valid status values: ACTIVE, INACTIVE, DISABLED</li>
     *   <li>Case-sensitive matching for status values</li>
     * </ul>
     *
     * <p>Common use cases:
     * <ul>
     *   <li>Monitor active devices for real-time operations</li>
     *   <li>Identify inactive devices requiring attention</li>
     *   <li>Review disabled devices for maintenance decisions</li>
     *   <li>Generate status-based reports and analytics</li>
     * </ul>
     *
     * @param status the operational status to filter devices by
     *               (valid values: "ACTIVE", "INACTIVE", "DISABLED")
     * @return a list of IoT devices with the specified status,
     *         or an empty list if no devices match the status
     * @throws IllegalArgumentException if status is null
     *
     * @see IotDeviceJpa#getStatus()
     * @see community.waterlevel.iot.module.device.model.entity.DeviceStatus
     * @see community.waterlevel.iot.module.device.service.IotDeviceJpaService#getDevicesByStatus(String)
     */
    @Query("SELECT d FROM IotDeviceJpa d WHERE d.status = :status")
    List<IotDeviceJpa> findByStatus(@Param("status") String status);
}

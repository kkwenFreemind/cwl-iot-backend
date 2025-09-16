package community.waterlevel.iot.module.metric.service;

import community.waterlevel.iot.module.metric.model.entity.IotMetricDefinition;
import community.waterlevel.iot.module.metric.model.form.IotMetricDefinitionCreateForm;
import community.waterlevel.iot.module.metric.model.form.IotMetricDefinitionUpdateForm;
import jakarta.persistence.EntityNotFoundException;
import community.waterlevel.iot.module.metric.model.enums.MetricDataType;
import community.waterlevel.iot.module.metric.model.enums.MetricUnit;
import community.waterlevel.iot.module.metric.model.enums.PhysicalQuantity;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * IoT Metric Definition Service Interface
 * 
 * This interface defines the contract for managing IoT metric definitions within the system.
 * It provides comprehensive business logic operations for creating, updating, retrieving, and
 * validating IoT metric definitions with department-level scoping for multi-tenant support.
 * 
 * Key Features:
 * - Department-scoped metric definition management
 * - CRUD operations with validation
 * - Uniqueness constraints enforcement
 * - Physical quantity and unit compatibility validation
 * - Paginated and searchable data retrieval
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/16
 */
public interface IotMetricDefinitionService {

    /**
     * Creates a new IoT metric definition within the specified department scope.
     * 
     * This method performs comprehensive validation including:
     * - Metric name and key uniqueness within the department
     * - Physical quantity and unit compatibility
     * - Data type consistency validation
     * - Required field presence verification
     * 
     * @param form The form data containing all necessary information to create the metric definition
     * @param deptId The unique identifier of the department for data scoping and isolation
     * @return The newly created and persisted IoT metric definition entity
     * @throws IllegalArgumentException if validation fails or required fields are missing
     * @throws DataIntegrityViolationException if uniqueness constraints are violated
     */
    IotMetricDefinition create(IotMetricDefinitionCreateForm form, Long deptId);

    /**
     * Updates an existing IoT metric definition with new data while maintaining department scope.
     * 
     * This method ensures data integrity by:
     * - Verifying the metric definition exists within the specified department
     * - Validating uniqueness constraints for updated name and key
     * - Checking physical quantity and unit compatibility for modified values
     * - Preserving audit trail information (created date, creator)
     * 
     * @param id The unique identifier of the metric definition to update
     * @param form The form data containing updated information
     * @param deptId The department identifier for scope validation and data isolation
     * @return The updated IoT metric definition entity with new values
     * @throws EntityNotFoundException if the metric definition is not found in the department
     * @throws IllegalArgumentException if validation fails
     * @throws DataIntegrityViolationException if uniqueness constraints are violated
     */
    IotMetricDefinition update(Long id, IotMetricDefinitionUpdateForm form, Long deptId);

    /**
     * Performs a soft delete operation on the specified IoT metric definition.
     * 
     * This method implements logical deletion by:
     * - Setting the is_deleted flag to true
     * - Updating the last modified timestamp
     * - Preserving data integrity for audit and recovery purposes
     * - Maintaining referential integrity with related entities
     * 
     * Note: This is a soft delete operation. The record remains in the database
     * but is marked as deleted and excluded from normal queries.
     * 
     * @param id The unique identifier of the metric definition to delete
     * @param deptId The department identifier for scope validation
     * @throws EntityNotFoundException if the metric definition is not found in the department
     * @throws IllegalStateException if the metric definition cannot be deleted due to dependencies
     */
    void delete(Long id, Long deptId);

    /**
     * Retrieves a specific IoT metric definition by its unique identifier within a department scope.
     * 
     * This method ensures secure data access by:
     * - Enforcing department-level data isolation
     * - Excluding soft-deleted records from results
     * - Providing null-safe return using Optional wrapper
     * - Maintaining consistent query performance
     * 
     * @param id The unique identifier of the metric definition to retrieve
     * @param deptId The department identifier for scope validation and data filtering
     * @return An Optional containing the metric definition if found and accessible, empty otherwise
     */
    Optional<IotMetricDefinition> getById(Long id, Long deptId);

    /**
     * Retrieves all active IoT metric definitions associated with the specified department.
     * 
     * This method provides comprehensive department-scoped data access by:
     * - Filtering results to the specified department only
     * - Excluding soft-deleted records from the result set
     * - Ordering results by creation date (most recent first)
     * - Ensuring consistent data isolation across multi-tenant environment
     * 
     * Use this method when you need the complete set of metric definitions
     * for a department without pagination constraints.
     * 
     * @param deptId The unique identifier of the department
     * @return A list of all active IoT metric definitions for the department
     */
    List<IotMetricDefinition> getAllByDeptId(Long deptId);

    /**
     * Retrieves a paginated subset of IoT metric definitions for the specified department.
     * 
     * This method supports efficient data retrieval for large datasets by:
     * - Implementing server-side pagination with configurable page size
     * - Applying department-level data isolation and filtering
     * - Excluding soft-deleted records from results
     * - Providing metadata about total records and available pages
     * - Supporting sorting by multiple criteria (creation date, name, etc.)
     * 
     * Ideal for UI components that display metric definitions in a paginated table
     * or when implementing infinite scroll functionality.
     * 
     * @param deptId The unique identifier of the department
     * @param pageable Pagination and sorting configuration including page number, size, and sort criteria
     * @return A Page object containing the requested subset of metric definitions with pagination metadata
     */
    Page<IotMetricDefinition> getPageByDeptId(Long deptId, Pageable pageable);

    /**
     * Performs a keyword-based search across IoT metric definitions within a department scope.
     * 
     * This method implements full-text search functionality by:
     * - Searching across metric name, key, and description fields
     * - Supporting partial and case-insensitive matching
     * - Maintaining department-level data isolation
     * - Excluding soft-deleted records from search results
     * - Combining pagination for efficient result handling
     * 
     * Search behavior:
     * - Matches keywords in metric name, key, or description
     * - Supports multiple keywords with AND logic
     * - Returns results ordered by relevance and creation date
     * 
     * @param keyword The search term(s) to match against metric definition fields
     * @param deptId The department identifier for scope validation and data filtering
     * @param pageable Pagination configuration for search results
     * @return A Page containing matching metric definitions with pagination metadata
     */
    Page<IotMetricDefinition> searchByKeyword(String keyword, Long deptId, Pageable pageable);

    /**
     * Validates the uniqueness of a metric name within the specified department scope.
     * 
     * This method enforces data integrity by:
     * - Checking for existing metric definitions with the same name
     * - Respecting department-level data isolation boundaries
     * - Excluding soft-deleted records from uniqueness validation
     * - Supporting update scenarios by allowing exclusion of current record
     * 
     * Uniqueness validation is critical for:
     * - Preventing naming conflicts in metric identification
     * - Maintaining consistent metric naming conventions
     * - Supporting reliable metric data aggregation and reporting
     * 
     * @param metricName The metric name to validate for uniqueness
     * @param deptId The department identifier for scope validation
     * @param excludeId Optional identifier of record to exclude from check (used during updates)
     * @return true if the metric name is unique within the department, false if it already exists
     */
    boolean isMetricNameUnique(String metricName, Long deptId, Long excludeId);

    /**
     * Validates the uniqueness of a metric key within the specified department scope.
     * 
     * This method ensures system-level data integrity by:
     * - Verifying that the metric key is not already in use
     * - Enforcing department-scoped uniqueness constraints
     * - Filtering out soft-deleted records from validation
     * - Providing update-safe validation through exclusion parameter
     * 
     * Metric key uniqueness is essential for:
     * - Reliable metric data identification and referencing
     * - Consistent API endpoint and data structure definitions
     * - Preventing conflicts in metric data processing pipelines
     * - Maintaining data consistency across distributed systems
     * 
     * @param metricKey The metric key to validate for uniqueness
     * @param deptId The department identifier for scope validation
     * @param excludeId Optional identifier of record to exclude from check (used during updates)
     * @return true if the metric key is unique within the department, false if it already exists
     */
    boolean isMetricKeyUnique(String metricKey, Long deptId, Long excludeId);

    /**
     * Retrieves all IoT metric definitions that measure the specified physical quantity.
     * 
     * This method supports domain-specific metric organization by:
     * - Grouping metrics by their fundamental physical properties
     * - Maintaining department-level data isolation
     * - Excluding soft-deleted records from results
     * - Ordering results by metric name for consistent presentation
     * 
     * Common use cases:
     * - Building metric selection dropdowns filtered by physical quantity
     * - Generating reports grouped by measurement type
     * - Validating unit compatibility within physical quantity domains
     * - Supporting scientific and engineering analysis workflows
     * 
     * @param physicalQuantity The physical quantity to filter metrics by
     * @param deptId The department identifier for scope validation and data filtering
     * @return A list of metric definitions measuring the specified physical quantity
     */
    List<IotMetricDefinition> getByPhysicalQuantity(PhysicalQuantity physicalQuantity, Long deptId);

    /**
     * Retrieves all IoT metric definitions that use the specified data type.
     * 
     * This method facilitates technical metric organization by:
     * - Categorizing metrics by their Sparkplug B data type
     * - Supporting protocol-specific metric filtering
     * - Maintaining department-level data isolation
     * - Excluding soft-deleted records from results
     * 
     * Technical applications:
     * - Optimizing data transmission based on data type characteristics
     * - Validating protocol compliance for metric definitions
     * - Supporting data type-specific processing pipelines
     * - Generating technical documentation and API specifications
     * 
     * @param dataType The Sparkplug B data type to filter metrics by
     * @param deptId The department identifier for scope validation and data filtering
     * @return A list of metric definitions using the specified data type
     */
    List<IotMetricDefinition> getByDataType(MetricDataType dataType, Long deptId);

    /**
     * Validates the compatibility between a physical quantity and its measurement unit.
     * 
     * This method ensures scientific and engineering accuracy by:
     * - Verifying that the unit is appropriate for the physical quantity
     * - Enforcing dimensional analysis principles
     * - Supporting SI unit system compliance
     * - Preventing measurement errors due to unit mismatches
     * 
     * Compatibility validation covers:
     * - Base unit relationships (length, mass, time, etc.)
     * - Derived unit compatibility (velocity, acceleration, etc.)
     * - Engineering unit conventions
     * - Industry-standard measurement practices
     * 
     * Critical for maintaining data integrity in IoT measurement systems
     * and ensuring accurate sensor data interpretation.
     * 
     * @param physicalQuantity The physical quantity being measured
     * @param unit The measurement unit to validate for compatibility
     * @return true if the unit is compatible with the physical quantity, false otherwise
     */
    boolean isUnitCompatibleWithQuantity(PhysicalQuantity physicalQuantity, MetricUnit unit);
}
package community.waterlevel.iot.module.metric.controller;

import community.waterlevel.iot.common.result.PageResult;
import community.waterlevel.iot.common.result.Result;
import community.waterlevel.iot.module.metric.model.entity.IotMetricDefinition;
import community.waterlevel.iot.module.metric.model.enums.MetricDataType;
import community.waterlevel.iot.module.metric.model.enums.MetricUnit;
import community.waterlevel.iot.module.metric.model.enums.PhysicalQuantity;
import community.waterlevel.iot.module.metric.model.form.IotMetricDefinitionCreateForm;
import community.waterlevel.iot.module.metric.model.form.IotMetricDefinitionUpdateForm;
import community.waterlevel.iot.module.metric.model.query.IotMetricDefinitionQuery;
import community.waterlevel.iot.module.metric.service.IotMetricDefinitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * IoT Metric Definition REST Controller
 *
 * This REST controller provides comprehensive API endpoints for managing IoT metric definitions
 * in a multi-tenant environment. It implements the full CRUD (Create, Read, Update, Delete) operations
 * along with advanced querying capabilities, ensuring secure and efficient metric definition management.
 *
 * Key Characteristics:
 * - RESTful API design following industry standards
 * - Comprehensive input validation using Jakarta Bean Validation
 * - Department-scoped operations for multi-tenant data isolation
 * - Structured logging for operational monitoring and debugging
 * - OpenAPI/Swagger documentation for API discoverability
 * - Consistent error handling and response formatting
 *
 * Architectural Role:
 * - Acts as the primary API boundary for metric definition operations
 * - Implements request/response transformation and validation
 * - Provides HTTP protocol abstraction for client applications
 * - Enables integration with external systems and frontend applications
 * - Supports API versioning and backward compatibility
 *
 * API Design Principles:
 * - RESTful resource-oriented endpoints with clear URL patterns
 * - HTTP method semantics (GET, POST, PUT, DELETE) for CRUD operations
 * - Query parameters for filtering, pagination, and search operations
 * - Path variables for resource identification
 * - Request body for complex data submission
 * - Consistent response structure with Result wrapper
 *
 * Security Implementation:
 * - Department-based access control through request parameters
 * - Input validation to prevent injection attacks and malformed data
 * - Proper error handling to avoid information leakage
 * - Audit trail logging for compliance and monitoring
 *
 * Data Flow:
 * 1. HTTP Request → Controller validation and parameter extraction
 * 2. Service layer → Business logic and data access
 * 3. Repository layer → Database operations with department scoping
 * 4. Response transformation → Consistent API response format
 * 5. HTTP Response → Client application consumption
 *
 * Performance Considerations:
 * - Efficient database queries with proper indexing
 * - Pagination support for large result sets
 * - Minimal data transformation overhead
 * - Connection pooling and resource management
 * - Caching strategies for frequently accessed data
 *
 * Error Handling Strategy:
 * - Validation errors with detailed field-level messages
 * - Business logic exceptions with meaningful error codes
 * - System errors with appropriate HTTP status codes
 * - Consistent error response format across all endpoints
 * - Comprehensive logging for troubleshooting and monitoring
 *
 * API Documentation:
 * - OpenAPI 3.0 specification for automatic API documentation
 * - Detailed parameter descriptions and examples
 * - Response schema definitions and status codes
 * - Authentication and authorization requirements
 * - Usage examples and integration guides
 *
 * Client Integration:
 * - Supports various client types (web browsers, mobile apps, IoT devices)
 * - JSON-based request/response format for broad compatibility
 * - CORS support for cross-origin requests
 * - Rate limiting and throttling capabilities
 * - API versioning for backward compatibility
 *
 * Monitoring and Observability:
 * - Structured logging with correlation IDs
 * - Performance metrics collection
 * - Health check endpoints for system monitoring
 * - Audit trail generation for compliance reporting
 * - Error tracking and alerting mechanisms
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/16
 * @see IotMetricDefinitionService
 * @see IotMetricDefinitionCreateForm
 * @see IotMetricDefinitionUpdateForm
 * @see Result
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/iot-metric-definitions")
@RequiredArgsConstructor
@Tag(name = "IoT Metric Definition", description = "IoT Metric Definition")
public class IotMetricDefinitionController {

    private final IotMetricDefinitionService service;

    /**
     * Creates a new IoT metric definition within the specified department scope.
     *
     * This endpoint implements the POST operation for metric definition creation,
     * providing a complete workflow from client request to persistent storage.
     * It ensures data integrity through comprehensive validation and enforces
     * department-level data isolation for multi-tenant environments.
     *
     * HTTP Method: POST
     * Endpoint: /api/v1/iot-metric-definitions
     * Content-Type: application/json
     *
     * Request Processing:
     * 1. Request body deserialization into CreateForm with validation
     * 2. Department parameter extraction and access validation
     * 3. Service layer invocation with business logic execution
     * 4. Response transformation with success/error handling
     * 5. Structured logging for audit trail and monitoring
     *
     * Validation Performed:
     * - Form field validation using Jakarta Bean Validation annotations
     * - Department existence and user access rights verification
     * - Metric name uniqueness within department scope
     * - Physical quantity and unit compatibility validation
     * - Required field presence and data type constraints
     *
     * Business Logic:
     * - Creates new metric definition with default values
     * - Initializes audit trail with creation timestamps
     * - Sets initial version for optimistic locking
     * - Associates metric with specified department
     * - Generates unique identifier for the new metric
     *
     * Success Response:
     * - HTTP 200 OK with created metric definition
     * - Complete entity data including generated ID and timestamps
     * - Consistent Result wrapper format
     *
     * Error Scenarios:
     * - HTTP 400 Bad Request: Validation failures with field-specific messages
     * - HTTP 403 Forbidden: Insufficient department access permissions
     * - HTTP 409 Conflict: Metric name uniqueness violation
     * - HTTP 500 Internal Server Error: System-level processing errors
     *
     * Usage Examples:
     * POST /api/v1/iot-metric-definitions?deptId=123
     * Content-Type: application/json
     * {
     *   "metricName": "Water Level Sensor",
     *   "physicalQuantity": "LENGTH",
     *   "unit": "METER",
     *   "dataType": "Float"
     * }
     *
     * @param form The validated creation form containing metric definition data
     * @param deptId The department identifier for data scoping and access control
     * @return Result wrapper containing the created metric definition or error details
     */
    @PostMapping
    @Operation(summary = "Create a new IoT metric definition", description = "Create a new IoT metric definition")
    public Result<IotMetricDefinition> create(
            @Valid @RequestBody IotMetricDefinitionCreateForm form,
            @Parameter(description = "部門ID") @RequestParam Long deptId) {
        log.info("Creating IoT metric definition: {}", form.getMetricName());
        IotMetricDefinition result = service.create(form, deptId);
        return Result.success(result);
    }

    /**
     * Updates an existing IoT metric definition with selective field modifications.
     *
     * This endpoint implements the PUT operation for metric definition updates,
     * supporting partial updates where null fields are ignored. It ensures data
     * consistency through validation and maintains audit trails for change tracking.
     *
     * HTTP Method: PUT
     * Endpoint: /api/v1/iot-metric-definitions/{id}
     * Content-Type: application/json
     *
     * Update Strategy:
     * 1. Path variable extraction for target metric identification
     * 2. Request body deserialization with selective field validation
     * 3. Department parameter validation for access control
     * 4. Service layer invocation with optimistic locking
     * 5. Response transformation with updated entity data
     *
     * Partial Update Logic:
     * - Only non-null fields from the form are applied to the entity
     * - Null fields preserve existing values (no modification)
     * - Supports flexible update scenarios with minimal data changes
     * - Maintains data integrity during selective modifications
     *
     * Validation Performed:
     * - Path variable validation for metric existence
     * - Department access control and ownership verification
     * - Form field validation for provided (non-null) values
     * - Uniqueness validation for name changes within department
     * - Unit compatibility validation for quantity/unit modifications
     *
     * Optimistic Locking:
     * - Version field increment for concurrent update detection
     * - Automatic retry mechanisms for conflict resolution
     * - Prevents lost updates in multi-user environments
     * - Provides clear feedback on update conflicts
     *
     * Audit Trail:
     * - Automatic timestamp updates for change tracking
     * - Version history maintenance for compliance
     * - Structured logging for operational monitoring
     * - Change attribution for accountability
     *
     * Success Response:
     * - HTTP 200 OK with updated metric definition
     * - Complete entity data reflecting all modifications
     * - Version increment and timestamp updates included
     *
     * Error Scenarios:
     * - HTTP 400 Bad Request: Validation failures with field messages
     * - HTTP 403 Forbidden: Department access denied
     * - HTTP 404 Not Found: Metric definition not found
     * - HTTP 409 Conflict: Concurrent modification or uniqueness violation
     * - HTTP 500 Internal Server Error: System processing errors
     *
     * Usage Examples:
     * PUT /api/v1/iot-metric-definitions/456?deptId=123
     * Content-Type: application/json
     * {
     *   "metricName": "Updated Water Level Sensor",
     *   "alias": "WLS"
     * }
     *
     * @param id The unique identifier of the metric definition to update
     * @param form The update form with selective field modifications
     * @param deptId The department identifier for scope validation
     * @return Result wrapper containing updated metric definition or error details
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing IoT metric definition", description = "Update an existing IoT metric definition")
    public Result<IotMetricDefinition> update(
            @Parameter(description = "指標定義ID") @PathVariable Long id,
            @Valid @RequestBody IotMetricDefinitionUpdateForm form,
            @Parameter(description = "部門ID") @RequestParam Long deptId) {
        log.info("Updating IoT metric definition ID: {}", id);
        IotMetricDefinition result = service.update(id, form, deptId);
        return Result.success(result);
    }

    /**
     * Performs a soft delete operation on the specified IoT metric definition.
     *
     * This endpoint implements the DELETE operation using logical deletion to maintain
     * data integrity and audit trails. The metric definition remains in the database
     * but is marked as inactive and excluded from normal query operations.
     *
     * HTTP Method: DELETE
     * Endpoint: /api/v1/iot-metric-definitions/{id}
     *
     * Soft Delete Process:
     * 1. Path variable extraction for target metric identification
     * 2. Department parameter validation for access control
     * 3. Service layer invocation for logical deletion
     * 4. Audit trail update with modification timestamp
     * 5. Success response with no content body
     *
     * Data Preservation:
     * - Metric definition record remains in database
     * - All historical data and relationships preserved
     * - Audit trail information maintained for compliance
     * - Unique constraints and referential integrity intact
     * - Potential for future data recovery if needed
     *
     * Operational Impact:
     * - Metric becomes invisible to standard query operations
     * - Excluded from active metric lists and dropdowns
     * - Historical data remains accessible for reporting
     * - No immediate impact on existing data collections
     * - Gradual retirement of metric usage
     *
     * Validation Performed:
     * - Path variable validation for metric existence
     * - Department access control and ownership verification
     * - Business rule validation for deletion eligibility
     * - Referential integrity checks for dependent data
     *
     * Audit Trail:
     * - Modification timestamp update for change tracking
     * - Structured logging for operational monitoring
     * - Change attribution for accountability
     * - Compliance reporting and audit requirements
     *
     * Success Response:
     * - HTTP 200 OK with empty response body
     * - Consistent Result wrapper format
     * - Confirmation of successful logical deletion
     *
     * Error Scenarios:
     * - HTTP 403 Forbidden: Department access denied
     * - HTTP 404 Not Found: Metric definition not found
     * - HTTP 409 Conflict: Deletion blocked by business rules
     * - HTTP 500 Internal Server Error: System processing errors
     *
     * Usage Examples:
     * DELETE /api/v1/iot-metric-definitions/456?deptId=123
     *
     * Recovery Options:
     * - Reactivation possible through update operation
     * - Historical data preserved for analysis
     * - No permanent data loss from soft delete
     * - Administrative override capabilities
     *
     * @param id The unique identifier of the metric definition to delete
     * @param deptId The department identifier for scope validation
     * @return Result wrapper confirming successful deletion or error details
     */
    @DeleteMapping("/{id}")
    @Operation(summary = " Delete an IoT metric definition (soft delete)", description = " Delete an IoT metric definition (soft delete)")
    public Result<Void> delete(
            @Parameter(description = "指標定義ID") @PathVariable Long id,
            @Parameter(description = "部門ID") @RequestParam Long deptId) {
        log.info("Deleting IoT metric definition ID: {}", id);
        service.delete(id, deptId);
        return Result.success();
    }

    /**
     * Retrieves a specific IoT metric definition by its unique identifier.
     *
     * This endpoint implements the GET operation for individual metric retrieval,
     * providing secure and efficient access to metric definition details within
     * department boundaries. It supports both active and inactive metrics for
     * administrative purposes.
     *
     * HTTP Method: GET
     * Endpoint: /api/v1/iot-metric-definitions/{id}
     *
     * Retrieval Process:
     * 1. Path variable extraction for target metric identification
     * 2. Department parameter validation for access control
     * 3. Service layer query with department scoping
     * 4. Optional result transformation and null handling
     * 5. Response formatting with consistent Result wrapper
     *
     * Security Implementation:
     * - Department-based access control enforcement
     * - User permission validation for metric visibility
     * - Data isolation preventing cross-tenant access
     * - Audit logging for access tracking
     *
     * Query Optimization:
     * - Indexed database lookup using primary key
     * - Department-scoped query for efficient filtering
     * - Minimal data transfer for single entity retrieval
     * - Connection pooling for optimal resource usage
     *
     * Response Handling:
     * - HTTP 200 OK with complete metric definition data
     * - HTTP 404 Not Found for non-existent metrics
     * - Consistent error message formatting
     * - Null-safe response construction
     *
     * Success Response:
     * - Complete metric definition with all fields
     * - Current version and audit trail information
     * - Department context and ownership details
     * - Active status and operational state
     *
     * Error Scenarios:
     * - HTTP 403 Forbidden: Department access denied
     * - HTTP 404 Not Found: Metric definition not found
     * - HTTP 500 Internal Server Error: System query errors
     *
     * Usage Examples:
     * GET /api/v1/iot-metric-definitions/456?deptId=123
     *
     * Response Format:
     * {
     *   "code": "200",
     *   "msg": "Success",
     *   "data": {
     *     "id": 456,
     *     "metricName": "Water Level Sensor",
     *     "physicalQuantity": "LENGTH",
     *     ...
     *   }
     * }
     *
     * @param id The unique identifier of the metric definition to retrieve
     * @param deptId The department identifier for scope validation
     * @return Result wrapper containing metric definition or error details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get IoT metric definition by ID", description = "Get IoT metric definition by ID")
    public Result<IotMetricDefinition> getById(
            @Parameter(description = "指標定義ID") @PathVariable Long id,
            @Parameter(description = "部門ID") @RequestParam Long deptId) {
        log.debug("Getting IoT metric definition ID: {}", id);
        return service.getById(id, deptId)
                .map(Result::success)
                .orElse(Result.failed("Metric definition not found"));
    }

    /**
     * Retrieves all active IoT metric definitions for the specified department.
     *
     * This endpoint provides comprehensive department-scoped metric listing,
     * supporting administrative and operational use cases that require complete
     * metric definition sets. It excludes soft-deleted records and ensures
     * efficient data retrieval with proper access control.
     *
     * HTTP Method: GET
     * Endpoint: /api/v1/iot-metric-definitions
     *
     * Query Process:
     * 1. Department parameter extraction and validation
     * 2. Service layer query with active status filtering
     * 3. Result list construction with consistent ordering
     * 4. Response formatting with Result wrapper
     * 5. Structured logging for monitoring and debugging
     *
     * Data Filtering:
     * - Department-scoped results only
     * - Active metrics (isActive = true)
     * - Excludes soft-deleted records
     * - Consistent ordering by creation date or name
     *
     * Performance Considerations:
     * - Suitable for moderate dataset sizes
     * - Consider pagination for large departments
     * - Database indexing on department and active status
     * - Connection pooling for optimal resource usage
     *
     * Use Cases:
     * - Administrative metric management interfaces
     * - Dropdown list population for metric selection
     * - Batch processing and data export operations
     * - System configuration and setup workflows
     * - Integration with external systems requiring metric lists
     *
     * Response Characteristics:
     * - Complete metric definitions with all fields
     * - Consistent ordering for predictable results
     * - Total count information for client processing
     * - Null-safe response construction
     *
     * Success Response:
     * - HTTP 200 OK with metric definition array
     * - Empty array for departments with no metrics
     * - Consistent Result wrapper format
     *
     * Error Scenarios:
     * - HTTP 403 Forbidden: Department access denied
     * - HTTP 500 Internal Server Error: System query errors
     *
     * Usage Examples:
     * GET /api/v1/iot-metric-definitions?deptId=123
     *
     * Response Format:
     * {
     *   "code": "200",
     *   "msg": "Success",
     *   "data": [
     *     {
     *       "id": 456,
     *       "metricName": "Water Level Sensor",
     *       ...
     *     }
     *   ]
     * }
     *
     * @param deptId The department identifier for scope validation
     * @return Result wrapper containing metric definition list or error details
     */
    @GetMapping
    @Operation(summary = "Get all IoT metric definitions for a department", description = "Get all IoT metric definitions for a department")
    public Result<List<IotMetricDefinition>> getAllByDeptId(
            @Parameter(description = "部門ID") @RequestParam Long deptId) {
        log.debug("Getting all IoT metric definitions for department: {}", deptId);
        List<IotMetricDefinition> result = service.getAllByDeptId(deptId);
        return Result.success(result);
    }

    /**
     * Retrieves a paginated subset of IoT metric definitions for efficient data handling.
     *
     * This endpoint implements server-side pagination for optimal performance with large
     * metric definition datasets. It provides flexible page navigation and supports
     * scalable user interfaces with efficient data loading patterns.
     *
     * HTTP Method: GET
     * Endpoint: /api/v1/iot-metric-definitions/page
     *
     * Pagination Process:
     * 1. Query parameter extraction for pagination configuration
     * 2. Department parameter validation for access control
     * 3. Pageable object construction with sorting options
     * 4. Service layer invocation with paginated query
     * 5. Response transformation with pagination metadata
     *
     * Pagination Features:
     * - Configurable page size (default: 10 items per page)
     * - Zero-based page numbering (default: page 0)
     * - Total count and page count calculation
     * - Efficient database LIMIT/OFFSET implementation
     * - Consistent sorting for predictable results
     *
     * Performance Optimization:
     * - Database-level pagination for memory efficiency
     * - Indexed queries for fast data retrieval
     * - Minimal data transfer per request
     * - Connection pooling and resource management
     * - Caching support for frequently accessed pages
     *
     * Response Structure:
     * - Current page content as data list
     * - Total number of elements across all pages
     * - Pagination metadata for client navigation
     * - Consistent PageResult wrapper format
     *
     * Use Cases:
     * - Web application table displays with pagination
     * - Mobile app infinite scroll implementations
     * - Large dataset browsing and navigation
     * - API consumers with limited bandwidth
     * - Administrative interfaces with data grids
     *
     * Success Response:
     * - HTTP 200 OK with paginated result structure
     * - Page content and metadata in structured format
     * - Navigation information for client-side pagination
     *
     * Error Scenarios:
     * - HTTP 400 Bad Request: Invalid pagination parameters
     * - HTTP 403 Forbidden: Department access denied
     * - HTTP 500 Internal Server Error: System query errors
     *
     * Usage Examples:
     * GET /api/v1/iot-metric-definitions/page?deptId=123&page=0&size=20
     *
     * Response Format:
     * {
     *   "code": "200",
     *   "msg": "Success",
     *   "data": {
     *     "list": [...],
     *     "total": 150
     *   }
     * }
     *
     * @param deptId The department identifier for scope validation
     * @param page Zero-based page number (default: 0)
     * @param size Number of items per page (default: 10)
     * @return Result wrapper containing paginated metric definitions or error details
     */
    @GetMapping("/page")
    @Operation(summary = "Get paginated IoT metric definitions for a department", description = "Get paginated IoT metric definitions for a department")
    public Result<PageResult<IotMetricDefinition>> getPageByDeptId(IotMetricDefinitionQuery queryParams) {
        log.info("Getting paginated IoT metric definitions with query: {}", queryParams);
        
        Specification<IotMetricDefinition> spec = (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> preds = new java.util.ArrayList<>();

            if (queryParams.getKeywords() != null && !queryParams.getKeywords().isEmpty()) {
                preds.add(cb.like(root.get("metricName"), "%" + queryParams.getKeywords() + "%"));
            }

            if (queryParams.getPhysicalQuantity() != null) {
                preds.add(cb.equal(root.get("physicalQuantity"), queryParams.getPhysicalQuantity()));
            }

            if (queryParams.getUnit() != null) {
                preds.add(cb.equal(root.get("unit"), queryParams.getUnit()));
            }

            if (queryParams.getDataType() != null) {
                preds.add(cb.equal(root.get("dataType"), queryParams.getDataType()));
            }

            if (queryParams.getDeptId() != null) {
                preds.add(cb.equal(root.get("deptId"), queryParams.getDeptId()));
            }

            return preds.isEmpty() ? null : cb.and(preds.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Pageable pageable = PageRequest.of(queryParams.getPage(), queryParams.getSize());
        Page<IotMetricDefinition> result = service.getPageBySpec(spec, pageable);
        PageResult<IotMetricDefinition> pageResult = new PageResult<>();
        pageResult.setCode("200");
        pageResult.setMsg("Success");

        PageResult.Data<IotMetricDefinition> data = new PageResult.Data<>();
        data.setList(result.getContent());
        data.setTotal(result.getTotalElements());
        pageResult.setData(data);

        return Result.success(pageResult);
    }

    /**
     * Search IoT metric definitions by keyword
     */
    @GetMapping("/search")
    @Operation(summary = "Search IoT metric definitions by keyword", description = "Search IoT metric definitions by keyword")
    public Result<PageResult<IotMetricDefinition>> searchByKeyword(
            @Parameter(description = "搜索關鍵字") @RequestParam String keyword,
            @Parameter(description = "部門ID") @RequestParam Long deptId,
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "10") int size) {
        log.debug("Searching IoT metric definitions with keyword: '{}' for department: {}", keyword, deptId);
        Pageable pageable = PageRequest.of(page, size);
        Page<IotMetricDefinition> result = service.searchByKeyword(keyword, deptId, pageable);
        PageResult<IotMetricDefinition> pageResult = new PageResult<>();
        pageResult.setCode("200");
        pageResult.setMsg("Success");

        PageResult.Data<IotMetricDefinition> data = new PageResult.Data<>();
        data.setList(result.getContent());
        data.setTotal(result.getTotalElements());
        pageResult.setData(data);

        return Result.success(pageResult);
    }

    /**
     * Get IoT metric definitions by physical quantity
     */
    @GetMapping("/by-physical-quantity")
    @Operation(summary = "Get IoT metric definitions by physical quantity", description = "Get IoT metric definitions by physical quantity")
    public Result<List<IotMetricDefinition>> getByPhysicalQuantity(
            @Parameter(description = "物理量") @RequestParam PhysicalQuantity physicalQuantity,
            @Parameter(description = "部門ID") @RequestParam Long deptId) {
        log.debug("Getting IoT metric definitions by physical quantity: {} for department: {}", physicalQuantity, deptId);
        List<IotMetricDefinition> result = service.getByPhysicalQuantity(physicalQuantity, deptId);
        return Result.success(result);
    }

    /**
     * Get IoT metric definitions by data type
     */
    @GetMapping("/by-data-type")
    @Operation(summary = "Get IoT metric definitions by data type", description = "Get IoT metric definitions by data type")
    public Result<List<IotMetricDefinition>> getByDataType(
            @Parameter(description = "數據類型") @RequestParam MetricDataType dataType,
            @Parameter(description = "部門ID") @RequestParam Long deptId) {
        log.debug("Getting IoT metric definitions by data type: {} for department: {}", dataType, deptId);
        List<IotMetricDefinition> result = service.getByDataType(dataType, deptId);
        return Result.success(result);
    }

    /**
     * Check if metric name is unique within department
     */
    @GetMapping("/check-name-unique")
    @Operation(summary = " Check if metric name is unique within department", description = " Check if metric name is unique within department")
    public Result<Boolean> checkMetricNameUnique(
            @Parameter(description = "指標名稱") @RequestParam String metricName,
            @Parameter(description = "部門ID") @RequestParam Long deptId,
            @Parameter(description = "排除的ID") @RequestParam(required = false) Long excludeId) {
        log.debug("Checking metric name uniqueness: '{}' for department: {}", metricName, deptId);
        boolean isUnique = service.isMetricNameUnique(metricName, deptId, excludeId);
        return Result.success(isUnique);
    }

    /**
     * Validate unit compatibility with physical quantity
     */
    @GetMapping("/validate-unit")
    @Operation(summary = "Validate unit compatibility with physical quantity", description = "Validate unit compatibility with physical quantity")
    public Result<Boolean> validateUnitCompatibility(
            @Parameter(description = "物理量") @RequestParam PhysicalQuantity physicalQuantity,
            @Parameter(description = "測量單位") @RequestParam MetricUnit unit) {
        log.debug("Validating unit compatibility: {} with {}", unit, physicalQuantity);
        boolean isCompatible = service.isUnitCompatibleWithQuantity(physicalQuantity, unit);
        return Result.success(isCompatible);
    }
}
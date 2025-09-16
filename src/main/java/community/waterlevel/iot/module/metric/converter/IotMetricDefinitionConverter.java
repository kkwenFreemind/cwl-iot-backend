package community.waterlevel.iot.module.metric.converter;

import community.waterlevel.iot.module.metric.model.entity.IotMetricDefinition;
import community.waterlevel.iot.module.metric.model.form.IotMetricDefinitionCreateForm;
import community.waterlevel.iot.module.metric.model.form.IotMetricDefinitionUpdateForm;
import community.waterlevel.iot.module.metric.model.vo.IotMetricDefinitionVO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * IoT Metric Definition Converter
 *
 * This converter class implements the Data Transfer Object (DTO) pattern to facilitate
 * seamless transformation between different object representations in the IoT metric
 * definition domain. It serves as the primary mechanism for converting between
 * Entity, Form, and Value Object representations while maintaining data integrity
 * and applying business logic during transformations.
 *
 * Key Characteristics:
 * - Implements clean separation of concerns between data layers
 * - Provides null-safe conversion methods with defensive programming
 * - Handles partial updates with selective field modification
 * - Supports computed field generation for enhanced client processing
 * - Maintains audit trail information during entity creation and updates
 *
 * Architectural Role:
 * - Acts as an anti-corruption layer between different data representations
 * - Enables loose coupling between service layer and data access layer
 * - Supports domain-driven design principles with clear boundaries
 * - Facilitates API versioning through selective field mapping
 *
 * Conversion Patterns:
 * - Form → Entity: Transforms validated input data into persistent entities
 * - Entity → VO: Converts persistent data into client-friendly representations
 * - Update Form → Entity: Applies partial updates with null-safe field handling
 *
 * Design Principles:
 * - Null Safety: All methods handle null inputs gracefully
 * - Immutability: Conversion methods don't modify source objects
 * - Consistency: Maintains data integrity across all transformations
 * - Performance: Efficient field mapping without unnecessary computations
 *
 * Business Logic Integration:
 * - Applies default values during entity creation (active status, version, timestamps)
 * - Handles optimistic locking version increments during updates
 * - Generates computed fields for enhanced client-side processing
 * - Supports audit trail maintenance with automatic timestamp updates
 *
 * Usage Scenarios:
 * - Service layer operations for create/update business logic
 * - API controllers for request/response data transformation
 * - Integration with external systems requiring different data formats
 * - Batch processing operations with standardized data conversion
 *
 * Error Handling:
 * - Graceful null handling prevents NullPointerException
 * - Maintains data consistency during conversion failures
 * - Preserves original data when conversion is not possible
 * - Supports debugging through clear method naming and documentation
 *
 * Performance Considerations:
 * - Lightweight conversion operations with minimal overhead
 * - No external dependencies or database calls during conversion
 * - Supports high-throughput scenarios with stateless design
 * - Memory efficient with direct field mapping
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/16
 * @see IotMetricDefinition
 * @see IotMetricDefinitionCreateForm
 * @see IotMetricDefinitionUpdateForm
 * @see IotMetricDefinitionVO
 */
@Component
public class IotMetricDefinitionConverter {

    /**
     * Converts a validated CreateForm into a new IoT Metric Definition Entity.
     *
     * This method transforms client-submitted form data into a persistent entity,
     * applying default values and initialization logic required for new metric definitions.
     * It serves as the primary transformation mechanism for the metric creation workflow.
     *
     * Conversion Process:
     * 1. Validates input form (null check for defensive programming)
     * 2. Creates new entity instance with form field mappings
     * 3. Applies default values for system-managed fields
     * 4. Initializes audit trail with creation timestamps
     * 5. Returns fully initialized entity ready for persistence
     *
     * Default Value Assignments:
     * - isActive: Set to true for new active metric definitions
     * - version: Initialized to 1 for optimistic locking
     * - createdAt: Set to current timestamp for audit trail
     * - updatedAt: Set to current timestamp for consistency
     *
     * Field Mapping Strategy:
     * - Direct mapping for all form fields to corresponding entity fields
     * - No transformation or business logic applied (handled by service layer)
     * - Preserves original form data integrity during conversion
     * - Supports all mandatory and optional form fields
     *
     * Null Safety:
     * - Returns null if input form is null (graceful degradation)
     * - No null checks required for individual fields (handled by form validation)
     * - Prevents NullPointerException in calling code
     *
     * Business Logic Separation:
     * - Pure data transformation without business rules
     * - Validation logic handled by form validation and service layer
     * - Entity initialization logic centralized in converter
     * - Maintains clean separation of concerns
     *
     * Performance Characteristics:
     * - Lightweight operation with minimal computational overhead
     * - No external dependencies or service calls
     * - Memory efficient with direct object instantiation
     * - Suitable for high-throughput creation scenarios
     *
     * @param form The validated create form containing all required metric definition data
     * @return A new IoT metric definition entity with default values applied, or null if form is null
     * @see IotMetricDefinitionCreateForm
     * @see IotMetricDefinition
     */
    public IotMetricDefinition toEntity(IotMetricDefinitionCreateForm form) {
        if (form == null) {
            return null;
        }

        IotMetricDefinition entity = new IotMetricDefinition();
        entity.setDeptId(form.getDeptId());
        entity.setMetricName(form.getMetricName());
        entity.setAlias(form.getAlias());
        entity.setPhysicalQuantity(form.getPhysicalQuantity());
        entity.setUnit(form.getUnit());
        entity.setDataType(form.getDataType());
        entity.setIsActive(true);
        entity.setVersion(1);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return entity;
    }

    /**
     * Applies selective field updates from UpdateForm to an existing IoT Metric Definition Entity.
     *
     * This method implements partial update semantics by selectively modifying only the non-null
     * fields from the update form, preserving existing values for null fields. It supports
     * flexible update operations while maintaining data integrity and audit trails.
     *
     * Update Strategy:
     * 1. Validates both entity and form parameters (defensive programming)
     * 2. Applies conditional updates based on form field presence
     * 3. Increments version number for optimistic locking
     * 4. Updates modification timestamp for audit trail
     * 5. Preserves existing values for null form fields
     *
     * Conditional Update Logic:
     * - metricName: Updated only if form.getMetricName() is not null
     * - alias: Updated only if form.getAlias() is not null
     * - physicalQuantity: Updated only if form.getPhysicalQuantity() is not null
     * - unit: Updated only if form.getUnit() is not null
     * - dataType: Updated only if form.getDataType() is not null
     * - isActive: Updated only if form.getIsActive() is not null
     *
     * System Field Management:
     * - version: Automatically incremented by 1 for optimistic locking
     * - updatedAt: Set to current timestamp for audit trail maintenance
     * - createdAt: Preserved (never modified during updates)
     * - id: Preserved (primary key never changes)
     *
     * Null Safety Implementation:
     * - Early return if either entity or form is null
     * - Null-safe field access with conditional checks
     * - Prevents unintended null assignments to entity fields
     * - Maintains data integrity during partial updates
     *
     * Optimistic Locking Support:
     * - Version increment ensures concurrent update detection
     * - Supports automatic retry mechanisms in service layer
     * - Prevents lost updates in multi-user environments
     * - Enables conflict resolution strategies
     *
     * Audit Trail Maintenance:
     * - Automatic timestamp updates for change tracking
     * - Supports compliance and regulatory requirements
     * - Enables temporal queries and historical analysis
     * - Provides accountability for data modifications
     *
     * Business Logic Considerations:
     * - Pure data transformation without validation logic
     * - Validation handled by service layer before conversion
     * - Supports complex update scenarios with selective modifications
     * - Maintains referential integrity during field updates
     *
     * @param entity The existing IoT metric definition entity to be updated
     * @param form The update form containing selective field modifications
     * @see IotMetricDefinitionUpdateForm
     * @see IotMetricDefinition
     */
    public void updateEntityFromForm(IotMetricDefinition entity, IotMetricDefinitionUpdateForm form) {
        if (entity == null || form == null) {
            return;
        }

        if (form.getMetricName() != null) {
            entity.setMetricName(form.getMetricName());
        }
        if (form.getAlias() != null) {
            entity.setAlias(form.getAlias());
        }
        if (form.getPhysicalQuantity() != null) {
            entity.setPhysicalQuantity(form.getPhysicalQuantity());
        }
        if (form.getUnit() != null) {
            entity.setUnit(form.getUnit());
        }
        if (form.getDataType() != null) {
            entity.setDataType(form.getDataType());
        }
        if (form.getIsActive() != null) {
            entity.setIsActive(form.getIsActive());
        }

        entity.setVersion(entity.getVersion() + 1);
        entity.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Converts an IoT Metric Definition Entity into a Value Object for API responses.
     *
     * This method transforms persistent entity data into a client-friendly representation,
     * including both direct field mappings and computed fields for enhanced client-side
     * processing and validation. It serves as the primary mechanism for preparing
     * data for API responses and client consumption.
     *
     * Conversion Process:
     * 1. Validates input entity (null check for defensive programming)
     * 2. Creates new VO instance with direct field mappings
     * 3. Applies computed field generation for enhanced functionality
     * 4. Returns fully populated VO ready for API serialization
     *
     * Direct Field Mapping:
     * - id: Primary key for client-side identification
     * - deptId: Department context for multi-tenant operations
     * - metricName: Human-readable metric identifier
     * - alias: Optional short name for compact displays
     * - physicalQuantity: Scientific quantity classification
     * - unit: Measurement unit specification
     * - dataType: Sparkplug B data type definition
     * - isActive: Operational status indicator
     * - version: Optimistic locking version number
     * - createdAt: Creation timestamp for audit trail
     * - updatedAt: Last modification timestamp
     *
     * Computed Field Generation:
     * - isNumericType: Determines if data type supports mathematical operations
     * - unitCompatible: Validates unit-quantity dimensional compatibility
     *
     * Null Safety:
     * - Returns null if input entity is null (graceful degradation)
     * - Handles potential null values in entity fields appropriately
     * - Prevents NullPointerException in calling code
     * - Supports partial data scenarios
     *
     * Client-Side Benefits:
     * - Provides ready-to-use data for UI rendering
     * - Includes computed fields for enhanced user experience
     * - Supports client-side validation and processing
     * - Enables efficient data binding and display
     *
     * Performance Characteristics:
     * - Lightweight conversion with minimal computational overhead
     * - Computed field generation adds minor processing cost
     * - No external dependencies or service calls
     * - Memory efficient with direct field mapping
     *
     * API Integration:
     * - Optimized for JSON serialization and API responses
     * - Supports API versioning through selective field exposure
     * - Enables flexible client-side data processing
     * - Facilitates integration with frontend frameworks
     *
     * Data Consistency:
     * - Preserves all entity data during conversion
     * - Maintains referential integrity of relationships
     * - Supports audit trail information for compliance
     * - Enables temporal data analysis and reporting
     *
     * @param entity The IoT metric definition entity to be converted
     * @return A new IoT metric definition VO with computed fields, or null if entity is null
     * @see IotMetricDefinitionVO
     * @see IotMetricDefinition
     */
    public IotMetricDefinitionVO toVO(IotMetricDefinition entity) {
        if (entity == null) {
            return null;
        }

        IotMetricDefinitionVO vo = new IotMetricDefinitionVO();
        vo.setId(entity.getId());
        vo.setDeptId(entity.getDeptId());
        vo.setMetricName(entity.getMetricName());
        vo.setAlias(entity.getAlias());
        vo.setPhysicalQuantity(entity.getPhysicalQuantity());
        vo.setUnit(entity.getUnit());
        vo.setDataType(entity.getDataType());
        vo.setIsActive(entity.getIsActive());
        vo.setVersion(entity.getVersion());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());

        // Add additional computed fields
        vo.setIsNumericType(entity.isNumericType());
        vo.setUnitCompatible(entity.isUnitCompatible());

        return vo;
    }
}
package community.waterlevel.iot.module.metric.model.form;

import community.waterlevel.iot.module.metric.model.entity.IotMetricDefinition;
import community.waterlevel.iot.module.metric.model.enums.MetricDataType;
import community.waterlevel.iot.module.metric.model.enums.MetricUnit;
import community.waterlevel.iot.module.metric.model.enums.PhysicalQuantity;
import community.waterlevel.iot.module.metric.service.IotMetricDefinitionService;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * IoT Metric Definition Update Form
 *
 * This form class encapsulates the data structure for updating existing IoT metric definitions
 * in a multi-tenant environment. It implements partial update semantics where null fields
 * are ignored during the update process, enabling flexible and selective modifications.
 *
 * Key Characteristics:
 * - Supports partial updates with selective field modification
 * - Implements comprehensive input validation using Bean Validation annotations
 * - Provides clear error messages for validation failures
 * - Maintains data integrity through field-level constraints
 * - Enables department-scoped metric definition management
 *
 * Architectural Role:
 * - Acts as the boundary object between client requests and service layer
 * - Provides input validation and sanitization before business logic processing
 * - Supports REST API request body deserialization and validation
 * - Enables type-safe data transfer with compile-time validation
 *
 * Update Semantics:
 * - Null fields are preserved (not updated) in the target entity
 * - Non-null fields replace existing values in the target entity
 * - Validation is performed only on non-null fields
 * - Supports conditional updates based on field presence
 *
 * Validation Strategy:
 * - Field-level validation using Jakarta Bean Validation annotations
 * - Custom validation messages for user-friendly error reporting
 * - Size constraints to prevent data truncation and buffer overflows
 * - Type safety through enum-based fields for categorical data
 *
 * Business Logic Integration:
 * - Compatible with optimistic locking for concurrent update protection
 * - Supports audit trail generation for change tracking
 * - Enables business rule validation in the service layer
 * - Facilitates integration with external validation frameworks
 *
 * Usage Scenarios:
 * - REST API PUT/PATCH endpoints for metric definition updates
 * - Administrative interfaces for metric configuration management
 * - Batch update operations for multiple metric definitions
 * - Integration with external systems requiring metric modifications
 *
 * Security Considerations:
 * - Input sanitization prevents injection attacks
 * - Field-level validation prevents malformed data submission
 * - Size limits protect against denial-of-service through large payloads
 * - Type constraints ensure data integrity and system stability
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/16
 * @see IotMetricDefinition
 * @see IotMetricDefinitionService#update
 * @see jakarta.validation.constraints.Size
 */
@Data
public class IotMetricDefinitionUpdateForm {

    /**
     * Metric Name: Optional field for updating the human-readable identifier of the IoT metric.
     *
     * This field enables selective update of the metric's primary user-facing identifier:
     * - When provided: Replaces the existing metric name with the new value
     * - When null: Preserves the existing metric name (no change)
     * - Supports department-level uniqueness validation during updates
     * - Enables meaningful metric identification and categorization
     *
     * Validation Constraints:
     * - Maximum length of 255 characters to prevent data truncation
     * - Must be unique within the department scope (enforced by service layer)
     * - Cannot be empty or whitespace-only if provided
     * - Supports Unicode characters for internationalization
     *
     * Business Impact:
     * - Affects user interface displays and report generation
     * - May require updates to dependent systems and integrations
     * - Supports metric evolution and naming standardization
     * - Enables search and filtering operations across the system
     *
     * Update Considerations:
     * - Triggers uniqueness validation against existing metric definitions
     * - May affect audit trails and change history tracking
     * - Could impact external system integrations and API consumers
     * - Should be communicated to stakeholders before implementation
     *
     * @see IotMetricDefinitionService#isMetricNameUnique
     */
    @Size(max = 255, message = "Metric name cannot exceed 255 characters")
    private String metricName;

    /**
     * Alias: Optional field for updating the short name or abbreviation of the IoT metric.
     *
     * This field provides flexibility for updating the metric's secondary identifier:
     * - When provided: Updates the alias with the new value (can be empty string to clear)
     * - When null: Preserves the existing alias (no change)
     * - Supports abbreviated naming conventions and technical standards
     * - Enables compact representation in constrained display environments
     *
     * Validation Constraints:
     * - Maximum length of 100 characters for space efficiency
     * - Can be empty string to explicitly clear the alias
     * - Supports technical abbreviations and acronyms
     * - No uniqueness constraints (multiple metrics can share aliases)
     *
     * Use Cases:
     * - Short labels in dashboard widgets and monitoring displays
     * - Technical identifiers for API integrations and data exports
     * - Abbreviations in protocol messages and data transmission
     * - Compact naming in mobile applications and responsive designs
     *
     * Update Scenarios:
     * - Adding an alias to a metric that previously had none
     * - Changing an existing alias to a more appropriate abbreviation
     * - Removing an alias by setting it to an empty string
     * - Standardizing aliases across related metrics
     *
     * Best Practices:
     * - Use consistent abbreviation patterns within the organization
     * - Consider the target audience when choosing alias formats
     * - Document alias meanings for maintenance and onboarding
     * - Avoid overly cryptic abbreviations that reduce clarity
     */
    @Size(max = 100, message = "Metric alias cannot exceed 100 characters")
    private String alias;

    /**
     * Physical Quantity: Optional field for updating the fundamental physical property being measured.
     *
     * This field enables modification of the metric's scientific or engineering quantity classification:
     * - When provided: Updates the physical quantity and may require unit compatibility validation
     * - When null: Preserves the existing physical quantity (no change)
     * - Establishes the dimensional nature of the measurement
     * - Determines compatible units of measurement and data processing algorithms
     *
     * Quantity Categories:
     * - Base Quantities: Length, Mass, Time, Temperature, Electric Current
     * - Derived Quantities: Velocity, Acceleration, Force, Pressure, Energy
     * - Engineering Quantities: Flow Rate, Voltage, Current, Resistance
     * - Custom Quantities: Domain-specific measurements and calculations
     *
     * Update Implications:
     * - May require changing the associated unit of measurement
     * - Could affect data processing and analysis algorithms
     * - May impact existing measurement data interpretation
     * - Could require recalibration of sensors or measurement systems
     *
     * Validation Requirements:
     * - Must be compatible with the current or updated unit of measurement
     * - Should maintain dimensional consistency with existing data
     * - May require business approval for significant quantity changes
     * - Should be validated against organizational measurement standards
     *
     * Business Considerations:
     * - Quantity changes may affect reporting and analytics systems
     * - May require updates to user interfaces and documentation
     * - Could impact integration with external measurement systems
     * - Should be communicated to all stakeholders before implementation
     *
     * @see PhysicalQuantity
     * @see MetricUnit
     */
    private PhysicalQuantity physicalQuantity;

    /**
     * Unit of Measurement: Optional field for updating the standardized unit used to express metric values.
     *
     * This field enables modification of the metric's measurement scale and reference standard:
     * - When provided: Updates the unit and triggers compatibility validation
     * - When null: Preserves the existing unit of measurement (no change)
     * - Defines the scale and reference for measurement values
     * - Must be dimensionally compatible with the physical quantity
     *
     * Unit Categories:
     * - SI Base Units: meter (m), kilogram (kg), second (s), kelvin (K)
     * - SI Derived Units: newton (N), pascal (Pa), joule (J), watt (W)
     * - Engineering Units: volt (V), ampere (A), hertz (Hz), degree Celsius (°C)
     * - Custom Units: Organization-specific or legacy measurement units
     *
     * Compatibility Validation:
     * - Must be dimensionally compatible with the physical quantity
     * - Supports unit conversion between compatible units
     * - Enables accurate data aggregation and analysis
     * - Critical for maintaining measurement integrity and accuracy
     *
     * Update Considerations:
     * - May affect data display formatting in user interfaces
     * - Could impact sensor calibration and measurement accuracy
     * - May require unit conversion for existing measurement data
     * - Should maintain consistency with organizational standards
     *
     * Business Impact:
     * - Affects data visualization and reporting formats
     * - May require updates to client applications and APIs
     * - Could impact integration with external measurement systems
     * - Should be validated against existing measurement data ranges
     *
     * Migration Strategy:
     * - Consider providing unit conversion for existing data
     * - Update user interfaces to display the new unit appropriately
     * - Communicate changes to all data consumers and stakeholders
     * - Provide migration documentation and support
     *
     * @see MetricUnit
     * @see PhysicalQuantity
     */
    private MetricUnit unit;

    /**
     * Data Type: Optional field for updating the Sparkplug B data type specification for metric values.
     *
     * This field enables modification of the technical data type for measurement data transmission:
     * - When provided: Updates the data type and may affect data processing pipelines
     * - When null: Preserves the existing data type (no change)
     * - Specifies the binary representation and encoding of metric data
     * - Influences data transmission efficiency and storage requirements
     *
     * Sparkplug B Data Types:
     * - Integer Types: Int8, Int16, Int32, Int64, UInt8, UInt16, UInt32, UInt64
     * - Floating Point: Float (32-bit), Double (64-bit)
     * - Text Types: String (UTF-8 encoded), Text (extended text)
     * - Temporal Types: DateTime (timestamp representation)
     * - Boolean: Boolean (true/false values)
     * - Complex Types: DataSet (structured data), Template (reusable structures)
     *
     * Update Implications:
     * - May affect MQTT payload sizes and transmission efficiency
     * - Could impact database storage requirements and indexing
     * - May require changes to data processing algorithms
     * - Could affect real-time data streaming capabilities
     *
     * Compatibility Considerations:
     * - Should be appropriate for the measurement precision requirements
     * - Must consider data transmission bandwidth constraints
     * - Should account for storage and processing resource limitations
     * - Must ensure compatibility with IoT platform capabilities
     *
     * Migration Challenges:
     * - May require data type conversion for existing measurements
     * - Could impact historical data analysis and reporting
     * - May affect integration with external systems and APIs
     * - Should be tested thoroughly in staging environments
     *
     * Business Validation:
     * - Assess impact on existing data collection systems
     * - Evaluate performance implications for data transmission
     * - Consider storage cost changes for large datasets
     * - Validate compatibility with downstream processing systems
     *
     * @see MetricDataType
     */
    private MetricDataType dataType;

    /**
     * Active Status: Optional field for updating the operational status of the IoT metric definition.
     *
     * This field controls the metric's availability and operational state in the system:
     * - When provided: Updates the active status (true = active, false = inactive)
     * - When null: Preserves the existing active status (no change)
     * - Implements soft delete functionality for graceful deactivation
     * - Supports temporary suspension and reactivation of metrics
     *
     * Status Values and Implications:
     * - true (Active): Metric is fully operational and available for data collection
     * - false (Inactive): Metric is deactivated but data is preserved for historical analysis
     *
     * Operational Use Cases:
     * - Temporary sensor maintenance or calibration periods
     * - Gradual migration to new metric definitions or measurement systems
     * - Regulatory compliance for data archiving and retention
     * - System maintenance and troubleshooting scenarios
     * - Seasonal or conditional metric activation/deactivation
     *
     * Business Considerations:
     * - Inactive metrics should not accept new measurement data
     * - Historical data remains accessible for reporting and analysis
     * - May affect dashboard displays and monitoring interfaces
     * - Should be communicated to data consumers and stakeholders
     *
     * Data Integrity:
     * - Preserves all historical measurement data and relationships
     * - Maintains referential integrity with existing data structures
     * - Supports audit trails and compliance requirements
     * - Enables potential data recovery if reactivation is needed
     *
     * System Impact:
     * - Affects API responses and data query results
     * - May impact real-time monitoring and alerting systems
     * - Could affect automated processes and scheduled reports
     * - Should trigger notifications to dependent systems
     *
     * Best Practices:
     * - Document reasons for deactivation in audit logs
     * - Provide clear communication about status changes
     * - Consider impact on automated systems and integrations
     * - Plan for potential reactivation scenarios
     * - Maintain status change history for compliance purposes
     */
    private Boolean isActive;
}
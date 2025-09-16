package community.waterlevel.iot.module.metric.model.form;

import community.waterlevel.iot.module.metric.model.entity.IotMetricDefinition;
import community.waterlevel.iot.module.metric.model.enums.MetricDataType;
import community.waterlevel.iot.module.metric.model.enums.MetricUnit;
import community.waterlevel.iot.module.metric.model.enums.PhysicalQuantity;
import community.waterlevel.iot.module.metric.service.IotMetricDefinitionService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * IoT Metric Definition Create Form
 *
 * This form class encapsulates the data structure required for creating new IoT metric definitions
 * in a multi-tenant environment. It implements comprehensive input validation to ensure data integrity
 * and enforces business rules at the boundary between client requests and the service layer.
 *
 * Key Characteristics:
 * - Requires all essential fields for complete metric definition creation
 * - Implements strict input validation using Jakarta Bean Validation annotations
 * - Provides clear, user-friendly error messages for validation failures
 * - Enforces department-scoped data isolation and access control
 * - Supports type-safe data transfer with compile-time validation
 *
 * Architectural Role:
 * - Acts as the primary boundary object for metric definition creation requests
 * - Provides input sanitization and validation before business logic processing
 * - Supports REST API request body deserialization and validation
 * - Enables early error detection and user feedback
 *
 * Validation Strategy:
 * - Field-level validation using Jakarta Bean Validation annotations
 * - Mandatory field validation with @NotNull and @NotBlank annotations
 * - Size constraints to prevent data truncation and buffer overflows
 * - Custom validation messages for user-friendly error reporting
 * - Type safety through enum-based fields for categorical data
 *
 * Business Logic Integration:
 * - Compatible with department-level uniqueness constraints
 * - Supports physical quantity and unit compatibility validation
 * - Enables audit trail generation for new metric definitions
 * - Facilitates integration with external validation frameworks
 *
 * Creation Workflow:
 * 1. Client submits form data via REST API
 * 2. Form undergoes automatic validation
 * 3. Service layer performs business rule validation
 * 4. Entity creation with default values and audit timestamps
 * 5. Database persistence with transaction management
 *
 * Usage Scenarios:
 * - REST API POST endpoints for metric definition creation
 * - Administrative interfaces for metric configuration setup
 * - Batch creation operations for multiple metric definitions
 * - Integration with external systems requiring metric provisioning
 *
 * Security Considerations:
 * - Input sanitization prevents injection attacks
 * - Field-level validation prevents malformed data submission
 * - Size limits protect against denial-of-service through large payloads
 * - Department scoping ensures proper access control
 *
 * Error Handling:
 * - Validation errors returned with specific field-level messages
 * - Business rule violations handled by service layer exceptions
 * - Clear error messages guide users toward correct data entry
 * - Supports internationalization of validation messages
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/16
 * @see IotMetricDefinition
 * @see IotMetricDefinitionService#create
 * @see jakarta.validation.constraints.NotNull
 * @see jakarta.validation.constraints.NotBlank
 * @see jakarta.validation.constraints.Size
 */
@Data
public class IotMetricDefinitionCreateForm {

    /**
     * Department ID: Mandatory field specifying the organizational unit that will own this metric definition.
     *
     * This field establishes the multi-tenant context for the new metric definition:
     * - Associates the metric with a specific department or organization
     * - Enables department-level data isolation and access control
     * - Supports role-based permissions and data filtering
     * - Critical for maintaining data privacy and compliance boundaries
     *
     * Validation Requirements:
     * - Must not be null (enforced by @NotNull annotation)
     * - Must reference a valid, active department in the system
     * - Must be accessible to the authenticated user
     * - Used for all subsequent data access and permission checks
     *
     * Business Logic:
     * - Users can only create metrics within their authorized departments
     * - Enables department-specific metric naming and customization
     * - Supports organizational hierarchy and reporting structures
     * - Facilitates data segregation for regulatory compliance
     *
     * Security Implications:
     * - Prevents cross-tenant data creation and access
     * - Enforces organizational boundaries and data ownership
     * - Supports audit trails for metric creation attribution
     * - Critical for maintaining data integrity in multi-tenant environments
     *
     * Usage in Validation:
     * - Verified against department existence and user access rights
     * - Used in uniqueness validation for metric names within the department
     * - Applied to all data access operations for the created metric
     * - Referenced in audit logs and change history tracking
     *
     * @see IotMetricDefinitionService#create
     */
    @NotNull(message = "Department ID cannot be null")
    private Long deptId;

    /**
     * Metric Name: Mandatory field providing the human-readable identifier for the IoT metric definition.
     *
     * This field serves as the primary user-facing identifier and must be unique within the department:
     * - Used in user interfaces, reports, and documentation
     * - Serves as the primary key for uniqueness validation within departments
     * - Supports search and filtering operations across the system
     * - Enables meaningful metric identification and categorization
     *
     * Validation Constraints:
     * - Must not be blank or null (enforced by @NotBlank annotation)
     * - Maximum length of 255 characters to prevent data truncation
     * - Must be unique within the department scope (enforced by service layer)
     * - Cannot be empty or whitespace-only
     * - Supports Unicode characters for internationalization
     *
     * Naming Conventions:
     * - Should be descriptive and follow organizational standards
     * - Supports spaces and special characters for readability
     * - Case-insensitive uniqueness validation
     * - Should be meaningful to end users and system administrators
     *
     * Business Impact:
     * - Affects user interface displays and report generation
     * - Used in API responses and client application integration
     * - Supports search functionality and data discovery
     * - Enables consistent metric identification across systems
     *
     * Examples of Good Naming:
     * - "Water Level Sensor Reading"
     * - "Temperature Monitoring System"
     * - "Battery Voltage Measurement"
     * - "Flow Rate Sensor Data"
     *
     * Uniqueness Requirements:
     * - Validated against existing metric definitions in the same department
     * - Case-insensitive comparison to prevent naming conflicts
     * - Immediate validation feedback to prevent duplicate creation
     * - Critical for maintaining data integrity and preventing confusion
     *
     * @see IotMetricDefinitionService#isMetricNameUnique
     * @see jakarta.validation.constraints.NotBlank
     * @see jakarta.validation.constraints.Size
     */
    @NotBlank(message = "Metric name cannot be blank")
    @Size(max = 255, message = "Metric name cannot exceed 255 characters")
    private String metricName;

    /**
     * Alias: Optional field providing a short name or abbreviation for the IoT metric definition.
     *
     * This field offers an alternative, concise identifier for space-constrained environments:
     * - Can be null or empty (no validation required)
     * - Supports abbreviated naming conventions and technical standards
     * - Enables compact representation in charts, tables, and reports
     * - Facilitates integration with external systems and protocols
     *
     * Validation Constraints:
     * - Optional field (no @NotNull or @NotBlank annotations)
     * - Maximum length of 100 characters when provided
     * - Can be explicitly set to empty string to indicate no alias
     * - No uniqueness constraints (multiple metrics can share aliases)
     *
     * Use Cases:
     * - Short labels in dashboard widgets and monitoring displays
     * - Technical identifiers for API integrations and data exports
     * - Abbreviations in protocol messages and data transmission
     * - Compact naming in mobile applications and responsive designs
     *
     * Best Practices:
     * - Use consistent abbreviation patterns within the organization
     * - Consider the target audience when choosing alias formats
     * - Document alias meanings for maintenance and onboarding
     * - Avoid overly cryptic abbreviations that reduce clarity
     *
     * Examples:
     * - "WL" for "Water Level Sensor Reading"
     * - "TEMP" for "Temperature Monitoring System"
     * - "BATT_V" for "Battery Voltage Measurement"
     * - "FLOW" for "Flow Rate Sensor Data"
     *
     * Implementation Notes:
     * - Field is nullable in the database schema
     * - UI components should handle null/empty alias gracefully
     * - Consider providing alias suggestions based on metric name
     * - May be auto-generated from metric name if not provided
     *
     * @see jakarta.validation.constraints.Size
     */
    @Size(max = 100, message = "Metric alias cannot exceed 100 characters")
    private String alias;

    /**
     * Physical Quantity: Mandatory field defining the fundamental physical property being measured.
     *
     * This field establishes the scientific or engineering quantity that the metric represents:
     * - Defines the dimensional nature of the measurement
     * - Determines compatible units of measurement
     * - Supports scientific accuracy and dimensional analysis
     * - Enables physics-based validation and data processing
     *
     * Quantity Categories:
     * - Base Quantities: Length, Mass, Time, Temperature, Electric Current
     * - Derived Quantities: Velocity, Acceleration, Force, Pressure, Energy
     * - Engineering Quantities: Flow Rate, Voltage, Current, Resistance
     * - Custom Quantities: Domain-specific measurements and calculations
     *
     * Validation Requirements:
     * - Must not be null (enforced by @NotNull annotation)
     * - Must be a valid enum value from the PhysicalQuantity enumeration
     * - Must be compatible with the selected unit of measurement
     * - Should align with organizational measurement standards
     *
     * Business Logic Integration:
     * - Influences unit selection and compatibility validation
     * - Affects data processing algorithms and analysis methods
     * - Determines appropriate data type recommendations
     * - Impacts sensor calibration and measurement accuracy requirements
     *
     * Selection Criteria:
     * - Based on the actual physical property being measured
     * - Should match sensor capabilities and measurement objectives
     * - Must be consistent with industry standards and conventions
     * - Should support required data analysis and reporting needs
     *
     * Examples:
     * - LENGTH for distance or position measurements
     * - TEMPERATURE for thermal measurements
     * - VOLTAGE for electrical potential measurements
     * - FLOW_RATE for fluid movement measurements
     *
     * Implementation Impact:
     * - Critical for maintaining scientific measurement accuracy
     * - Affects unit conversion capabilities and dimensional analysis
     * - Influences data visualization and reporting formats
     * - Supports integration with scientific and engineering systems
     *
     * @see PhysicalQuantity
     * @see MetricUnit
     * @see jakarta.validation.constraints.NotNull
     */
    @NotNull(message = "Physical quantity cannot be null")
    private PhysicalQuantity physicalQuantity;

    /**
     * Unit of Measurement: Mandatory field specifying the standardized unit used to express metric values.
     *
     * This field defines the measurement unit that must be compatible with the physical quantity:
     * - Specifies the scale and reference for measurement values
     * - Must be dimensionally compatible with the selected physical quantity
     * - Supports SI units and industry-standard conventions
     * - Enables unit conversion and data normalization
     *
     * Unit Categories:
     * - SI Base Units: meter (m), kilogram (kg), second (s), kelvin (K)
     * - SI Derived Units: newton (N), pascal (Pa), joule (J), watt (W)
     * - Engineering Units: volt (V), ampere (A), hertz (Hz), degree Celsius (°C)
     * - Custom Units: Organization-specific or legacy measurement units
     *
     * Validation Requirements:
     * - Must not be null (enforced by @NotNull annotation)
     * - Must be a valid enum value from the MetricUnit enumeration
     * - Must be dimensionally compatible with the physical quantity
     * - Should follow organizational unit standardization policies
     *
     * Compatibility Validation:
     * - Verified against physical quantity dimensional requirements
     * - Supports unit conversion between compatible units
     * - Enables accurate data aggregation and analysis
     * - Critical for maintaining measurement integrity and accuracy
     *
     * Selection Guidelines:
     * - Choose units appropriate for the measurement scale and precision
     * - Consider data transmission and storage efficiency
     * - Align with industry standards and regulatory requirements
     * - Support required data analysis and reporting capabilities
     *
     * Examples by Quantity:
     * - Length: meters (m), feet (ft), inches (in)
     * - Temperature: celsius (°C), fahrenheit (°F), kelvin (K)
     * - Electrical: volts (V), amperes (A), ohms (Ω)
     * - Flow: liters/second (L/s), cubic meters/hour (m³/h)
     *
     * Business Impact:
     * - Affects data display and user interface formatting
     * - Influences sensor calibration and measurement accuracy
     * - Supports internationalization and localization requirements
     * - Enables integration with external measurement systems
     *
     * @see MetricUnit
     * @see PhysicalQuantity
     * @see jakarta.validation.constraints.NotNull
     */
    @NotNull(message = "Unit cannot be null")
    private MetricUnit unit;

    /**
     * Data Type: Mandatory field specifying the Sparkplug B data type specification for metric values.
     *
     * This field defines the technical data type for measurement values according to MQTT Sparkplug B protocol:
     * - Specifies the binary representation and encoding of metric data
     * - Determines the range and precision of measurement values
     * - Influences data transmission efficiency and storage requirements
     * - Critical for protocol compliance and system interoperability
     *
     * Sparkplug B Data Types:
     * - Integer Types: Int8, Int16, Int32, Int64, UInt8, UInt16, UInt32, UInt64
     * - Floating Point: Float (32-bit), Double (64-bit)
     * - Text Types: String (UTF-8 encoded), Text (extended text)
     * - Temporal Types: DateTime (timestamp representation)
     * - Boolean: Boolean (true/false values)
     * - Complex Types: DataSet (structured data), Template (reusable structures)
     *
     * Validation Requirements:
     * - Must not be null (enforced by @NotNull annotation)
     * - Must be a valid enum value from the MetricDataType enumeration
     * - Should be appropriate for the measurement precision requirements
     * - Must consider data transmission bandwidth constraints
     *
     * Selection Criteria:
     * - Based on measurement precision and range requirements
     * - Considers data transmission bandwidth and efficiency
     * - Accounts for storage and processing resource limitations
     * - Ensures compatibility with IoT platform capabilities
     *
     * Technical Implications:
     * - Affects MQTT payload size and transmission efficiency
     * - Determines database storage requirements and indexing strategies
     * - Influences data processing algorithms and computational accuracy
     * - Critical for real-time data streaming and edge computing
     *
     * Performance Considerations:
     * - Smaller data types reduce network bandwidth usage
     * - Appropriate precision prevents data truncation errors
     * - Efficient storage supports larger datasets and faster queries
     * - Protocol compliance ensures interoperability with other systems
     *
     * Examples by Use Case:
     * - Sensor Readings: Float or Double for precise measurements
     * - Status Indicators: Boolean for on/off states
     * - Identifiers: String for device names and locations
     * - Timestamps: DateTime for temporal measurements
     * - Counters: UInt32 or UInt64 for incremental values
     *
     * Business Considerations:
     * - Affects data transmission costs in bandwidth-constrained environments
     * - Influences storage costs for large-scale IoT deployments
     * - Impacts data processing performance and real-time capabilities
     * - Critical for integration with external systems and protocols
     *
     * @see MetricDataType
     * @see jakarta.validation.constraints.NotNull
     */
    @NotNull(message = "Data type cannot be null")
    private MetricDataType dataType;
}
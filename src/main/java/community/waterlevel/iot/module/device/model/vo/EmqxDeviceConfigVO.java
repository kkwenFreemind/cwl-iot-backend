package community.waterlevel.iot.module.device.model.vo;

import lombok.Data;

import java.util.UUID;

/**
 * View Object (VO) for EMQX device configuration data presentation in API responses.
 *
 * <p>This class serves as the data transfer object for presenting EMQX-specific
 * device configuration information to API clients. It provides the MQTT credentials
 * and topic information needed for device connectivity and communication.
 *
 * <p>Key characteristics:
 * <ul>
 *   <li>EMQX-specific configuration data for device connectivity</li>
 *   <li>MQTT authentication credentials (username/password)</li>
 *   <li>MQTT client identifier for unique device identification</li>
 *   <li>Telemetry and command topic definitions</li>
 *   <li>Direct mapping from {@link community.waterlevel.iot.module.device.model.entity.IotDeviceJpa} entity</li>
 * </ul>
 *
 * <p>Usage scenarios:
 * <ul>
 *   <li>Device provisioning APIs for MQTT connectivity setup</li>
 *   <li>Device configuration retrieval for frontend applications</li>
 *   <li>MQTT client initialization with proper credentials</li>
 *   <li>Device management interfaces requiring EMQX settings</li>
 * </ul>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/19
 * @see community.waterlevel.iot.module.device.model.entity.IotDeviceJpa
 */
@Data
public class EmqxDeviceConfigVO {

    /**
     * Unique identifier for the IoT device.
     *
     * <p>This UUID serves as the primary key and unique identifier for
     * the device across the entire system. It is used for device-specific
     * operations and configuration retrieval.
     *
     * <p>Format: RFC 4122 UUID
     * <p>Usage: Device identification and configuration association
     */
    private UUID deviceId;

    /**
     * EMQX MQTT broker authentication username.
     *
     * <p>The username credential used by this device to authenticate
     * with the EMQX MQTT broker. This credential is generated during
     * device provisioning and stored securely.
     *
     * <p>Format: Generated username following device-specific pattern
     * <p>Example: "device_123_550e8400-e29b-41d4-a716-446655440000"
     * <p>Usage: MQTT client authentication with EMQX broker
     */
    private String emqxUsername;

    /**
     * EMQX MQTT broker authentication password.
     *
     * <p>The password credential used by this device to authenticate
     * with the EMQX MQTT broker. This credential is generated during
     * device provisioning and should be handled securely.
     *
     * <p>Format: Randomly generated 16-character string
     * <p>Security: Should be transmitted over secure channels only
     * <p>Usage: MQTT client authentication with EMQX broker
     */
    private String emqxPassword;

    /**
     * Unique MQTT client identifier for device connection.
     *
     * <p>The unique client identifier used by this device when connecting
     * to the MQTT broker. This ID must be unique across all devices and
     * is used by the MQTT broker to identify and manage the device connection.
     *
     * <p>Format: Generated client ID following device-specific pattern
     * <p>Example: "client_550e8400-e29b-41d4-a716-446655440000"
     * <p>Usage: MQTT client identification and connection management
     */
    private String mqttClientId;

    /**
     * MQTT topic for publishing device telemetry data.
     *
     * <p>The MQTT topic where this device publishes its telemetry data,
     * including sensor readings, status information, and operational data.
     * The topic follows a hierarchical structure for multi-tenant support.
     *
     * <p>Format: Hierarchical topic structure with tenant and device identifiers
     * <p>Example: "tenants/123/devices/550e8400-e29b-41d4-a716-446655440000/telemetry"
     * <p>Usage: Device data publishing and telemetry collection
     */
    private String telemetryTopic;

    /**
     * MQTT topic for receiving device control commands.
     *
     * <p>The MQTT topic where this device listens for control commands
     * from the backend system. Commands may include configuration updates,
     * calibration requests, operational instructions, or maintenance commands.
     *
     * <p>Format: Hierarchical topic structure with tenant and device identifiers
     * <p>Example: "tenants/123/devices/550e8400-e29b-41d4-a716-446655440000/commands"
     * <p>Usage: Device control and command reception
     */
    private String commandTopic;
}
package community.waterlevel.iot.module.device.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.UUID;

/**
 * IoT Device form object for device registration and management operations.
 * Handles device creation and updates with validation support for device
 * information, spatial coordinates, and operational status configuration.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/14
 */
@Schema(description = "IoT Device Form")
@Data
public class IotDeviceForm {

    @Schema(description = "Device ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID deviceId;

    @Schema(description = "Device Name", example = "Water Level Sensor #1")
    @NotBlank(message = "Device name cannot be empty")
    private String deviceName;

    @Schema(description = "Department ID", example = "2")
    @NotNull(message = "Department ID cannot be null")
    private Long deptId;

    @Schema(description = "Device Model", example = "ESP32-WL")
    private String deviceModel;

    @Schema(description = "Latitude coordinate (WGS84)", example = "25.0330")
    @Range(min = -90, max = 90, message = "Latitude must be between -90 and 90 degrees")
    private Double latitude;

    @Schema(description = "Longitude coordinate (WGS84)", example = "121.5654")
    @Range(min = -180, max = 180, message = "Longitude must be between -180 and 180 degrees")
    private Double longitude;

    @Schema(description = "Location Description", example = "Roof of Building A, 3rd Floor")
    private String location;

    @Schema(description = "Device Status", example = "active", allowableValues = {"active", "inactive", "disabled"})
    @Pattern(regexp = "^(active|inactive|disabled)$", message = "Status must be one of: active, inactive, disabled")
    private String status = "inactive";

}
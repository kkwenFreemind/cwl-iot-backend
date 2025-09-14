package community.waterlevel.iot.module.device.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * View object representing an IoT device record for API responses.
 * Encapsulates device metadata, spatial location, operational status,
 * and department association for device management features.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/14
 */
@Schema(description = "IoT Device VO")
@Data
public class IotDeviceVO {

    @Schema(description = "Device ID (UUID)")
    private UUID deviceId;

    @Schema(description = "Device Name")
    private String deviceName;

    @Schema(description = "Department ID")
    private Long deptId;

    @Schema(description = "Department Name")
    private String deptName;

    @Schema(description = "Device Model")
    private String deviceModel;

    @Schema(description = "Latitude coordinate")
    private Double latitude;

    @Schema(description = "Longitude coordinate")
    private Double longitude;

    @Schema(description = "Location Description")
    private String location;

    @Schema(description = "Device Status", allowableValues = {"active", "inactive", "disabled"})
    private String status;

    @Schema(description = "Created By User ID")
    private Long createdBy;

    @Schema(description = "Creator Username")
    private String createdByUsername;

    @Schema(description = "Creation Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Last Communication Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSeen;

    @Schema(description = "Last Update Time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "Online Status (derived from last_seen)")
    private Boolean isOnline;

    @Schema(description = "Time since last seen (in minutes)")
    private Long minutesSinceLastSeen;

}
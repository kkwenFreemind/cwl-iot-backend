package community.waterlevel.iot.module.device.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Query object for searching and filtering IoT devices.
 * Supports keyword search on device name and optional filtering by status and department.
 */
@Schema(description = "IoT device query object")
@Data
public class IotDeviceQuery {

    @Schema(description = "Keyword (device name)")
    private String keywords;

    @Schema(description = "Status (active/inactive/disabled)")
    private String status;

    @Schema(description = "Department ID to filter devices")
    private Long deptId;
}

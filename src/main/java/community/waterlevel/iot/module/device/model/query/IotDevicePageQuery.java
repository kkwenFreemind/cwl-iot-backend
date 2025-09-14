package community.waterlevel.iot.module.device.model.query;

import cn.hutool.db.sql.Direction;
import community.waterlevel.iot.common.annotation.ValidField;
import community.waterlevel.iot.common.base.BasePageQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Page query object for searching and filtering IoT device records.
 * Supports keyword-based search on device name or model, filtering by status,
 * department, location proximity, and creation time range with sorting capabilities.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "IoT Device paging query object")
public class IotDevicePageQuery extends BasePageQuery {

    @Schema(description = "Keywords (device name/model/location)")
    private String keywords;

    @Schema(description = "Device Status", allowableValues = {"active", "inactive", "disabled"})
    private String status;

    @Schema(description = "Department ID")
    private Long deptId;

    @Schema(description = "Department IDs for bulk filtering")
    private List<Long> deptIds;

    @Schema(description = "Device Model")
    private String deviceModel;

    @Schema(description = "Creation Time Range", example = "[\"2025-01-01\", \"2025-12-31\"]")
    private List<String> createTime;

    @Schema(description = "Last Seen Time Range for filtering online/offline devices")
    private List<String> lastSeenTime;

    @Schema(description = "Show only online devices (based on last_seen within threshold)")
    private Boolean onlineOnly;

    @Schema(description = "Center latitude for proximity search")
    private Double centerLat;

    @Schema(description = "Center longitude for proximity search") 
    private Double centerLng;

    @Schema(description = "Search radius in kilometers for proximity search")
    private Double radiusKm;

    @Schema(description = "Sort fields")
    @ValidField(allowedValues = {"created_at", "updated_at", "last_seen", "device_name", "status"})
    private String field;

    @Schema(description = "Sorting method (ASC/DESC)")
    private Direction direction;

    @JsonIgnore
    @Schema(hidden = true)
    private Boolean isRoot;

}
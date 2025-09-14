package community.waterlevel.iot.module.device.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class IotDeviceVO {
    private UUID deviceId;
    private String deviceName;
    private Long deptId;
    private String deviceModel;
    private Double latitude;
    private Double longitude;
    private String location;
    private String status;
    private LocalDateTime lastSeen;
    private LocalDateTime createdAt;
}

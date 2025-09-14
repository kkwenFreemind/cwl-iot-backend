package community.waterlevel.iot.module.device.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class IotDeviceForm {

    private UUID deviceId;

    @NotBlank
    private String deviceName;

    @NotNull
    private Long deptId;

    private String deviceModel;

    private Double latitude;

    private Double longitude;

    private String location;
}

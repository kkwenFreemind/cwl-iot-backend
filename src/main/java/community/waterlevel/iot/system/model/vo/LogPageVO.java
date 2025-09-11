package community.waterlevel.iot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import community.waterlevel.iot.common.enums.LogModuleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * View object representing a paginated system log record for API responses.
 * <p>
 * Encapsulates log ID, module, content, request details, IP, region, browser, OS, execution time, operator, and timestamps.
 * Used to transfer paginated log data from the backend to the client in log management features.
 * </p>
 *
 * @author Ray
 * @since 2.10.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
@Schema(description = "LogPage VO")
public class LogPageVO implements Serializable {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "module")
    private LogModuleEnum module;

    @Schema(description = "content")
    private String content;

    @Schema(description = "requestUri")
    private String requestUri;

    @Schema(description = "method")
    private String method;

    @Schema(description = "IP Address")
    private String ip;

    @Schema(description = "region")
    private String region;

    @Schema(description = "browser")
    private String browser;

    @Schema(description = "os")
    private String os;

    @Schema(description = "executionTime(millisecond)")
    private Long executionTime;

    @Schema(description = "createBy")
    private Long createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "operator")
    private String operator;
}
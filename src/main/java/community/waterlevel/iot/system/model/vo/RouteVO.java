package community.waterlevel.iot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * View object representing menu route information for API responses.
 * <p>
 * Encapsulates route path, component, redirect, name, meta attributes, and
 * child routes. Used to transfer
 * menu routing data from the backend to the client for dynamic navigation and
 * menu rendering.
 * </p>
 *
 * @author haoxr
 * @since 2020/11/28
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "Route VO")
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouteVO {

    @Schema(description = "path", example = "user")
    private String path;

    @Schema(description = "component", example = "system/user/index")
    private String component;

    @Schema(description = "redirect")
    private String redirect;

    @Schema(description = "name")
    private String name;

    @Schema(description = "meta")
    private Meta meta;

    @Schema(description = "路由属性类型")
    @Data
    public static class Meta {

        @Schema(description = "title")
        private String title;

        @Schema(description = "ICON")
        private String icon;

        @Schema(description = "Is it hidden (true-yes, false-no)", example = "true")
        private Boolean hidden;

        @Schema(description = "[Menu] Whether to enable page cache", example = "true")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean keepAlive;

        @Schema(description = "[Directory] Is it always displayed if only one sub-route is present?", example = "true")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean alwaysShow;

        @Schema(description = "Route parameters")
        private Map<String, String> params;
    }

    @Schema(description = "Sub-route list")
    private List<RouteVO> children;
}

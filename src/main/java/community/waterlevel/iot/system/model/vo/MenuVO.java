package community.waterlevel.iot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * View object representing menu data for API responses.
 * <p>
 * Encapsulates menu ID, parent ID, name, type, route, component, sort order,
 * visibility, icon, redirect, permissions, and child menus.
 * Used to transfer menu data from the backend to the client in menu management
 * and navigation features.
 * </p>
 * 
 * @author youlaitech
 * @since 2024-08-27 10:31
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "Menu view object")
@Data
public class MenuVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "parentId")
    private Long parentId;

    @Schema(description = "name")
    private String name;

    @Schema(description = "type")
    private Integer type;

    @Schema(description = "routeName")
    private String routeName;

    @Schema(description = "routePath")
    private String routePath;

    @Schema(description = "component")
    private String component;

    @Schema(description = "Menu sorting (the smaller the number, the higher the ranking)")
    private Integer sort;

    @Schema(description = "Whether the menu is visible (1: show; 0: hide)")
    private Integer visible;

    @Schema(description = "ICON")
    private String icon;

    @Schema(description = "redirect")
    private String redirect;

    @Schema(description = "Button permission flag")
    private String perm;

    @Schema(description = "children")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private List<MenuVO> children;

}

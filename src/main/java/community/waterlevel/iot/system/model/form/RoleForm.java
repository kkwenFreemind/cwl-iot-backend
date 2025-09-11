package community.waterlevel.iot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

// import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

/**
 * Role form object for role-based access control management.
 * Handles role creation and updates with validation support for maintaining
 * user authorization roles and permission assignment in the security system.
 * 
 * @author youlaitech
 * @since 2024-08-27 10:31
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "Role Form")
@Data
public class RoleForm {

    @Schema(description="Role ID")
    private Long id;

    @Schema(description="Role name")
    @NotBlank(message = "Role name cannot be empty")
    private String name;

    @Schema(description="Role Code")
    @NotBlank(message = "Role code cannot be empty")
    private String code;

    @Schema(description="sort")
    private Integer sort;

    @Schema(description="Character status (1-normal; 0-disabled)")
    @Range(max = 1, min = 0, message = "Incorrect character status")
    private Integer status;

    @Schema(description="data Scope")
    private Integer dataScope;

}

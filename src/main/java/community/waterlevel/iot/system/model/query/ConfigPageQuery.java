package community.waterlevel.iot.system.model.query;

import community.waterlevel.iot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Page query object for searching and filtering system configuration records.
 * <p>
 * Supports keyword-based search on configuration name and value, and provides pagination features
 * via the base class. Used for paginated queries in system configuration management.
 * </p>
 *
 * @author Theo
 * @since 2024-7-29 11:38:00
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Getter
@Setter
@Schema(description = "Config Page Query")
public class ConfigPageQuery extends BasePageQuery {

    @Schema(description="Keywords (configuration item name/configuration item value)")
    private String keywords;
}

package community.waterlevel.iot.system.model.query;

import community.waterlevel.iot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
/**
 * Page query object for searching and filtering dictionary records.
 * <p>
 * Supports keyword-based search on dictionary name and provides pagination features
 * via the base class. Used for paginated queries in dictionary management.
 * </p>
 * 
 * @author youlaitech
 * @since 2024-08-27 10:31
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11 
 */
@Schema(description ="Dictionary page query object")
public class DictPageQuery extends BasePageQuery {

    @Schema(description="Keyword (dictionary name)")
    private String keywords;

}

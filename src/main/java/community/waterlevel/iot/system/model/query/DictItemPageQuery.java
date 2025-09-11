package community.waterlevel.iot.system.model.query;


import community.waterlevel.iot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
/**
 * Page query object for searching and filtering dictionary item records.
 * <p>
 * Supports keyword-based search on dictionary item value or name, and filtering by dictionary code.
 * Inherits pagination features from the base class. Used for paginated queries in dictionary item management.
 * </p>
 * 
 * @author youlaitech
 * @since 2024-08-27 10:31
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description ="Dictionary item page query object")
public class DictItemPageQuery extends BasePageQuery {

    @Schema(description="Keyword (dictionary item value/dictionary item name)")
    private String keywords;

    @Schema(description="Dictionary encoding")
    private String dictCode;

}

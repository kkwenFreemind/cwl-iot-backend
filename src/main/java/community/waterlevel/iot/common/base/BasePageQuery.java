package community.waterlevel.iot.common.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Basic pagination request object for API queries.
 * Encapsulates common pagination parameters such as page number and page size.
 * Intended to be extended or used by request DTOs that require paginated results.
 *
 * @author haoxr
 * @since 2021/2/28
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Data
@Schema
public class BasePageQuery implements Serializable {

    /**
     * Serialization version UID for Serializable interface.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Page number for pagination (starts from 1).
     * Default is 1.
     */
    @Schema(description = "page number", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int pageNum = 1;

    /**
     * Number of records per page for pagination.
     * Default is 10.
     */
    @Schema(description = "Number of records per page", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private int pageSize = 10;

}

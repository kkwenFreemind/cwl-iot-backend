package community.waterlevel.iot.module.metric.model.query;

import community.waterlevel.iot.module.metric.model.enums.MetricDataType;
import community.waterlevel.iot.module.metric.model.enums.MetricUnit;
import community.waterlevel.iot.module.metric.model.enums.PhysicalQuantity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Query object for filtering and searching IoT metric definitions in the system.
 *
 * <p>This class encapsulates the search criteria used for metric definition listing and
 * filtering operations. It provides a structured way to specify search parameters
 * that are applied to metric definition queries, supporting both simple and advanced filtering
 * based on metric attributes and organizational hierarchy.
 *
 * <p>The query object supports the following filtering capabilities:
 * <ul>
 *   <li>Text search across metric names using keywords</li>
 *   <li>Physical quantity-based filtering</li>
 *   <li>Unit-based filtering</li>
 *   <li>Data type-based filtering</li>
 *   <li>Department-based filtering for organizational access control</li>
 * </ul>
 *
 * <p>All fields are optional, allowing for flexible query construction. When
 * multiple criteria are provided, they are combined using AND logic in the
 * database query layer.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/16
 */
@Schema(description = "IoT metric definition query object")
@Data
public class IotMetricDefinitionQuery {

    /**
     * Keyword search parameter for metric name filtering.
     *
     * <p>This field enables text-based search across metric names using partial
     * matching. The search is case-insensitive and supports substring matching.
     */
    @Schema(description = "Keyword (metric name)")
    private String keywords;

    /**
     * Physical quantity filter.
     */
    @Schema(description = "Physical quantity")
    private PhysicalQuantity physicalQuantity;

    /**
     * Unit filter.
     */
    @Schema(description = "Unit")
    private MetricUnit unit;

    /**
     * Data type filter.
     */
    @Schema(description = "Data type")
    private MetricDataType dataType;

    /**
     * Department identifier for organizational filtering.
     */
    @Schema(description = "Department ID")
    private Long deptId;

    /**
     * Page number for pagination (0-based).
     */
    @Schema(description = "Page number")
    private Integer page = 0;

    /**
     * Page size for pagination.
     */
    @Schema(description = "Page size")
    private Integer size = 10;
}
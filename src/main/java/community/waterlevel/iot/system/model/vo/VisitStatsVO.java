package community.waterlevel.iot.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * View object representing visit statistics for API responses.
 * <p>
 * Encapsulates today's and total unique visitor (UV) and page view (PV) counts, as well as growth rates.
 * Used to transfer visit statistics data from the backend to the client in analytics features.
 * </p>
 *
 * <p>
 * This class is used to provide summary statistics for visits, including current day and total counts
 * for unique visitors and page views, as well as their respective growth rates, typically for dashboard
 * or analytics modules.
 * </p>
 *
 * @author Ray.Hao
 * @since 2024/7/2
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "VisitStats VO")
@Getter
@Setter
public class VisitStatsVO {

    /**
     * The number of unique visitors (UV) for today.
     */
    @Schema(description = "todayUvCount (UV)")
    private Integer todayUvCount;

    /**
     * The total number of unique visitors (UV).
     */
    @Schema(description = "totalUvCount (UV)")
    private Integer totalUvCount;

    /**
     * The growth rate of unique visitors (UV).
     */
    @Schema(description = "uvGrowthRate")
    private BigDecimal uvGrowthRate;

    /**
     * The number of page views (PV) for today.
     */
    @Schema(description = "todayPvCount (PV)")
    private Integer todayPvCount;

    /**
     * The total number of page views (PV).
     */
    @Schema(description = "totalPvCount (PV)")
    private Integer totalPvCount;

    /**
     * The growth rate of page views (PV).
     */
    @Schema(description = "pvGrowthRate")
    private BigDecimal pvGrowthRate;

}

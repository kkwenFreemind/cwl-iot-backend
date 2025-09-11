package community.waterlevel.iot.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * View object representing visit trend statistics for API responses.
 * <p>
 * Encapsulates lists of dates, page views (PV), and unique IP counts for trend analysis.
 * Used to transfer visit trend data from the backend to the client in analytics features.
 * </p>
 *
 * <p>
 * This class is used to provide time-series data for visit trends, including the dates,
 * page view counts, and unique IP counts, typically for dashboard or analytics modules.
 * </p>
 *
 * @author Ray.Hao
 * @since 2.3.0
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "VisitTrend VO")
@Getter
@Setter
public class VisitTrendVO {

    /**
     * The list of date strings representing the time axis for the trend data.
     */
    @Schema(description = "dates")
    private List<String> dates;

    /**
     * The list of page view (PV) counts corresponding to each date.
     */
    @Schema(description = "pvList(PV)")
    private List<Integer> pvList;

    /**
     * The list of unique IP counts corresponding to each date.
     */
    @Schema(description = "ipList")
    private List<Integer> ipList;

}

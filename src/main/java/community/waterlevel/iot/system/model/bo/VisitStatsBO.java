package community.waterlevel.iot.system.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * VisitStatsBO is a business object representing visit statistics for the
 * system.
 * <p>
 * This class encapsulates today's visit count, total visit count, and the page
 * view growth rate, used for analytics and reporting in the IoT backend.
 *
 * @author Ray.Hao
 * @since 2024/7/2
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Getter
@Setter
public class VisitStatsBO {

    private Integer todayCount;

    private Integer totalCount;

    private BigDecimal growthRate;

}

package community.waterlevel.iot.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import community.waterlevel.iot.common.result.PageResult;
import community.waterlevel.iot.common.result.Result;
import community.waterlevel.iot.system.model.query.LogPageQuery;
import community.waterlevel.iot.system.model.vo.LogPageVO;
import community.waterlevel.iot.system.model.vo.VisitStatsVO;
import community.waterlevel.iot.system.model.vo.VisitTrendVO;
import community.waterlevel.iot.system.service.LogJpaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import community.waterlevel.iot.system.service.SystemLogJpaService;
import java.time.LocalDate;

/**
 * LogController is a REST controller that provides endpoints for managing and
 * retrieving system logs and visit statistics.
 * <p>
 * It exposes APIs for paginated log retrieval, visit trend analysis, and visit
 * statistics, delegating business logic to the {@link LogJpaService} and returning
 * standardized API responses.
 * <p>
 * Designed for use in the IoT backend's administration interface to monitor and
 * analyze system activity and user visits.
 *
 * @author Ray.Hao
 * @since 2.10.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Tag(name = "10.Log Controller")
@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class LogJpaController {

    private final SystemLogJpaService systemLogService;

    /**
     * Retrieves a paginated list of system logs.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return PageResult containing log page data
     */
    @Operation(summary = "Retrieves a paginated list of system logs")
    @GetMapping("/page")
    public PageResult<LogPageVO> getLogPage(LogPageQuery queryParams) {
        Page<LogPageVO> result = systemLogService.getLogPage(queryParams);
        return PageResult.success(result);
    }

    /**
     * Retrieves visit trend statistics for a given date range.
     *
     * @param startDate the start date (yyyy-MM-dd)
     * @param endDate   the end date (yyyy-MM-dd)
     * @return Result containing visit trend data
     */
    @Operation(summary = "Retrieves visit trend statistics for a given date range")
    @GetMapping("/visit-trend")
    public Result<VisitTrendVO> getVisitTrend(
            @Parameter(description = "開始時間", example = "yyyy-MM-dd") @RequestParam String startDate,
            @Parameter(description = "結束時間", example = "yyyy-MM-dd") @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        VisitTrendVO data = systemLogService.getVisitTrend(start, end);
        return Result.success(data);
    }

    /**
     * Retrieves overall visit statistics.
     *
     * @return Result containing visit statistics data
     */
    @Operation(summary = "Retrieves overall visit statistics")
    @GetMapping("/visit-stats")
    public Result<VisitStatsVO> getVisitStats() {
        VisitStatsVO result = systemLogService.getVisitStats();
        return Result.success(result);
    }

}

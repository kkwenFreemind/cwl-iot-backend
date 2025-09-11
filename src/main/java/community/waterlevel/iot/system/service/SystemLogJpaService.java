package community.waterlevel.iot.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import community.waterlevel.iot.system.model.entity.LogJpa;
import community.waterlevel.iot.system.model.query.LogPageQuery;
import community.waterlevel.iot.system.model.vo.LogPageVO;
import community.waterlevel.iot.system.model.vo.VisitStatsVO;
import community.waterlevel.iot.system.model.vo.VisitTrendVO;

import java.time.LocalDate;

/**
 * Unified service interface for managing system log data with support for
 * multiple persistence strategies.
 * <p>
 * Provides methods for saving logs, paginated log queries, and retrieving visit
 * trend and statistics data.
 * Used in system log management features to support analytics, monitoring, and
 * auditing.
 * </p>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface SystemLogJpaService {

    /**
     * Saves a new system log record.
     *
     * @param logJpa the log entity to save
     */
    void saveLog(LogJpa logJpa);

    /**
     * Retrieves a paginated list of system log records based on query parameters.
     *
     * @param queryParams the parameters for pagination and filtering
     * @return a paginated result of system log view objects
     */
    Page<LogPageVO> getLogPage(LogPageQuery queryParams);

    /**
     * Retrieves visit trend statistics for a specified date range.
     *
     * @param startDate the start date of the trend analysis
     * @param endDate   the end date of the trend analysis
     * @return a view object containing visit trend data
     */
    VisitTrendVO getVisitTrend(LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves overall visit statistics.
     *
     * @return a view object containing visit statistics data
     */
    VisitStatsVO getVisitStats();
}

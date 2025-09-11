package community.waterlevel.iot.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import community.waterlevel.iot.system.model.entity.LogJpa;
import community.waterlevel.iot.system.model.query.LogPageQuery;
import community.waterlevel.iot.system.model.vo.LogPageVO;
import community.waterlevel.iot.system.model.vo.VisitStatsVO;
import community.waterlevel.iot.system.model.vo.VisitTrendVO;

import java.time.LocalDate;

/**
 * Service interface for managing system log data and operations.
 * <p>
 * Provides methods for paginated log queries, visit trend and statistics
 * retrieval, log record saving, and log lookup by ID.
 * Used in system log management features to support analytics, monitoring, and
 * auditing.
 * </p>
 *
 * @author Ray.Hao
 * @since 2.10.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface LogJpaService {


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
     * @param endDate the end date of the trend analysis
     * @return a view object containing visit trend data
     */
    VisitTrendVO getVisitTrend(LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves overall visit statistics.
     *
     * @return a view object containing visit statistics data
     */
    VisitStatsVO getVisitStats();

    /**
     * Saves a new system log record.
     *
     * @param logJpa the log entity to save
     * @return the saved log entity
     */
    LogJpa save(LogJpa logJpa);

    /**
     * Finds a system log record by its ID.
     *
     * @param id the ID of the log record
     * @return the log entity if found, or null if not found
     */
    LogJpa findById(Long id);

}

package community.waterlevel.iot.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import community.waterlevel.iot.common.enums.LogModuleEnum;
import community.waterlevel.iot.system.model.entity.LogJpa;
import community.waterlevel.iot.system.model.query.LogPageQuery;
import community.waterlevel.iot.system.model.vo.LogPageVO;
import community.waterlevel.iot.system.model.vo.VisitStatsVO;
import community.waterlevel.iot.system.model.vo.VisitTrendVO;
import community.waterlevel.iot.system.repository.LogJpaRepository;
import community.waterlevel.iot.system.repository.UserJpaRepository;
import community.waterlevel.iot.system.service.LogJpaService;
import community.waterlevel.iot.system.service.SystemLogJpaService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the system log service business logic.
 * <p>
 * Provides methods for managing system log entities, including CRUD operations,
 * pagination,
 * statistics, and entity conversion. Integrates with JPA repositories for
 * persistence and analytics.
 * </p>
 *
 * @author Ray.Hao
 * @since 2.10.0
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class LogJpaServiceImpl implements LogJpaService, SystemLogJpaService {

    private final LogJpaRepository logJpaRepository;
    private final UserJpaRepository userJpaRepository;

    /**
     * Saves a log record to the database. If the creation time is not set, it will
     * be set to the current time.
     *
     * @param logJpa the log entity to save
     */
    @Override
    public void saveLog(LogJpa logJpa) {
        try {
            if (logJpa.getCreateTime() == null) {
                logJpa.setCreateTime(LocalDateTime.now());
            }
            logJpaRepository.save(logJpa);
            log.debug("Save log record successfully: {}", logJpa.getContent());
        } catch (Exception e) {
            log.error("Failed to save log record: {}", e.getMessage(), e);
        }
    }

    /**
     * Retrieves a paginated list of logs based on the provided query parameters.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return a paginated result of log view objects
     */
    @Override
    public Page<LogPageVO> getLogPage(LogPageQuery queryParams) {
        try {
            Pageable pageable = PageRequest.of(
                    queryParams.getPageNum() - 1,
                    queryParams.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createTime"));
                    
            org.springframework.data.domain.Page<LogJpa> jpaPage = logJpaRepository.findAll(
                    (root, query, criteriaBuilder) -> {
                        var predicates = new ArrayList<Predicate>();

                        if (queryParams.getKeywords() != null && !queryParams.getKeywords().trim().isEmpty()) {
                            String keyword = "%" + queryParams.getKeywords().trim() + "%";
                            Predicate contentLike = criteriaBuilder.like(root.get("content"), keyword);
                            Predicate ipLike = criteriaBuilder.like(root.get("ip"), keyword);
                            predicates.add(criteriaBuilder.or(contentLike, ipLike));
                        }

                        if (queryParams.getCreateTime() != null && !queryParams.getCreateTime().isEmpty()) {
                            if (queryParams.getCreateTime().size() >= 1 && queryParams.getCreateTime().get(0) != null
                                    && !queryParams.getCreateTime().get(0).trim().isEmpty()) {
                                String startDateStr = queryParams.getCreateTime().get(0);
                                LocalDateTime startDate = parseDateTime(startDateStr, true);
                                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), startDate));
                            }
                            if (queryParams.getCreateTime().size() >= 2 && queryParams.getCreateTime().get(1) != null
                                    && !queryParams.getCreateTime().get(1).trim().isEmpty()) {
                                String endDateStr = queryParams.getCreateTime().get(1);
                                LocalDateTime endDate = parseDateTime(endDateStr, false);
                                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), endDate));
                            }
                        }

                        query.orderBy(criteriaBuilder.desc(root.get("createTime")));

                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    },
                    pageable);

            Page<LogPageVO> mybatisPage = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
            mybatisPage.setTotal(jpaPage.getTotalElements());

            List<LogPageVO> records = jpaPage.getContent().stream()
                    .map(this::convertToLogPageVO)
                    .collect(Collectors.toList());
            mybatisPage.setRecords(records);

            return mybatisPage;

        } catch (Exception e) {
            log.error("Failed to obtain the log paging list", e);
            throw new RuntimeException("Failed to obtain the log paging list: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves visit trend statistics (PV and UV) for a given date range.
     *
     * @param startDate the start date of the range
     * @param endDate   the end date of the range
     * @return a visit trend view object containing date, PV, and UV lists
     */
    @Override
    public VisitTrendVO getVisitTrend(LocalDate startDate, LocalDate endDate) {
        try {
            VisitTrendVO visitTrend = new VisitTrendVO();
            List<String> dates = new ArrayList<>();

            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                dates.add(currentDate.toString());
                currentDate = currentDate.plusDays(1);
            }
            visitTrend.setDates(dates);

            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

            List<Object[]> pvResults = logJpaRepository.countPvByDateRange(startDateTime, endDateTime);
            Map<String, Long> pvMap = pvResults.stream()
                    .collect(Collectors.toMap(
                            result -> result[0].toString(),
                            result -> ((Number) result[1]).longValue()));

            List<Object[]> uvResults = logJpaRepository.countUvByDateRange(startDateTime, endDateTime);
            Map<String, Long> uvMap = uvResults.stream()
                    .collect(Collectors.toMap(
                            result -> result[0].toString(),
                            result -> ((Number) result[1]).longValue()));

            List<Integer> pvList = new ArrayList<>();
            List<Integer> ipList = new ArrayList<>();

            for (String date : dates) {
                pvList.add(pvMap.getOrDefault(date, 0L).intValue());
                ipList.add(uvMap.getOrDefault(date, 0L).intValue());
            }

            visitTrend.setPvList(pvList);
            visitTrend.setIpList(ipList);

            return visitTrend;

        } catch (Exception e) {
            log.error("Failed to obtain access trends", e);
            throw new RuntimeException("Failed to obtain access trends: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves visit statistics for today and in total, including PV and UV counts
     * and growth rates.
     *
     * @return a visit statistics view object
     */
    @Override
    public VisitStatsVO getVisitStats() {
        try {
            VisitStatsVO result = new VisitStatsVO();

            LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
            LocalDateTime startOfTomorrow = startOfToday.plusDays(1);

            Long todayUv = logJpaRepository.countTodayUv(startOfToday, startOfTomorrow);
            Long totalUv = logJpaRepository.countTotalUv();
            result.setTodayUvCount(todayUv != null ? todayUv.intValue() : 0);
            result.setTotalUvCount(totalUv != null ? totalUv.intValue() : 0);

            Long todayPv = logJpaRepository.countTodayPv(startOfToday, startOfTomorrow);
            Long totalPv = logJpaRepository.countTotalPv();
            result.setTodayPvCount(todayPv != null ? todayPv.intValue() : 0);
            result.setTotalPvCount(totalPv != null ? totalPv.intValue() : 0);

            result.setUvGrowthRate(BigDecimal.ZERO);
            result.setPvGrowthRate(BigDecimal.ZERO);

            return result;

        } catch (Exception e) {
            log.error("Failed to obtain access statistics", e);
            throw new RuntimeException("Failed to obtain access statistics: " + e.getMessage(), e);
        }
    }

    /**
     * Saves a log entity to the database and returns the saved entity.
     *
     * @param logJpa the log entity to save
     * @return the saved log entity
     */
    @Override
    public LogJpa save(LogJpa logJpa) {
        try {
            return logJpaRepository.save(logJpa);
        } catch (Exception e) {
            log.error("Failed to save log", e);
            throw new RuntimeException("Failed to save log: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a log entity by its unique identifier.
     *
     * @param id the unique identifier of the log
     * @return the log entity if found, or null otherwise
     */
    @Override
    public LogJpa findById(Long id) {
        try {
            Optional<LogJpa> optional = logJpaRepository.findById(id);
            return optional.orElse(null);
        } catch (Exception e) {
            log.error("Failed to find the log by ID", e);
            throw new RuntimeException("Failed to find the log by ID: " + e.getMessage(), e);
        }
    }

    /**
     * Converts a log entity to a log page view object.
     *
     * @param logJpa the log entity
     * @return the log page view object
     */
    private LogPageVO convertToLogPageVO(LogJpa logJpa) {
        LogPageVO vo = new LogPageVO();
        vo.setId(logJpa.getId());

        try {
            if (logJpa.getModule() != null) {
                // Find enum by module name instead of enum name
                LogModuleEnum moduleEnum = findModuleEnumByName(logJpa.getModule());
                vo.setModule(moduleEnum);
            }
        } catch (IllegalArgumentException e) {
            vo.setModule(null);
        }

        vo.setContent(logJpa.getContent());
        vo.setRequestUri(logJpa.getRequestUri());
        vo.setIp(logJpa.getIp());

        if (logJpa.getProvince() != null && logJpa.getCity() != null) {
            vo.setRegion(logJpa.getProvince() + " " + logJpa.getCity());
        } else if (logJpa.getProvince() != null) {
            vo.setRegion(logJpa.getProvince());
        } else if (logJpa.getCity() != null) {
            vo.setRegion(logJpa.getCity());
        }

        if (logJpa.getBrowser() != null && logJpa.getBrowserVersion() != null) {
            vo.setBrowser(logJpa.getBrowser() + " " + logJpa.getBrowserVersion());
        } else if (logJpa.getBrowser() != null) {
            vo.setBrowser(logJpa.getBrowser());
        }

        vo.setOs(logJpa.getOs());
        vo.setExecutionTime(logJpa.getExecutionTime());
        vo.setCreateTime(logJpa.getCreateTime());
        vo.setCreateBy(logJpa.getCreateBy());

        // Set operator name based on createBy
        if (logJpa.getCreateBy() != null) {
            userJpaRepository.findById(logJpa.getCreateBy())
                    .ifPresentOrElse(
                            user -> vo.setOperator(user.getNickname() != null ? user.getNickname() : user.getUsername()),
                            () -> vo.setOperator("Unknown user")
                    );
        } else {
            vo.setOperator("System");
        }

        return vo;
    }

    /**
     * Parses a date string into a LocalDateTime object.
     *
     * @param dateStr the date string to parse
     * @param isStart whether to parse as the start of the day (true) or end of the
     *                day (false)
     * @return the parsed LocalDateTime object
     */
    private LocalDateTime parseDateTime(String dateStr, boolean isStart) {

        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be empty");
        }

        dateStr = dateStr.trim();
        try {
            if (dateStr.length() == 10) {
                LocalDate date = LocalDate.parse(dateStr);
                return isStart ? date.atStartOfDay() : date.atTime(23, 59, 59);
            } else {
                return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse date string: " + dateStr, e);
        }
    }

    /**
     * Find LogModuleEnum by module name.
     * 
     * @param moduleName the module name to search for
     * @return the corresponding LogModuleEnum, or null if not found
     */
    private LogModuleEnum findModuleEnumByName(String moduleName) {
        for (LogModuleEnum moduleEnum : LogModuleEnum.values()) {
            if (moduleEnum.getModuleName().equals(moduleName)) {
                return moduleEnum;
            }
        }
        return null;
    }
}

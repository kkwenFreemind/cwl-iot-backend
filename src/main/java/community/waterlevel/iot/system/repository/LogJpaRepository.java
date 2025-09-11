package community.waterlevel.iot.system.repository;

import community.waterlevel.iot.system.model.entity.LogJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing system log entities.
 * <p>
 * Provides methods for counting page views (PV) and unique visitors (UV), as
 * well as retrieving
 * statistics by date range. Extends the base JPA repository for CRUD operations
 * and specification-based
 * queries on {@link LogJpa} entities.
 * </p>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Repository
public interface LogJpaRepository extends JpaRepository<LogJpa, Long>, JpaSpecificationExecutor<LogJpa> {

       /**
        * Counts the number of page views (PV) for today.
        *
        * @param startOfToday    the start of today (inclusive)
        * @param startOfTomorrow the start of tomorrow (exclusive)
        * @return the number of page views for today
        */
       @Query("SELECT COUNT(l) FROM LogJpa l WHERE l.createTime >= :startOfToday AND l.createTime < :startOfTomorrow")
       Long countTodayPv(@Param("startOfToday") LocalDateTime startOfToday,
                     @Param("startOfTomorrow") LocalDateTime startOfTomorrow);

       /**
        * Counts the total number of page views (PV).
        *
        * @return the total number of page views
        */
       @Query("SELECT COUNT(l) FROM LogJpa l")
       Long countTotalPv();

       /**
        * Counts the number of unique visitors (UV) for today.
        *
        * @param startOfToday    the start of today (inclusive)
        * @param startOfTomorrow the start of tomorrow (exclusive)
        * @return the number of unique visitors for today
        */
       @Query("SELECT COUNT(DISTINCT l.ip) FROM LogJpa l WHERE l.createTime >= :startOfToday AND l.createTime < :startOfTomorrow")
       Long countTodayUv(@Param("startOfToday") LocalDateTime startOfToday,
                     @Param("startOfTomorrow") LocalDateTime startOfTomorrow);

       /**
        * Counts the total number of unique visitors (UV).
        *
        * @return the total number of unique visitors
        */
       @Query("SELECT COUNT(DISTINCT l.ip) FROM LogJpa l")
       Long countTotalUv();

       /**
        * Retrieves the number of page views (PV) grouped by date within a specified
        * date range.
        *
        * @param startDate the start date of the range (inclusive)
        * @param endDate   the end date of the range (inclusive)
        * @return a list of objects containing date and count pairs
        */
       @Query("SELECT FUNCTION('TO_CHAR', l.createTime, 'YYYY-MM-DD') as date, COUNT(l) as count FROM LogJpa l " +
                     "WHERE l.createTime BETWEEN :startDate AND :endDate " +
                     "GROUP BY FUNCTION('TO_CHAR', l.createTime, 'YYYY-MM-DD') ORDER BY FUNCTION('TO_CHAR', l.createTime, 'YYYY-MM-DD')")
       List<Object[]> countPvByDateRange(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       /**
        * Retrieves the number of unique visitors (UV) grouped by date within a
        * specified date range.
        *
        * @param startDate the start date of the range (inclusive)
        * @param endDate   the end date of the range (inclusive)
        * @return a list of objects containing date and count pairs
        */
       @Query("SELECT FUNCTION('TO_CHAR', l.createTime, 'YYYY-MM-DD') as date, COUNT(DISTINCT l.ip) as count FROM LogJpa l "
                     +
                     "WHERE l.createTime BETWEEN :startDate AND :endDate " +
                     "GROUP BY FUNCTION('TO_CHAR', l.createTime, 'YYYY-MM-DD') ORDER BY FUNCTION('TO_CHAR', l.createTime, 'YYYY-MM-DD')")
       List<Object[]> countUvByDateRange(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);
}

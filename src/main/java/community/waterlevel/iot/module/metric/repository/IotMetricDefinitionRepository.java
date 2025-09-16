package community.waterlevel.iot.module.metric.repository;

import community.waterlevel.iot.module.metric.model.entity.IotMetricDefinition;
import community.waterlevel.iot.module.metric.model.enums.MetricDataType;
import community.waterlevel.iot.module.metric.model.enums.PhysicalQuantity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for IoT metric definitions.
 *
 * <p>Provides data access methods for managing IoT metric definitions
 * with department-scoped queries and custom search functionality.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/16
 */
@Repository
public interface IotMetricDefinitionRepository extends JpaRepository<IotMetricDefinition, Long> {

    /**
     * Find metric definition by department ID and metric name.
     *
     * @param deptId the department ID
     * @param metricName the metric name
     * @return Optional containing the metric definition if found
     */
    Optional<IotMetricDefinition> findByDeptIdAndMetricName(Long deptId, String metricName);

    /**
     * Find metric definition by ID and department ID.
     *
     * @param id the metric definition ID
     * @param deptId the department ID
     * @return Optional containing the metric definition if found
     */
    Optional<IotMetricDefinition> findByIdAndDeptId(Long id, Long deptId);

    /**
     * Find all active metric definitions for a department.
     *
     * @param deptId the department ID
     * @return list of active metric definitions
     */
    List<IotMetricDefinition> findByDeptIdAndIsActiveTrue(Long deptId);

    /**
     * Find all metric definitions for a department with pagination.
     *
     * @param deptId the department ID
     * @param pageable pagination information
     * @return page of metric definitions
     */
    Page<IotMetricDefinition> findByDeptId(Long deptId, Pageable pageable);

    /**
     * Find metric definitions by physical quantity.
     *
     * @param deptId the department ID
     * @param physicalQuantity the physical quantity
     * @return list of metric definitions
     */
    List<IotMetricDefinition> findByDeptIdAndPhysicalQuantity(Long deptId, PhysicalQuantity physicalQuantity);

    /**
     * Find metric definitions by data type.
     *
     * @param deptId the department ID
     * @param dataType the data type
     * @return list of metric definitions
     */
    List<IotMetricDefinition> findByDeptIdAndDataType(Long deptId, MetricDataType dataType);

    /**
     * Search metric definitions by name or alias.
     *
     * @param deptId the department ID
     * @param keyword the search keyword
     * @param pageable pagination information
     * @return page of matching metric definitions
     */
    @Query("SELECT m FROM IotMetricDefinition m WHERE m.deptId = :deptId AND " +
           "(LOWER(m.metricName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.alias) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<IotMetricDefinition> searchByKeyword(@Param("deptId") Long deptId,
                                             @Param("keyword") String keyword,
                                             Pageable pageable);

    /**
     * Check if metric name exists for department (excluding specific ID for updates).
     *
     * @param deptId the department ID
     * @param metricName the metric name
     * @param excludeId the ID to exclude from check (for updates)
     * @return true if name exists, false otherwise
     */
    @Query("SELECT COUNT(m) > 0 FROM IotMetricDefinition m WHERE m.deptId = :deptId " +
           "AND m.metricName = :metricName AND m.id != :excludeId")
    boolean existsByDeptIdAndMetricNameAndIdNot(@Param("deptId") Long deptId,
                                               @Param("metricName") String metricName,
                                               @Param("excludeId") Long excludeId);

    /**
     * Check if alias exists for department (excluding specific ID for updates).
     *
     * @param deptId the department ID
     * @param alias the alias
     * @param excludeId the ID to exclude from check (for updates)
     * @return true if alias exists, false otherwise
     */
    @Query("SELECT COUNT(m) > 0 FROM IotMetricDefinition m WHERE m.deptId = :deptId " +
           "AND m.alias = :alias AND m.id != :excludeId AND m.alias IS NOT NULL")
    boolean existsByDeptIdAndAliasAndIdNot(@Param("deptId") Long deptId,
                                          @Param("alias") String alias,
                                          @Param("excludeId") Long excludeId);

    /**
     * Count active metric definitions for a department.
     *
     * @param deptId the department ID
     * @return count of active metric definitions
     */
    long countByDeptIdAndIsActiveTrue(Long deptId);

    /**
     * Find metric definitions by multiple physical quantities.
     *
     * @param deptId the department ID
     * @param physicalQuantities list of physical quantities
     * @return list of metric definitions
     */
    List<IotMetricDefinition> findByDeptIdAndPhysicalQuantityIn(Long deptId,
                                                               List<PhysicalQuantity> physicalQuantities);
}
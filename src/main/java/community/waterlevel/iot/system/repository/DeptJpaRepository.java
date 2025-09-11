package community.waterlevel.iot.system.repository;

import community.waterlevel.iot.system.model.entity.DeptJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing department entities.
 * <p>
 * Provides methods for querying, checking existence, and retrieving department
 * records
 * with various filters and sorting options. Extends the base JPA repository for
 * CRUD operations
 * and specification-based queries on {@link DeptJpa} entities.
 * </p>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Repository
public interface DeptJpaRepository extends JpaRepository<DeptJpa, Long>, JpaSpecificationExecutor<DeptJpa> {

    /**
     * Finds a department entity by its unique code.
     *
     * @param code the unique department code
     * @return an Optional containing the department entity if found, or empty
     *         otherwise
     */
    @Query("SELECT d FROM DeptJpa d WHERE d.code = :code")
    Optional<DeptJpa> findByCode(@Param("code") String code);

    /**
     * Counts the number of departments with the given code, excluding a specific
     * ID.
     *
     * @param code the unique department code
     * @param id   the ID to exclude from the count
     * @return the number of departments with the given code, excluding the
     *         specified ID
     */
    @Query("SELECT COUNT(d) FROM DeptJpa d WHERE d.code = :code AND d.id != :id")
    long countByCodeAndIdNot(@Param("code") String code, @Param("id") Long id);

    /**
     * Counts the number of departments with the given code.
     *
     * @param code the unique department code
     * @return the number of departments with the given code
     */
    @Query("SELECT COUNT(d) FROM DeptJpa d WHERE d.code = :code")
    long countByCode(@Param("code") String code);

    /**
     * Retrieves a list of departments with the specified status, ordered by the
     * sort field.
     *
     * @param status the status of the departments to retrieve
     * @return a list of departments with the given status, ordered by sort
     */
    @Query("SELECT d FROM DeptJpa d WHERE d.status = :status ORDER BY d.sort ASC")
    List<DeptJpa> findByStatusOrderBySort(@Param("status") Integer status);

    /**
     * Retrieves a list of departments with the specified status.
     *
     * @param status the status of the departments to retrieve
     * @return a list of departments with the given status
     */
    List<DeptJpa> findByStatus(Integer status);

    /**
     * Checks if a department exists by its code.
     *
     * @param code the unique department code
     * @return true if a department with the given code exists, false otherwise
     */
    default boolean existsByCode(String code) {
        return countByCode(code) > 0;
    }

    /**
     * Checks if a department exists by its code, excluding a specific ID.
     *
     * @param code the unique department code
     * @param id   the ID to exclude from the check
     * @return true if a department with the given code exists (excluding the
     *         specified ID), false otherwise
     */
    default boolean existsByCodeAndIdNot(String code, Long id) {
        return countByCodeAndIdNot(code, id) > 0;
    }

    /**
     * Checks if any department exists with the specified parent ID.
     *
     * @param parentId the parent department ID
     * @return true if a department with the given parent ID exists, false otherwise
     */
    boolean existsByParentId(Long parentId);

    /**
     * Retrieves all departments ordered by the sort field.
     *
     * @return a list of all departments ordered by sort
     */
    @Query("SELECT d FROM DeptJpa d ORDER BY d.sort ASC")
    List<DeptJpa> findAllOrderBySort();

    /**
     * Retrieves a list of departments by parent ID, ordered by the sort field.
     *
     * @param parentId the parent department ID
     * @return a list of departments with the given parent ID, ordered by sort
     */
    @Query("SELECT d FROM DeptJpa d WHERE d.parentId = :parentId ORDER BY d.sort ASC")
    List<DeptJpa> findByParentIdOrderBySort(@Param("parentId") Long parentId);
}

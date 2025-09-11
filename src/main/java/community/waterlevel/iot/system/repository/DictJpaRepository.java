package community.waterlevel.iot.system.repository;

import community.waterlevel.iot.system.model.entity.DictJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing dictionary entities.
 * <p>
 * Provides methods for querying, checking existence, and retrieving dictionary
 * records
 * with various filters and sorting options. Extends the base JPA repository for
 * CRUD operations
 * and specification-based queries on {@link DictJpa} entities.
 * </p>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Repository
public interface DictJpaRepository extends JpaRepository<DictJpa, Long>, JpaSpecificationExecutor<DictJpa> {

    /**
     * Finds a dictionary entity by its unique dictionary code.
     *
     * @param dictCode the unique dictionary code
     * @return an Optional containing the dictionary entity if found, or empty
     *         otherwise
     */
    Optional<DictJpa> findByDictCode(String dictCode);

    /**
     * Checks if a dictionary entity exists by its dictionary code, excluding a
     * specific ID.
     *
     * @param dictCode the unique dictionary code
     * @param id       the ID to exclude from the check
     * @return true if a dictionary with the given code exists (excluding the
     *         specified ID), false otherwise
     */
    boolean existsByDictCodeAndIdNot(String dictCode, Long id);

    /**
     * Checks if a dictionary entity exists by its dictionary code.
     *
     * @param dictCode the unique dictionary code
     * @return true if a dictionary with the given code exists, false otherwise
     */
    boolean existsByDictCode(String dictCode);

    /**
     * Retrieves a list of dictionaries with the specified status, ordered by
     * creation time descending.
     *
     * @param status the status of the dictionaries to retrieve
     * @return a list of dictionaries with the given status, ordered by creation
     *         time descending
     */
    List<DictJpa> findByStatusOrderByCreateTimeDesc(Integer status);

    /**
     * Retrieves a list of dictionaries by a list of IDs.
     *
     * @param ids the list of dictionary IDs
     * @return a list of dictionaries with the given IDs
     */
    List<DictJpa> findByIdIn(List<Long> ids);
}

package community.waterlevel.iot.system.repository;

import community.waterlevel.iot.system.model.entity.DictItemJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository interface for managing dictionary item entities.
 * <p>
 * Provides methods for querying, checking existence, and logically deleting dictionary item records
 * with various filters and sorting options. Extends the base JPA repository for CRUD operations
 * and specification-based queries on {@link DictItemJpa} entities.
 * </p>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Repository
public interface DictItemJpaRepository extends JpaRepository<DictItemJpa, Long>, JpaSpecificationExecutor<DictItemJpa> {

    /**
     * Retrieves a list of dictionary items by dictionary code and status, ordered by sort and creation time.
     *
     * @param dictCode the code of the dictionary
     * @param status the status of the dictionary items
     * @return a list of dictionary items matching the criteria
     */
    List<DictItemJpa> findByDictCodeAndStatusOrderBySortAscCreateTimeAsc(String dictCode, Integer status);

    /**
     * Retrieves a list of dictionary items by dictionary code, ordered by sort.
     *
     * @param dictCode the code of the dictionary
     * @return a list of dictionary items for the given dictionary code
     */
    List<DictItemJpa> findByDictCodeOrderBySortAsc(String dictCode);

    /**
     * Checks if a dictionary item exists by dictionary code and value, excluding a specific ID.
     *
     * @param dictCode the code of the dictionary
     * @param value the value of the dictionary item
     * @param id the ID to exclude from the check
     * @return true if a dictionary item exists with the given code and value (excluding the specified ID), false otherwise
     */
    boolean existsByDictCodeAndValueAndIdNot(String dictCode, String value, Long id);

    /**
     * Checks if a dictionary item exists by dictionary code and value.
     *
     * @param dictCode the code of the dictionary
     * @param value the value of the dictionary item
     * @return true if a dictionary item exists with the given code and value, false otherwise
     */
    boolean existsByDictCodeAndValue(String dictCode, String value);

    /**
     * Logically deletes all dictionary items by dictionary code (sets isDeleted to 1).
     *
     * @param dictCode the code of the dictionary
     */
    @Modifying
    @Transactional
    @Query("UPDATE DictItemJpa d SET d.isDeleted = 1 WHERE d.dictCode = :dictCode")
    void logicalDeleteByDictCode(@Param("dictCode") String dictCode);

    /**
     * Logically deletes all dictionary items by a list of dictionary codes (sets isDeleted to 1).
     *
     * @param dictCodes the list of dictionary codes
     */
    @Modifying
    @Transactional
    @Query("UPDATE DictItemJpa d SET d.isDeleted = 1 WHERE d.dictCode IN :dictCodes")
    void logicalDeleteByDictCodes(@Param("dictCodes") List<String> dictCodes);
}

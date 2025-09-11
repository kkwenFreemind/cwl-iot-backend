package community.waterlevel.iot.system.repository;

import community.waterlevel.iot.common.repository.BaseJpaRepository;
import community.waterlevel.iot.system.model.entity.ConfigJpa;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing system configuration entities.
 * <p>
 * Provides methods for querying, checking existence, and retrieving active
 * configuration records.
 * Extends the base JPA repository for CRUD operations on {@link ConfigJpa}
 * entities.
 * </p>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Repository
public interface ConfigJpaRepository extends BaseJpaRepository<ConfigJpa, Long> {

    /**
     * Finds a configuration entity by its unique configuration key.
     *
     * @param configKey the unique configuration key
     * @return an Optional containing the configuration entity if found, or empty
     *         otherwise
     */
    Optional<ConfigJpa> findByConfigKey(String configKey);

    /**
     * Checks if a configuration entity exists by its configuration key.
     *
     * @param configKey the unique configuration key
     * @return true if a configuration with the given key exists, false otherwise
     */
    boolean existsByConfigKey(String configKey);

    /**
     * Checks if a configuration entity exists by its configuration key, excluding a
     * specific ID.
     *
     * @param configKey the unique configuration key
     * @param id        the ID to exclude from the check
     * @return true if a configuration with the given key exists (excluding the
     *         specified ID), false otherwise
     */
    @Query("SELECT COUNT(c) > 0 FROM ConfigJpa c WHERE c.configKey = :configKey AND c.id != :id")
    boolean existsByConfigKeyAndIdNot(@Param("configKey") String configKey, @Param("id") Long id);

    /**
     * Retrieves all active (not deleted) configuration entities.
     *
     * @return a list of active configuration entities
     */
    @Query("SELECT c FROM ConfigJpa c WHERE c.isDeleted = 0")
    List<ConfigJpa> findAllActive();

}

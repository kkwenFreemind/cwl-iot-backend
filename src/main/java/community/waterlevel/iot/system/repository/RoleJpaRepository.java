package community.waterlevel.iot.system.repository;

import community.waterlevel.iot.system.model.entity.RoleJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for managing role entities.
 * <p>
 * Provides methods for querying, checking existence, and retrieving role
 * records
 * with various filters and sorting options. Extends the base JPA repository for
 * CRUD operations
 * and specification-based queries on {@link RoleJpa} entities.
 * </p>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Repository
public interface RoleJpaRepository extends JpaRepository<RoleJpa, Long>, JpaSpecificationExecutor<RoleJpa> {

    /**
     * Retrieves a list of active role IDs by a set of role codes.
     *
     * @param codes the set of role codes
     * @return a list of active role IDs matching the given codes
     */
    @Query("SELECT r.id FROM RoleJpa r WHERE r.code IN :codes AND r.status = 1")
    List<Long> findActiveRoleIdsByCodes(@Param("codes") Set<String> codes);

    /**
     * Finds a role entity by its unique code.
     *
     * @param code the unique role code
     * @return an Optional containing the role entity if found, or empty otherwise
     */
    Optional<RoleJpa> findByCode(String code);

    /**
     * Finds a role entity by its unique name.
     *
     * @param name the unique role name
     * @return an Optional containing the role entity if found, or empty otherwise
     */
    Optional<RoleJpa> findByName(String name);

    /**
     * Checks if a role exists by its code, excluding a specific ID.
     *
     * @param code the unique role code
     * @param id   the ID to exclude from the check
     * @return true if a role with the given code exists (excluding the specified
     *         ID), false otherwise
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * Checks if a role exists by its name, excluding a specific ID.
     *
     * @param name the unique role name
     * @param id   the ID to exclude from the check
     * @return true if a role with the given name exists (excluding the specified
     *         ID), false otherwise
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * Retrieves a list of roles by status, ordered by the sort field.
     *
     * @param status the status of the roles to retrieve
     * @return a list of roles with the given status, ordered by sort
     */
    List<RoleJpa> findByStatusOrderBySortAsc(Integer status);

    /**
     * Retrieves a list of active roles, ordered by the sort field.
     *
     * @return a list of active roles ordered by sort
     */
    @Query("SELECT r FROM RoleJpa r WHERE r.status = 1 ORDER BY r.sort ASC")
    List<RoleJpa> findActiveRoles();
}

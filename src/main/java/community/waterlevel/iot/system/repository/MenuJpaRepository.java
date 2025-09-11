package community.waterlevel.iot.system.repository;

import community.waterlevel.iot.system.model.entity.MenuJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing system menu entities.
 * <p>
 * Provides methods for querying, checking existence, and retrieving menu
 * records
 * with various filters and sorting options. Extends the base JPA repository for
 * CRUD operations
 * on {@link MenuJpa} entities.
 * </p>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Repository
public interface MenuJpaRepository extends JpaRepository<MenuJpa, Long> {

    /**
     * Retrieves a list of menus excluding those of type 4 (typically buttons),
     * ordered by sort.
     *
     * @return a list of menu entities excluding buttons, ordered by sort
     */
    @Query("SELECT DISTINCT m FROM MenuJpa m WHERE m.type != 4 ORDER BY m.sort ASC")
    List<MenuJpa> findMenusExcludingButtons();

    /**
     * Retrieves a list of menus by parent ID, ordered by sort.
     *
     * @param parentId the parent menu ID
     * @return a list of menu entities with the given parent ID, ordered by sort
     */
    List<MenuJpa> findByParentIdOrderBySortAsc(Long parentId);

    /**
     * Retrieves a list of menus by type, ordered by sort.
     *
     * @param type the type of the menu
     * @return a list of menu entities with the given type, ordered by sort
     */
    List<MenuJpa> findByTypeOrderBySortAsc(Integer type);

    /**
     * Retrieves a list of menus by visibility status, ordered by sort.
     *
     * @param visible the visibility status of the menu
     * @return a list of menu entities with the given visibility, ordered by sort
     */
    List<MenuJpa> findByVisibleOrderBySortAsc(Integer visible);

    /**
     * Checks if a menu exists by its name, excluding a specific ID.
     *
     * @param name the name of the menu
     * @param id   the ID to exclude from the check
     * @return true if a menu with the given name exists (excluding the specified
     *         ID), false otherwise
     */
    boolean existsByNameAndIdNot(String name, Long id);
}

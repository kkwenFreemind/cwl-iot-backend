package community.waterlevel.iot.system.repository;

import community.waterlevel.iot.system.model.entity.RoleMenuJpa;
import community.waterlevel.iot.system.model.entity.RoleMenuId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Repository interface for managing role-menu association entities.
 * <p>
 * Provides methods for querying and deleting role-menu associations by role or
 * menu IDs.
 * Extends the base JPA repository for CRUD operations on {@link RoleMenuJpa}
 * entities.
 * </p>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Repository
public interface RoleMenuJpaRepository extends JpaRepository<RoleMenuJpa, RoleMenuId> {

    /**
     * Retrieves a list of menu IDs associated with the given set of role IDs.
     *
     * @param roleIds the set of role IDs
     * @return a list of menu IDs associated with the given role IDs
     */
    @Query("SELECT DISTINCT rm.id.menuId FROM RoleMenuJpa rm WHERE rm.id.roleId IN :roleIds")
    List<Long> findMenuIdsByRoleIds(@Param("roleIds") Set<Long> roleIds);

    /**
     * Retrieves a list of role IDs associated with the given menu ID.
     *
     * @param menuId the menu ID
     * @return a list of role IDs associated with the given menu ID
     */
    @Query("SELECT rm.id.roleId FROM RoleMenuJpa rm WHERE rm.id.menuId = :menuId")
    List<Long> findRoleIdsByMenuId(@Param("menuId") Long menuId);

    /**
     * Deletes all role-menu associations for the given role ID.
     *
     * @param roleId the role ID
     */
    void deleteByIdRoleId(Long roleId);

    /**
     * Deletes all role-menu associations for the given menu ID.
     *
     * @param menuId the menu ID
     */
    void deleteByIdMenuId(Long menuId);
}

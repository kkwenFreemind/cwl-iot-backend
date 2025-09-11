package community.waterlevel.iot.system.repository;

import community.waterlevel.iot.system.model.entity.UserRoleJpa;
import community.waterlevel.iot.system.model.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Repository interface for managing user-role association entities.
 * <p>
 * Provides methods for querying and deleting user-role associations by user or
 * role IDs.
 * Extends the base JPA repository for CRUD operations on {@link UserRoleJpa}
 * entities.
 * </p>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Repository
public interface UserRoleJpaRepository extends JpaRepository<UserRoleJpa, UserRoleId> {

    /**
     * Retrieves a list of role IDs associated with the given set of user IDs.
     *
     * @param userIds the set of user IDs
     * @return a list of role IDs associated with the given user IDs
     */
    @Query("SELECT DISTINCT ur.id.roleId FROM UserRoleJpa ur WHERE ur.id.userId IN :userIds")
    List<Long> findRoleIdsByUserIds(@Param("userIds") Set<Long> userIds);

    /**
     * Retrieves a list of user IDs associated with the given set of role IDs.
     *
     * @param roleIds the set of role IDs
     * @return a list of user IDs associated with the given role IDs
     */
    @Query("SELECT DISTINCT ur.id.userId FROM UserRoleJpa ur WHERE ur.id.roleId IN :roleIds")
    List<Long> findUserIdsByRoleIds(@Param("roleIds") Set<Long> roleIds);

    /**
     * Retrieves a list of role IDs associated with a specific user ID.
     *
     * @param userId the user ID
     * @return a list of role IDs associated with the given user ID
     */
    @Query("SELECT ur.id.roleId FROM UserRoleJpa ur WHERE ur.id.userId = :userId")
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * Retrieves a list of user IDs associated with a specific role ID.
     *
     * @param roleId the role ID
     * @return a list of user IDs associated with the given role ID
     */
    @Query("SELECT ur.id.userId FROM UserRoleJpa ur WHERE ur.id.roleId = :roleId")
    List<Long> findUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * Deletes all user-role associations for the given user ID.
     *
     * @param userId the user ID
     */
    void deleteByIdUserId(Long userId);

    /**
     * Deletes all user-role associations for the given role ID.
     *
     * @param roleId the role ID
     */
    void deleteByIdRoleId(Long roleId);

    /**
     * Checks if a user-role association exists for the given user ID and role ID.
     *
     * @param userId the user ID
     * @param roleId the role ID
     * @return true if the association exists, false otherwise
     */
    boolean existsByIdUserIdAndIdRoleId(Long userId, Long roleId);
}

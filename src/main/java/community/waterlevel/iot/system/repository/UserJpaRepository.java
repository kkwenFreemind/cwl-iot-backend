package community.waterlevel.iot.system.repository;

import community.waterlevel.iot.system.model.entity.UserJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing user entities.
 * <p>
 * Provides methods for querying, checking existence, and retrieving user
 * records
 * with various filters and sorting options. Extends the base JPA repository for
 * CRUD operations
 * and specification-based queries on {@link UserJpa} entities.
 * </p>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpa, Long>, JpaSpecificationExecutor<UserJpa> {

    /**
     * Finds a user entity by its username.
     *
     * @param username the username of the user
     * @return an Optional containing the user entity if found, or empty otherwise
     */
    Optional<UserJpa> findByUsername(String username);

    /**
     * Finds a user entity by its mobile number.
     *
     * @param mobile the mobile number of the user
     * @return an Optional containing the user entity if found, or empty otherwise
     */
    Optional<UserJpa> findByMobile(String mobile);

    /**
     * Finds a user entity by its OpenID.
     *
     * @param openid the OpenID of the user
     * @return an Optional containing the user entity if found, or empty otherwise
     */
    Optional<UserJpa> findByOpenid(String openid);

    /**
     * Finds a user entity by its email address.
     *
     * @param email the email address of the user
     * @return an Optional containing the user entity if found, or empty otherwise
     */
    Optional<UserJpa> findByEmail(String email);

    /**
     * Checks if a user exists by username, excluding a specific ID.
     *
     * @param username the username of the user
     * @param id       the ID to exclude from the check
     * @return true if a user with the given username exists (excluding the
     *         specified ID), false otherwise
     */
    boolean existsByUsernameAndIdNot(String username, Long id);

    /**
     * Checks if a user exists by mobile number, excluding a specific ID.
     *
     * @param mobile the mobile number of the user
     * @param id     the ID to exclude from the check
     * @return true if a user with the given mobile number exists (excluding the
     *         specified ID), false otherwise
     */
    boolean existsByMobileAndIdNot(String mobile, Long id);

    /**
     * Checks if a user exists by email address, excluding a specific ID.
     *
     * @param email the email address of the user
     * @param id    the ID to exclude from the check
     * @return true if a user with the given email exists (excluding the specified
     *         ID), false otherwise
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Checks if a user exists by OpenID, excluding a specific ID.
     *
     * @param openid the OpenID of the user
     * @param id     the ID to exclude from the check
     * @return true if a user with the given OpenID exists (excluding the specified
     *         ID), false otherwise
     */
    boolean existsByOpenidAndIdNot(String openid, Long id);

    /**
     * Retrieves a list of users by status.
     *
     * @param status the status of the users to retrieve
     * @return a list of users with the given status
     */
    List<UserJpa> findByStatus(Integer status);

    /**
     * Retrieves a list of users by department ID.
     *
     * @param deptId the department ID
     * @return a list of users belonging to the given department
     */
    List<UserJpa> findByDeptId(Long deptId);

    /**
     * Finds an active user by username (status = 1).
     *
     * @param username the username of the user
     * @return an Optional containing the active user entity if found, or empty
     *         otherwise
     */
    @Query("SELECT u FROM UserJpa u WHERE u.username = :username AND u.status = 1")
    Optional<UserJpa> findActiveUserByUsername(@Param("username") String username);

    /**
     * Finds an active user by mobile number (status = 1).
     *
     * @param mobile the mobile number of the user
     * @return an Optional containing the active user entity if found, or empty
     *         otherwise
     */
    @Query("SELECT u FROM UserJpa u WHERE u.mobile = :mobile AND u.status = 1")
    Optional<UserJpa> findActiveUserByMobile(@Param("mobile") String mobile);

    /**
     * Finds an active user by OpenID (status = 1).
     *
     * @param openid the OpenID of the user
     * @return an Optional containing the active user entity if found, or empty
     *         otherwise
     */
    @Query("SELECT u FROM UserJpa u WHERE u.openid = :openid AND u.status = 1")
    Optional<UserJpa> findActiveUserByOpenid(@Param("openid") String openid);
}

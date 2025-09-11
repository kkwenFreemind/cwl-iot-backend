package community.waterlevel.iot.system.repository;

import community.waterlevel.iot.system.model.entity.NoticeJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing notice entities.
 * <p>
 * Provides methods for publishing, revoking, and querying notices with various
 * filters and sorting options.
 * Extends the base JPA repository for CRUD operations and specification-based
 * queries on {@link NoticeJpa} entities.
 * </p>
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Repository
public interface NoticeJpaRepository extends JpaRepository<NoticeJpa, Long>, JpaSpecificationExecutor<NoticeJpa> {

    /**
     * Publishes a notice by updating its publish status and publish time.
     *
     * @param id          the ID of the notice to publish
     * @param publishTime the time the notice is published
     * @return the number of affected rows
     */
    @Modifying
    @Query("UPDATE NoticeJpa n SET n.publishStatus = 1, n.publishTime = :publishTime WHERE n.id = :id")
    int publishNotice(@Param("id") Long id, @Param("publishTime") LocalDateTime publishTime);

    /**
     * Revokes a notice by updating its publish status and revoke time.
     *
     * @param id         the ID of the notice to revoke
     * @param revokeTime the time the notice is revoked
     * @return the number of affected rows
     */
    @Modifying
    @Query("UPDATE NoticeJpa n SET n.publishStatus = -1, n.revokeTime = :revokeTime WHERE n.id = :id")
    int revokeNotice(@Param("id") Long id, @Param("revokeTime") LocalDateTime revokeTime);

    /**
     * Retrieves a list of notices by publish status.
     *
     * @param publishStatus the publish status to filter by
     * @return a list of notices with the given publish status
     */
    List<NoticeJpa> findByPublishStatus(Integer publishStatus);

    /**
     * Retrieves a list of published notices, ordered by publish time descending.
     *
     * @return a list of published notices ordered by publish time descending
     */
    @Query("SELECT n FROM NoticeJpa n WHERE n.publishStatus = 1 ORDER BY n.publishTime DESC")
    List<NoticeJpa> findPublishedNotices();
}

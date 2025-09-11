package community.waterlevel.iot.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Basic JPA Repository Interface for generic entity operations.
 * <p>
 * Extends {@link JpaRepository} and {@link JpaSpecificationExecutor} to provide
 * CRUD and specification-based query support.
 * </p>
 *
 * @param <T>  the entity type
 * @param <ID> the type of the entity's primary key
 * @author Chang Xiu-Wen, AI-Enhanced
 */
@NoRepositoryBean
public interface BaseJpaRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    // You can add common methods here
}

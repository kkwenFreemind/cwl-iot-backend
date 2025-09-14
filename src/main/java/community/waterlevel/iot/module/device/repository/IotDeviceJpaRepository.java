package community.waterlevel.iot.module.device.repository;

import community.waterlevel.iot.module.device.model.entity.IotDeviceJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IotDeviceJpaRepository extends JpaRepository<IotDeviceJpa, UUID>, JpaSpecificationExecutor<IotDeviceJpa> {

    @Query("SELECT d FROM IotDeviceJpa d WHERE d.deptId = :deptId")
    List<IotDeviceJpa> findByDeptId(@Param("deptId") Long deptId);

    @Query("SELECT d FROM IotDeviceJpa d WHERE d.status = :status")
    List<IotDeviceJpa> findByStatus(@Param("status") String status);
}

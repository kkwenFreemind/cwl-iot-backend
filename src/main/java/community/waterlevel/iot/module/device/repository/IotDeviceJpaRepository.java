package community.waterlevel.iot.module.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import community.waterlevel.iot.module.device.model.entity.IotDeviceJpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing IoT device entities.
 * Provides methods for querying, checking existence, and retrieving device
 * records with various filters including spatial queries and department-based access.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/14
 */
@Repository
public interface IotDeviceJpaRepository extends JpaRepository<IotDeviceJpa, UUID>, JpaSpecificationExecutor<IotDeviceJpa> {

    /**
     * Finds a device entity by its name within a specific department.
     *
     * @param deviceName the device name
     * @param deptId     the department ID
     * @return an Optional containing the device entity if found, or empty otherwise
     */
    Optional<IotDeviceJpa> findByDeviceNameAndDeptId(String deviceName, Long deptId);

    /**
     * Checks if a device exists by name within a department, excluding a specific device ID.
     *
     * @param deviceName the device name
     * @param deptId     the department ID
     * @param deviceId   the device ID to exclude from the check
     * @return true if a device with the given name exists in the department (excluding the specified ID)
     */
    boolean existsByDeviceNameAndDeptIdAndDeviceIdNot(String deviceName, Long deptId, UUID deviceId);

    /**
     * Retrieves a list of devices by department ID.
     *
     * @param deptId the department ID
     * @return a list of devices belonging to the given department
     */
    List<IotDeviceJpa> findByDeptId(Long deptId);

    /**
     * Retrieves a list of devices by status.
     *
     * @param status the device status
     * @return a list of devices with the given status
     */
    List<IotDeviceJpa> findByStatus(String status);

    /**
     * Retrieves a list of devices by department ID and status.
     *
     * @param deptId the department ID
     * @param status the device status
     * @return a list of devices in the department with the given status
     */
    List<IotDeviceJpa> findByDeptIdAndStatus(Long deptId, String status);

    /**
     * Retrieves a list of devices by multiple department IDs.
     *
     * @param deptIds the list of department IDs
     * @return a list of devices belonging to any of the given departments
     */
    List<IotDeviceJpa> findByDeptIdIn(List<Long> deptIds);

    /**
     * Retrieves a list of devices by device model.
     *
     * @param deviceModel the device model
     * @return a list of devices with the given model
     */
    List<IotDeviceJpa> findByDeviceModel(String deviceModel);

    /**
     * Finds devices created by a specific user.
     *
     * @param createdBy the user ID who created the devices
     * @return a list of devices created by the given user
     */
    List<IotDeviceJpa> findByCreatedBy(Long createdBy);

    /**
     * Finds online devices (last seen within specified minutes).
     *
     * @param threshold the threshold datetime for considering a device online
     * @return a list of devices that communicated after the threshold
     */
    @Query("SELECT d FROM IotDeviceJpa d WHERE d.lastSeen >= :threshold AND d.status = 'active'")
    List<IotDeviceJpa> findOnlineDevices(@Param("threshold") LocalDateTime threshold);

    /**
     * Finds offline devices in a specific department.
     *
     * @param deptId    the department ID
     * @param threshold the threshold datetime for considering a device offline
     * @return a list of devices in the department that haven't communicated since threshold
     */
    @Query("SELECT d FROM IotDeviceJpa d WHERE d.deptId = :deptId AND (d.lastSeen < :threshold OR d.lastSeen IS NULL) AND d.status = 'active'")
    List<IotDeviceJpa> findOfflineDevicesInDept(@Param("deptId") Long deptId, @Param("threshold") LocalDateTime threshold);

    /**
     * Counts total devices by department.
     *
     * @param deptId the department ID
     * @return the total number of devices in the department
     */
    long countByDeptId(Long deptId);

    /**
     * Counts active devices by department.
     *
     * @param deptId the department ID
     * @return the number of active devices in the department
     */
    @Query("SELECT COUNT(d) FROM IotDeviceJpa d WHERE d.deptId = :deptId AND d.status = 'active'")
    long countActiveDevicesInDept(@Param("deptId") Long deptId);

    /**
     * Simple coordinate-based search within a bounding box.
     * This is a simplified version without PostGIS functions.
     *
     * @param minLat minimum latitude
     * @param maxLat maximum latitude
     * @param minLng minimum longitude
     * @param maxLng maximum longitude
     * @return list of devices within the coordinate bounds
     */
    @Query("SELECT d FROM IotDeviceJpa d WHERE d.latitude BETWEEN :minLat AND :maxLat " +
           "AND d.longitude BETWEEN :minLng AND :maxLng")
    List<IotDeviceJpa> findDevicesInBounds(@Param("minLat") Double minLat, 
                                          @Param("maxLat") Double maxLat,
                                          @Param("minLng") Double minLng, 
                                          @Param("maxLng") Double maxLng);

}
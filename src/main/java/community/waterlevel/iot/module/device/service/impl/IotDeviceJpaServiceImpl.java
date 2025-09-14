package community.waterlevel.iot.module.device.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import community.waterlevel.iot.common.annotation.DataPermission;
import community.waterlevel.iot.common.exception.BusinessException;
import community.waterlevel.iot.core.security.util.SecurityUtils;
import community.waterlevel.iot.module.device.converter.IotDeviceJpaConverter;
import community.waterlevel.iot.module.device.model.entity.IotDeviceJpa;
import community.waterlevel.iot.module.device.model.form.IotDeviceForm;
import community.waterlevel.iot.module.device.model.query.IotDevicePageQuery;
import community.waterlevel.iot.module.device.model.vo.IotDeviceVO;
import community.waterlevel.iot.module.device.repository.IotDeviceJpaRepository;
import community.waterlevel.iot.module.device.service.IotDeviceJpaService;
import community.waterlevel.iot.system.repository.DeptJpaRepository;
import community.waterlevel.iot.system.repository.UserJpaRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for IoT device-related business logic and operations.
 * Provides comprehensive device management with spatial capabilities and 
 * department-based access control through data permission filtering.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/14
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IotDeviceJpaServiceImpl implements IotDeviceJpaService {

    private final IotDeviceJpaRepository deviceRepository;
    private final DeptJpaRepository deptRepository;
    private final UserJpaRepository userRepository;
    private final IotDeviceJpaConverter deviceConverter;

    private static final int DEFAULT_ONLINE_THRESHOLD_MINUTES = 30;

    @Override
    @DataPermission(deptIdColumnName = "dept_id")
    public IPage<IotDeviceVO> getDevicePage(IotDevicePageQuery queryParams) {
        log.info("Getting device page with params: {}", queryParams);

        Pageable pageable = PageRequest.of(
                queryParams.getPageNum() - 1,
                queryParams.getPageSize(),
                Sort.by("createdAt").descending());

        Specification<IotDeviceJpa> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Apply basic query filters
            if (StrUtil.isNotBlank(queryParams.getKeywords())) {
                String keywords = "%" + queryParams.getKeywords() + "%";
                Predicate deviceNamePredicate = criteriaBuilder.like(root.get("deviceName"), keywords);
                Predicate deviceModelPredicate = criteriaBuilder.like(root.get("deviceModel"), keywords);
                Predicate locationPredicate = criteriaBuilder.like(root.get("location"), keywords);
                predicates.add(criteriaBuilder.or(deviceNamePredicate, deviceModelPredicate, locationPredicate));
            }

            if (StrUtil.isNotBlank(queryParams.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), queryParams.getStatus()));
            }

            if (queryParams.getDeptId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("deptId"), queryParams.getDeptId()));
            }

            if (queryParams.getDeptIds() != null && !queryParams.getDeptIds().isEmpty()) {
                predicates.add(root.get("deptId").in(queryParams.getDeptIds()));
            }

            if (StrUtil.isNotBlank(queryParams.getDeviceModel())) {
                predicates.add(criteriaBuilder.equal(root.get("deviceModel"), queryParams.getDeviceModel()));
            }

            // Online/offline filtering
            if (queryParams.getOnlineOnly() != null && queryParams.getOnlineOnly()) {
                LocalDateTime threshold = LocalDateTime.now().minusMinutes(DEFAULT_ONLINE_THRESHOLD_MINUTES);
                predicates.add(criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("lastSeen"), threshold),
                    criteriaBuilder.equal(root.get("status"), "active")
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        org.springframework.data.domain.Page<IotDeviceJpa> jpaPage = deviceRepository.findAll(spec, pageable);

        Page<IotDeviceVO> result = new Page<>(queryParams.getPageNum(), queryParams.getPageSize(),
                jpaPage.getTotalElements());

        List<IotDeviceVO> records = jpaPage.getContent().stream()
                .map(device -> {
                    String deptName = null;
                    String createdByUsername = null;

                    // Get department name
                    if (device.getDeptId() != null) {
                        deptName = deptRepository.findById(device.getDeptId())
                                .map(dept -> dept.getName())
                                .orElse("Unknown Department");
                    }

                    // Get creator username
                    if (device.getCreatedBy() != null) {
                        createdByUsername = userRepository.findById(device.getCreatedBy())
                                .map(user -> user.getUsername())
                                .orElse("Unknown User");
                    }

                    return deviceConverter.toEnhancedVo(device, deptName, createdByUsername, DEFAULT_ONLINE_THRESHOLD_MINUTES);
                })
                .collect(Collectors.toList());

        result.setRecords(records);
        return result;
    }

    @Override
    public IotDeviceForm getDeviceFormData(UUID deviceId) {
        IotDeviceJpa device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException("Device does not exist"));

        return deviceConverter.toForm(device);
    }

    @Override
    @Transactional
    public boolean saveDevice(IotDeviceForm deviceForm) {
        log.info("Saving device: {}", deviceForm.getDeviceName());

        // Check for duplicate device name in the same department
        if (deviceRepository.findByDeviceNameAndDeptId(deviceForm.getDeviceName(), deviceForm.getDeptId()).isPresent()) {
            throw new BusinessException("Device name already exists in this department");
        }

        // Verify department exists
        if (!deptRepository.existsById(deviceForm.getDeptId())) {
            throw new BusinessException("Department does not exist");
        }

        IotDeviceJpa device = deviceConverter.toEntity(deviceForm);
        device.setCreatedBy(SecurityUtils.getUserId());
        device.setCreatedAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());

        deviceRepository.save(device);
        log.info("Device saved successfully: {}", device.getDeviceId());
        return true;
    }

    @Override
    @Transactional
    public boolean updateDevice(UUID deviceId, IotDeviceForm deviceForm) {
        log.info("Updating device: {}", deviceId);

        IotDeviceJpa existingDevice = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException("Device does not exist"));

        // Check for duplicate device name in the same department (excluding current device)
        if (deviceRepository.existsByDeviceNameAndDeptIdAndDeviceIdNot(
                deviceForm.getDeviceName(), deviceForm.getDeptId(), deviceId)) {
            throw new BusinessException("Device name already exists in this department");
        }

        // Update fields
        existingDevice.setDeviceName(deviceForm.getDeviceName());
        existingDevice.setDeptId(deviceForm.getDeptId());
        existingDevice.setDeviceModel(deviceForm.getDeviceModel());
        existingDevice.setLatitude(deviceForm.getLatitude());
        existingDevice.setLongitude(deviceForm.getLongitude());
        existingDevice.setLocation(deviceForm.getLocation());
        existingDevice.setStatus(deviceForm.getStatus());
        existingDevice.setUpdatedAt(LocalDateTime.now());

        deviceRepository.save(existingDevice);
        log.info("Device updated successfully: {}", deviceId);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteDevices(String idsStr) {
        log.info("Deleting devices: {}", idsStr);

        List<UUID> deviceIds = Arrays.stream(idsStr.split(","))
                .map(String::trim)
                .map(UUID::fromString)
                .collect(Collectors.toList());

        List<IotDeviceJpa> devices = deviceRepository.findAllById(deviceIds);
        if (devices.size() != deviceIds.size()) {
            throw new BusinessException("Some devices do not exist");
        }

        // Soft delete by setting isDeleted = 1
        devices.forEach(device -> {
            device.setIsDeleted(1);
            device.setUpdatedAt(LocalDateTime.now());
        });

        deviceRepository.saveAll(devices);
        log.info("Devices deleted successfully: {}", deviceIds.size());
        return true;
    }

    @Override
    @DataPermission(deptIdColumnName = "dept_id")
    public List<IotDeviceVO> getDevicesByDepartment(Long deptId) {
        List<IotDeviceJpa> devices = deviceRepository.findByDeptId(deptId);
        return devices.stream()
                .map(device -> {
                    String deptName = deptRepository.findById(device.getDeptId())
                            .map(dept -> dept.getName())
                            .orElse("Unknown Department");
                    String createdByUsername = userRepository.findById(device.getCreatedBy())
                            .map(user -> user.getUsername())
                            .orElse("Unknown User");
                    return deviceConverter.toEnhancedVo(device, deptName, createdByUsername, DEFAULT_ONLINE_THRESHOLD_MINUTES);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<IotDeviceVO> getDevicesByStatus(String status) {
        List<IotDeviceJpa> devices = deviceRepository.findByStatus(status);
        return devices.stream()
                .map(device -> deviceConverter.toVo(device))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean updateDeviceStatus(UUID deviceId, String status) {
        IotDeviceJpa device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException("Device does not exist"));

        device.setStatus(status);
        device.setUpdatedAt(LocalDateTime.now());
        deviceRepository.save(device);

        log.info("Device status updated: {} -> {}", deviceId, status);
        return true;
    }

    @Override
    @Transactional
    public boolean updateDeviceLastSeen(UUID deviceId) {
        IotDeviceJpa device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new BusinessException("Device does not exist"));

        device.setLastSeen(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());
        deviceRepository.save(device);

        return true;
    }

    @Override
    public List<IotDeviceVO> getOnlineDevices(int minutesThreshold) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(minutesThreshold);
        List<IotDeviceJpa> devices = deviceRepository.findOnlineDevices(threshold);
        return devices.stream()
                .map(device -> deviceConverter.toVo(device))
                .collect(Collectors.toList());
    }

    @Override
    @DataPermission(deptIdColumnName = "dept_id")
    public List<IotDeviceVO> getOfflineDevicesInDept(Long deptId, int minutesThreshold) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(minutesThreshold);
        List<IotDeviceJpa> devices = deviceRepository.findOfflineDevicesInDept(deptId, threshold);
        return devices.stream()
                .map(device -> deviceConverter.toVo(device))
                .collect(Collectors.toList());
    }

    @Override
    public List<IotDeviceVO> getDevicesWithinRadius(Double centerLat, Double centerLng, Double radiusKm) {
        // Calculate approximate bounding box (simplified calculation)
        double latDelta = radiusKm / 111.0; // Approximate degrees latitude per km
        double lngDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(centerLat))); // Approximate degrees longitude per km
        
        double minLat = centerLat - latDelta;
        double maxLat = centerLat + latDelta;
        double minLng = centerLng - lngDelta;
        double maxLng = centerLng + lngDelta;
        
        List<IotDeviceJpa> devices = deviceRepository.findDevicesInBounds(minLat, maxLat, minLng, maxLng);
        return devices.stream()
                .map(device -> deviceConverter.toVo(device))
                .collect(Collectors.toList());
    }

    @Override
    public List<IotDeviceVO> getNearestDevices(Double centerLat, Double centerLng, Integer limit) {
        // For simplified implementation, get devices in a 10km radius and sort by simple distance
        List<IotDeviceVO> nearbyDevices = getDevicesWithinRadius(centerLat, centerLng, 10.0);
        return nearbyDevices.stream()
                .sorted((d1, d2) -> {
                    double dist1 = calculateSimpleDistance(centerLat, centerLng, d1.getLatitude(), d1.getLongitude());
                    double dist2 = calculateSimpleDistance(centerLat, centerLng, d2.getLatitude(), d2.getLongitude());
                    return Double.compare(dist1, dist2);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Calculate simple Euclidean distance between two points (approximation).
     */
    private double calculateSimpleDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
            return Double.MAX_VALUE;
        }
        double deltaLat = lat1 - lat2;
        double deltaLng = lng1 - lng2;
        return Math.sqrt(deltaLat * deltaLat + deltaLng * deltaLng);
    }

    @Override
    @DataPermission(deptIdColumnName = "dept_id")
    public DeviceStatsVO getDeviceStats(Long deptId) {
        long totalDevices = deviceRepository.countByDeptId(deptId);
        long activeDevices = deviceRepository.countActiveDevicesInDept(deptId);
        
        List<IotDeviceJpa> allDevices = deviceRepository.findByDeptId(deptId);
        long inactiveDevices = allDevices.stream()
                .filter(d -> "inactive".equals(d.getStatus()))
                .count();
        long disabledDevices = allDevices.stream()
                .filter(d -> "disabled".equals(d.getStatus()))
                .count();

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(DEFAULT_ONLINE_THRESHOLD_MINUTES);
        long onlineDevices = allDevices.stream()
                .filter(d -> "active".equals(d.getStatus()) && 
                           d.getLastSeen() != null && 
                           d.getLastSeen().isAfter(threshold))
                .count();
        long offlineDevices = activeDevices - onlineDevices;

        return new DeviceStatsVO(totalDevices, activeDevices, inactiveDevices, 
                               disabledDevices, onlineDevices, offlineDevices);
    }

}
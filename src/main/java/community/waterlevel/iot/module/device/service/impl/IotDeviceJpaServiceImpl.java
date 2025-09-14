package community.waterlevel.iot.module.device.service.impl;

import community.waterlevel.iot.module.device.model.entity.IotDeviceJpa;
import community.waterlevel.iot.module.device.model.form.IotDeviceForm;
import community.waterlevel.iot.module.device.model.vo.IotDeviceVO;
import community.waterlevel.iot.module.device.repository.IotDeviceJpaRepository;
import community.waterlevel.iot.module.device.converter.IotDeviceJpaConverter;
import community.waterlevel.iot.module.device.service.IotDeviceJpaService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class IotDeviceJpaServiceImpl implements IotDeviceJpaService {

    private final IotDeviceJpaRepository repository;
    private final IotDeviceJpaConverter converter;

    public IotDeviceJpaServiceImpl(IotDeviceJpaRepository repository, IotDeviceJpaConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public List<IotDeviceVO> getDevicesByDept(Long deptId) {
        List<IotDeviceJpa> list = repository.findByDeptId(deptId);
        List<IotDeviceVO> voList = new ArrayList<>();
        for (IotDeviceJpa e : list) voList.add(converter.toVo(e));
        return voList;
    }

    @Override
    public List<IotDeviceVO> getDevicesByStatus(String status) {
        List<IotDeviceJpa> list = repository.findByStatus(status);
        List<IotDeviceVO> voList = new ArrayList<>();
        for (IotDeviceJpa e : list) voList.add(converter.toVo(e));
        return voList;
    }

    @Override
    public boolean saveDevice(IotDeviceForm deviceForm) {
        IotDeviceJpa entity = converter.toEntity(deviceForm);
        if (entity.getDeviceId() == null) {
            entity.setDeviceId(UUID.randomUUID());
        }
        entity.setCreatedAt(LocalDateTime.now());
        repository.save(entity);
        return true;
    }

    @Override
    public boolean updateDevice(UUID deviceId, IotDeviceForm deviceForm) {
        Optional<IotDeviceJpa> opt = repository.findById(deviceId);
        if (opt.isEmpty()) return false;
        IotDeviceJpa exist = opt.get();
        exist.setDeviceName(deviceForm.getDeviceName());
        exist.setDeviceModel(deviceForm.getDeviceModel());
        exist.setLatitude(deviceForm.getLatitude());
        exist.setLongitude(deviceForm.getLongitude());
        exist.setLocation(deviceForm.getLocation());
        exist.setUpdatedAt(LocalDateTime.now());
        repository.save(exist);
        return true;
    }

    @Override
    public boolean deleteDevices(String ids) {
        String[] parts = ids.split(",");
        for (String p : parts) {
            try {
                UUID id = UUID.fromString(p.trim());
                repository.deleteById(id);
            } catch (Exception ignore) {
            }
        }
        return true;
    }

    @Override
    public boolean updateDeviceStatus(UUID deviceId, String status) {
        Optional<IotDeviceJpa> opt = repository.findById(deviceId);
        if (opt.isEmpty()) return false;
        IotDeviceJpa exist = opt.get();
        exist.setStatus(Enum.valueOf(community.waterlevel.iot.module.device.model.entity.DeviceStatus.class, status.toUpperCase()));
        exist.setUpdatedAt(LocalDateTime.now());
        repository.save(exist);
        return true;
    }

    @Override
    public boolean updateDeviceLastSeen(UUID deviceId) {
        Optional<IotDeviceJpa> opt = repository.findById(deviceId);
        if (opt.isEmpty()) return false;
        IotDeviceJpa exist = opt.get();
        exist.setLastSeen(LocalDateTime.now());
        repository.save(exist);
        return true;
    }

    @Override
    public List<IotDeviceVO> getDevicesWithinRadius(Double centerLat, Double centerLng, Double radiusKm) {
        // Simplified approach: use bounding box on lat/lng as a first pass
        double radiusDeg = radiusKm / 111.0; // approx degrees
        double minLat = centerLat - radiusDeg;
        double maxLat = centerLat + radiusDeg;
        double minLng = centerLng - radiusDeg;
        double maxLng = centerLng + radiusDeg;
        // Use JPA specification executor or repository custom query for bounding box.
        // For simplicity, use a stream filter (inefficient for large datasets) as placeholder.
        List<IotDeviceJpa> all = repository.findAll();
        List<IotDeviceJpa> filtered = new ArrayList<>();
        for (IotDeviceJpa d : all) {
            if (d.getLatitude() == null || d.getLongitude() == null) continue;
            if (d.getLatitude() >= minLat && d.getLatitude() <= maxLat && d.getLongitude() >= minLng && d.getLongitude() <= maxLng) {
                filtered.add(d);
            }
        }
        List<IotDeviceVO> voList = new ArrayList<>();
        for (IotDeviceJpa e : filtered) voList.add(converter.toVo(e));
        return voList;
    }

    @Override
    public List<IotDeviceVO> getNearestDevices(Double centerLat, Double centerLng, Integer limit) {
        List<IotDeviceJpa> all = repository.findAll();
        List<IotDeviceJpa> withLoc = new ArrayList<>();
        for (IotDeviceJpa d : all) {
            if (d.getLatitude() != null && d.getLongitude() != null) withLoc.add(d);
        }
        withLoc.sort(Comparator.comparingDouble(d -> haversine(centerLat, centerLng, d.getLatitude(), d.getLongitude())));
        List<IotDeviceJpa> sub = withLoc.subList(0, Math.min(limit, withLoc.size()));
        List<IotDeviceVO> voList = new ArrayList<>();
        for (IotDeviceJpa e : sub) voList.add(converter.toVo(e));
        return voList;
    }

    @Override
    @community.waterlevel.iot.common.annotation.DataPermission(deptIdColumnName = "deptId")
    public List<IotDeviceVO> listDevices(community.waterlevel.iot.module.device.model.query.IotDeviceQuery queryParams) {
        // Build a specification and delegate to the spec-aware method so the
        // DataPermissionAspect can find and wrap the Specification argument.
        org.springframework.data.jpa.domain.Specification<IotDeviceJpa> spec = (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> preds = new java.util.ArrayList<>();

            if (queryParams.getKeywords() != null && !queryParams.getKeywords().isEmpty()) {
                preds.add(cb.like(root.get("deviceName"), "%" + queryParams.getKeywords() + "%"));
            }

            if (queryParams.getStatus() != null && !queryParams.getStatus().isEmpty()) {
                preds.add(cb.equal(root.get("status"), queryParams.getStatus()));
            }

            if (queryParams.getDeptId() != null) {
                preds.add(cb.equal(root.get("deptId"), queryParams.getDeptId()));
            }

            return preds.isEmpty() ? null : cb.and(preds.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return listDevices(spec);
    }

    @Override
    @community.waterlevel.iot.common.annotation.DataPermission(deptIdColumnName = "deptId")
    public java.util.List<IotDeviceVO> listDevices(org.springframework.data.jpa.domain.Specification<IotDeviceJpa> specification) {
        java.util.List<IotDeviceJpa> list = repository.findAll(specification);
        java.util.List<IotDeviceVO> voList = new java.util.ArrayList<>();
        for (IotDeviceJpa e : list) voList.add(converter.toVo(e));
        return voList;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.sin(dLon/2)*Math.sin(dLon/2);
        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R*c;
    }
}

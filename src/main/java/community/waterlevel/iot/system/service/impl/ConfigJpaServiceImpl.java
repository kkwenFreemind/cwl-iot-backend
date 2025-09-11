package community.waterlevel.iot.system.service.impl;

import community.waterlevel.iot.common.constant.RedisConstants;
import community.waterlevel.iot.common.result.PageResult;
import community.waterlevel.iot.common.result.ResultCode;
import community.waterlevel.iot.core.security.util.SecurityUtils;
import community.waterlevel.iot.system.model.entity.ConfigJpa;
import community.waterlevel.iot.system.model.form.ConfigForm;
import community.waterlevel.iot.system.model.query.ConfigPageQuery;
import community.waterlevel.iot.system.model.vo.ConfigVO;
import community.waterlevel.iot.system.repository.ConfigJpaRepository;
import community.waterlevel.iot.system.service.ConfigJpaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the system configuration service interface using JPA.
 * <p>
 * Provides methods for managing system configuration records, including CRUD
 * operations,
 * pagination, cache refresh, and conversion between entity and form objects.
 * Integrates with Redis
 * for caching configuration data.
 * </p>
 *
 * @author Theo
 * @since 2024-07-29 11:17:26
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Service("configJpaService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ConfigJpaServiceImpl implements ConfigJpaService {

    private final ConfigJpaRepository configRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Initializes the system configuration cache after bean construction.
     */
    @PostConstruct
    public void init() {
        log.info("Initialize the system configuration cache (JPA implementation)...");
        refreshCache();
    }

    /**
     * Retrieves a paginated list of system configurations based on the provided
     * query parameters.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return a paginated result of configuration view objects
     */
    @Override
    public PageResult<ConfigVO> getConfigPage(ConfigPageQuery queryParams) {
        Specification<ConfigJpa> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(queryParams.getKeywords())) {
                String keyword = "%" + queryParams.getKeywords().toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("configName")), keyword);
                Predicate keyPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("configKey")), keyword);
                predicates.add(criteriaBuilder.or(namePredicate, keyPredicate));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(
                queryParams.getPageNum() - 1,
                queryParams.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createTime"));
        Page<ConfigJpa> configPage = configRepository.findAll(specification, pageable);
        List<ConfigVO> configVOList = configPage.getContent().stream()
                .map(config -> ConfigVO.builder()
                        .id(config.getId())
                        .configName(config.getConfigName())
                        .configKey(config.getConfigKey())
                        .configValue(config.getConfigValue())
                        .remark(config.getRemark())
                        .build())
                .toList();
        return createPageResult(configVOList, configPage.getTotalElements());
    }

    /**
     * Retrieves the configuration form data for a specific configuration ID.
     *
     * @param id the unique identifier of the configuration
     * @return the configuration form data
     */
    @Override
    public ConfigForm getConfigFormData(Long id) {
        ConfigJpa config = configRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuration does not exist"));
        return convertToForm(config);
    }

    /**
     * Saves a new system configuration using the provided form data.
     *
     * @param configForm the configuration form data
     * @return true if the operation was successful, false otherwise
     */
    @Override
    @Transactional
    public boolean save(ConfigForm configForm) {
        if (configRepository.existsByConfigKey(configForm.getConfigKey())) {
            throw new RuntimeException("Configuration key already exists");
        }
        ConfigJpa config = convertFromForm(configForm);
        config.setCreateBy(SecurityUtils.getUserId());
        ConfigJpa savedConfig = configRepository.save(config);
        return savedConfig.getId() != null;
    }

    /**
     * Updates an existing system configuration with the provided form data.
     *
     * @param id         the unique identifier of the configuration
     * @param configForm the configuration form data
     * @return true if the operation was successful, false otherwise
     */
    @Override
    @Transactional
    public boolean update(Long id, ConfigForm configForm) {
        ConfigJpa config = configRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuration does not exist"));
        if (configRepository.existsByConfigKeyAndIdNot(configForm.getConfigKey(), id)) {
            throw new RuntimeException("Configuration key already exists");
        }
        config.setConfigName(configForm.getConfigName());
        config.setConfigKey(configForm.getConfigKey());
        config.setConfigValue(configForm.getConfigValue());
        config.setRemark(configForm.getRemark());
        config.setUpdateBy(SecurityUtils.getUserId());
        configRepository.save(config);
        return true;
    }

    /**
     * Deletes a system configuration by its unique identifier.
     *
     * @param id the unique identifier of the configuration
     * @return true if the operation was successful, false otherwise
     */
    @Override
    @Transactional
    public boolean delete(Long id) {
        if (!configRepository.existsById(id)) {
            throw new RuntimeException("Configuration does not exist");
        }
        configRepository.deleteById(id);
        return true;
    }

    /**
     * Retrieves the value of a configuration by its configuration key.
     *
     * @param configKey the unique configuration key
     * @return the configuration value, or null if not found
     */
    @Override
    public String getConfigValue(String configKey) {
        return configRepository.findByConfigKey(configKey)
                .map(ConfigJpa::getConfigValue)
                .orElse(null);
    }

    /**
     * Creates a paginated result object for configuration view objects.
     *
     * @param list  the list of configuration view objects
     * @param total the total number of records
     * @return a paginated result object
     */
    private PageResult<ConfigVO> createPageResult(List<ConfigVO> list, long total) {
        PageResult<ConfigVO> result = new PageResult<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        PageResult.Data<ConfigVO> data = new PageResult.Data<>();
        data.setList(list);
        data.setTotal(total);
        result.setData(data);
        result.setMsg(ResultCode.SUCCESS.getMsg());
        return result;
    }

    /**
     * Converts a configuration entity to a configuration form object.
     *
     * @param config the configuration entity
     * @return the configuration form object
     */
    private ConfigForm convertToForm(ConfigJpa config) {
        ConfigForm form = new ConfigForm();
        form.setId(config.getId());
        form.setConfigName(config.getConfigName());
        form.setConfigKey(config.getConfigKey());
        form.setConfigValue(config.getConfigValue());
        form.setRemark(config.getRemark());
        return form;
    }

    /**
     * Converts a configuration form object to a configuration entity.
     *
     * @param form the configuration form object
     * @return the configuration entity
     */
    private ConfigJpa convertFromForm(ConfigForm form) {
        ConfigJpa config = new ConfigJpa();
        config.setId(form.getId());
        config.setConfigName(form.getConfigName());
        config.setConfigKey(form.getConfigKey());
        config.setConfigValue(form.getConfigValue());
        config.setRemark(form.getRemark());
        return config;
    }

    /**
     * Refreshes the system configuration cache in Redis.
     *
     * @return true if the cache was refreshed successfully, false otherwise
     */
    @Override
    public boolean refreshCache() {
        try {
            redisTemplate.delete(RedisConstants.System.CONFIG);
            List<ConfigJpa> list = configRepository.findAllActive();
            if (list != null && !list.isEmpty()) {
                Map<String, String> map = list.stream()
                        .collect(Collectors.toMap(ConfigJpa::getConfigKey, ConfigJpa::getConfigValue));
                redisTemplate.opsForHash().putAll(RedisConstants.System.CONFIG, map);
                return true;
            }
        } catch (Exception e) {
            log.error("Failed to refresh the system configuration cache", e);
        }
        return false;
    }

}

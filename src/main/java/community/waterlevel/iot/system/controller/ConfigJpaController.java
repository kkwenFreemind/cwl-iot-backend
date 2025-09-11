package community.waterlevel.iot.system.controller;

import community.waterlevel.iot.common.result.PageResult;
import community.waterlevel.iot.common.result.Result;
import community.waterlevel.iot.system.model.form.ConfigForm;
import community.waterlevel.iot.system.model.query.ConfigPageQuery;
import community.waterlevel.iot.system.model.vo.ConfigVO;
import community.waterlevel.iot.system.service.ConfigJpaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * ConfigController is a REST controller that provides endpoints for managing
 * system configuration settings.
 * <p>
 * It exposes APIs for listing, creating, updating, deleting, and refreshing
 * configuration items, supporting pagination and permission-based access
 * control. The controller delegates business logic to the {@link ConfigJpaService}
 * and returns standardized API responses.
 * <p>
 * Designed for use in the IoT backend's administration interface to manage
 * application settings dynamically.
 *
 * @author Theo
 * @since 2024-07-30 11:25
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@RestController
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
@Tag(name = "08.Config Controller")
public class ConfigJpaController {

    /**
     * Service for system configuration JPA operations
     */
    private final ConfigJpaService configJpaService;

    /**
     * Get paginated list of configuration items.
     *
     * @param queryParams Query parameters for pagination and filtering
     * @return Paginated result of configuration value objects
     */
    @Operation(summary = "Configuring a paginated list")
    @GetMapping("/page")
    public PageResult<ConfigVO> getConfigPage(ConfigPageQuery queryParams) {
        return configJpaService.getConfigPage(queryParams);
    }

    /**
     * Get details of a specific configuration item by ID.
     *
     * @param id Configuration ID
     * @return Result containing configuration form data
     */
    @Operation(summary = "Configuration details")
    @GetMapping("/{id}")
    public Result<ConfigForm> getConfig(
            @Parameter(description = "Configuration ID") @PathVariable Long id) {
        ConfigForm configForm = configJpaService.getConfigFormData(id);
        return Result.success(configForm);
    }

    /**
     * Get configuration form data by ID.
     *
     * @param id Configuration ID
     * @return Result containing configuration form data
     */
    @Operation(summary = "Get configuration form information")
    @GetMapping("/{id}/form")
    public Result<ConfigForm> getConfigForm(
            @Parameter(description = "Configuration ID") @PathVariable Long id) {
        ConfigForm configForm = configJpaService.getConfigFormData(id);
        return Result.success(configForm);
    }

    /**
     * Create a new configuration item.
     *
     * @param configForm Configuration form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Added configuration")
    @PostMapping
    public Result<Void> saveConfig(@RequestBody ConfigForm configForm) {
        boolean result = configJpaService.save(configForm);
        return Result.judge(result);
    }

    /**
     * Update an existing configuration item by ID.
     *
     * @param id         Configuration ID
     * @param configForm Configuration form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Edit configuration")
    @PutMapping("/{id}")
    public Result<Void> updateConfig(
            @Parameter(description = "Configuration ID") @PathVariable Long id,
            @RequestBody ConfigForm configForm) {
        boolean result = configJpaService.update(id, configForm);
        return Result.judge(result);
    }

    /**
     * Delete a configuration item by ID.
     *
     * @param id Configuration ID
     * @return Result indicating success or failure
     */
    @Operation(summary = "Deleting a Configuration")
    @DeleteMapping("/{id}")
    public Result<Void> deleteConfig(
            @Parameter(description = "Configuration ID") @PathVariable Long id) {
        boolean result = configJpaService.delete(id);
        return Result.judge(result);
    }

    /**
     * Get configuration value by key.
     *
     * @param configKey Configuration key
     * @return Result containing the configuration value
     */
    @Operation(summary = "Get configuration value by key")
    @GetMapping("/value/{configKey}")
    public Result<String> getConfigValue(
            @Parameter(description = "Configuration key") @PathVariable String configKey) {
        String configValue = configJpaService.getConfigValue(configKey);
        return Result.success(configValue);
    }

    /**
     * Refresh the system configuration cache.
     *
     * @return Result indicating success or failure
     */
    @Operation(summary = "Refresh system configuration cache")
    @PutMapping("/refresh")
    public Result<Void> refreshCache() {
        boolean result = configJpaService.refreshCache();
        return Result.judge(result);
    }
}
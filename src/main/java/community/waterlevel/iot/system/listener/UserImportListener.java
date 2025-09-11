package community.waterlevel.iot.system.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import community.waterlevel.iot.common.constant.SystemConstants;
import community.waterlevel.iot.common.enums.StatusEnum;
import community.waterlevel.iot.common.result.ExcelResult;
import community.waterlevel.iot.system.converter.UserJpaConverter;
import community.waterlevel.iot.system.enums.DictCodeEnum;
import community.waterlevel.iot.system.model.dto.UserImportDTO;

import community.waterlevel.iot.system.service.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import community.waterlevel.iot.system.model.entity.DeptJpa;
import community.waterlevel.iot.system.model.entity.DictItemJpa;
import community.waterlevel.iot.system.model.entity.RoleJpa;
import community.waterlevel.iot.system.model.entity.UserJpa;
import community.waterlevel.iot.system.model.entity.UserRoleJpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserImportListener is an EasyExcel event listener for handling user import
 * operations from Excel files.
 * <p>
 * This listener validates, processes, and persists each row of user data,
 * collecting import results and error messages. It leverages Spring beans for
 * user, role, and department management, and supports batch role assignment and
 * data validation.
 * <p>
 * Designed for use with the EasyExcel library to enable efficient and robust
 * user data import in the IoT backend.
 *
 * @author Ray
 * @since 2022/4/10
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Slf4j
public class UserImportListener extends AnalysisEventListener<UserImportDTO> {

    /**
     * Excel import result container for tracking processing statistics.
     * Accumulates valid/invalid record counts and error messages during import.
     */
    @Getter
    private final ExcelResult excelResult;

    /**
     * JPA service for user entity operations and validation queries.
     */
    private final UserJpaService userService;

    /**
     * Spring Security password encoder for secure password hashing.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * MapStruct converter for DTO to entity transformation.
     */
    private final UserJpaConverter userConverter;

    /**
     * JPA service for managing user-role associations.
     */
    private final UserRoleJpaService userRoleService;

    /**
     * Pre-loaded active role list for efficient role code resolution.
     * Cached during initialization to avoid repeated database queries.
     */
    private final List<RoleJpa> roleList;

    /**
     * Pre-loaded department list for efficient department code resolution.
     * Cached during initialization to avoid repeated database queries.
     */
    private final List<DeptJpa> deptList;

    /**
     * Pre-loaded gender dictionary items for label-to-value mapping.
     * Cached during initialization for efficient gender validation.
     */
    private final List<DictItemJpa> genderList;

    /**
     * Current Excel row number being processed.
     * Used for error message context and progress tracking.
     */
    private Integer currentRow = 1;

    /**
     * Constructor for user import listener with dependency injection and data
     * pre-loading.
     * Initializes all required services and caches reference data to optimize
     * per-row processing performance by avoiding repeated database queries.
     * 
     * <p>
     * Pre-loads active roles, departments, and gender dictionary items
     * for efficient lookup during data validation and transformation.
     * </p>
     */
    public UserImportListener() {
        this.userService = SpringUtil.getBean(UserJpaService.class);
        this.passwordEncoder = SpringUtil.getBean(PasswordEncoder.class);
        this.userRoleService = SpringUtil.getBean(UserRoleJpaService.class);
        this.userConverter = SpringUtil.getBean(UserJpaConverter.class);
        this.roleList = SpringUtil.getBean(RoleJpaService.class)
                .list(new LambdaQueryWrapper<RoleJpa>().eq(RoleJpa::getStatus, StatusEnum.ENABLE.getValue())
                        .select(RoleJpa::getId, RoleJpa::getCode));
        this.deptList = SpringUtil.getBean(DeptJpaService.class)
                .list(new LambdaQueryWrapper<DeptJpa>().select(DeptJpa::getId, DeptJpa::getCode));
        this.genderList = SpringUtil.getBean(SystemDictItemJpaService.class)
                .getDictItems(DictCodeEnum.GENDER.getValue()).stream()
                .map(item -> {
                    DictItemJpa dictItem = new DictItemJpa();
                    dictItem.setValue(item.getValue());
                    dictItem.setLabel(item.getLabel());
                    return dictItem;
                }).collect(Collectors.toList());
        this.excelResult = new ExcelResult();
    }

    /**
     * Processes each parsed Excel row with comprehensive validation and
     * persistence.
     * Performs multi-level data validation including field presence, format
     * validation,
     * and business rule checks, followed by entity transformation and database
     * persistence
     * with role assignment and error tracking.
     * 
     * <p>
     * Processing workflow:
     * 1. Comprehensive field validation (required fields, format validation)
     * 2. Business rule validation (username uniqueness, mobile format)
     * 3. Entity transformation and relationship resolution
     * 4. Database persistence with user-role association
     * 5. Result tracking and error reporting
     * </p>
     *
     * @param userImportDTO   parsed row data similar to
     *                        {@link AnalysisContext#readRowHolder()}
     * @param analysisContext EasyExcel analysis context for additional processing
     *                        info
     */
    @Override
    public void invoke(UserImportDTO userImportDTO, AnalysisContext analysisContext) {
        log.info("Parsed to a user profile:{}", JSONUtil.toJsonStr(userImportDTO));

        boolean validation = true;
        String errorMsg = "Validation failed at row " + currentRow + ":";
        String username = userImportDTO.getUsername();
        if (StrUtil.isBlank(username)) {
            errorMsg += "User name is empty;";
            validation = false;
        } else {
            long count = userService.count(new LambdaQueryWrapper<UserJpa>().eq(UserJpa::getUsername, username));
            if (count > 0) {
                errorMsg += "The user name already exists;";
                validation = false;
            }
        }

        String nickname = userImportDTO.getNickname();
        if (StrUtil.isBlank(nickname)) {
            errorMsg += "The user nickname is empty;";
            validation = false;
        }

        String mobile = userImportDTO.getMobile();
        if (StrUtil.isBlank(mobile)) {
            errorMsg += "The mobile phone number is empty;";
            validation = false;
        } else {
            if (!Validator.isMobile(mobile)) {
                errorMsg += "Incorrect mobile phone number;";
                validation = false;
            }
        }

        if (validation) {
            UserJpa entity = userConverter.toEntity(userImportDTO);
            entity.setPassword(passwordEncoder.encode(SystemConstants.DEFAULT_PASSWORD)); 
            String genderLabel = userImportDTO.getGenderLabel();
            entity.setGender(getGenderValue(genderLabel));
            String roleCodes = userImportDTO.getRoleCodes();
            List<Long> roleIds = getRoleIds(roleCodes);
            String deptCode = userImportDTO.getDeptCode();
            entity.setDeptId(getDeptId(deptCode));

            boolean saveResult = userService.save(entity);
            if (saveResult) {
                excelResult.setValidCount(excelResult.getValidCount() + 1);
                if (CollectionUtil.isNotEmpty(roleIds)) {
                    List<UserRoleJpa> userRoles = roleIds.stream()
                            .map(roleId -> new UserRoleJpa(entity.getId(), roleId))
                            .collect(Collectors.toList());
                    userRoleService.saveBatch(userRoles);
                }
            } else {
                excelResult.setInvalidCount(excelResult.getInvalidCount() + 1);
                errorMsg += "Failed to save data at row " + currentRow + ".";
                excelResult.getMessageList().add(errorMsg);
            }
        } else {
            excelResult.setInvalidCount(excelResult.getInvalidCount() + 1);
            excelResult.getMessageList().add(errorMsg);
        }
        currentRow++;
    }

    /**
     * Resolves role IDs from comma-separated role codes.
     * Parses the role codes string and maps each code to its corresponding
     * role ID using the pre-loaded role cache, with deduplication.
     *
     * @param roleCodes comma-separated role codes from Excel input
     * @return list of unique role IDs, or empty list if no valid codes found
     */
    private List<Long> getRoleIds(String roleCodes) {
        if (StrUtil.isNotBlank(roleCodes)) {
            String[] split = roleCodes.split(",");
            if (split.length > 0) {
                List<Long> roleIds = new ArrayList<>();
                for (String roleCode : split) {
                    this.roleList.stream().filter(r -> r.getCode().equals(roleCode))
                            .findFirst().ifPresent(role -> roleIds.add(role.getId()));
                }
                return roleIds.stream().distinct().toList();
            }
        }
        return Collections.emptyList();
    }

    /**
     * Resolves department ID from department code.
     * Maps the department code to its corresponding ID using the
     * pre-loaded department cache for efficient lookup.
     *
     * @param deptCode department code from Excel input
     * @return department ID if found, null otherwise
     */
    private Long getDeptId(String deptCode) {
        if (StrUtil.isNotBlank(deptCode)) {
            return this.deptList.stream().filter(r -> r.getCode().equals(deptCode))
                    .findFirst().map(DeptJpa::getId).orElse(null);
        }
        return null;
    }

    /**
     * Converts gender label to numeric gender value.
     * Maps human-readable gender label to its corresponding dictionary value
     * using the pre-loaded gender dictionary cache.
     *
     * @param genderLabel gender label from Excel input (e.g., "男", "女")
     * @return numeric gender value if found, null otherwise
     */
    private Integer getGenderValue(String genderLabel) {
        if (StrUtil.isNotBlank(genderLabel)) {
            return this.genderList.stream()
                    .filter(r -> r.getLabel().equals(genderLabel))
                    .findFirst()
                    .map(DictItemJpa::getValue)
                    .map(Convert::toInt)
                    .orElse(null);
        }
        return null;
    }

    /**
     * Callback method invoked after all Excel data has been processed.
     * Provides a hook for cleanup operations and final processing summary.
     * Logs completion status for monitoring and debugging purposes.
     *
     * @param analysisContext EasyExcel analysis context with processing metadata
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("All data analysis completed!");
    }

}

package community.waterlevel.iot.system.converter;

import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.system.model.entity.UserJpa;
import community.waterlevel.iot.system.model.dto.CurrentUserDTO;
import community.waterlevel.iot.system.model.vo.UserPageVO;
import community.waterlevel.iot.system.model.vo.UserProfileVO;
import community.waterlevel.iot.system.model.bo.UserBO;
import community.waterlevel.iot.system.model.form.UserForm;
import community.waterlevel.iot.system.model.dto.UserImportDTO;
import community.waterlevel.iot.system.model.form.UserProfileForm;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * UserJpaConverter is a MapStruct converter interface for transforming
 * user-related objects between entity, form, DTO, and view representations.
 * <p>
 * It defines methods for converting between {@link UserJpa}, {@link UserForm},
 * {@link UserProfileForm}, {@link UserPageVO}, {@link CurrentUserDTO},
 * {@link UserImportDTO}, and {@link Option}, supporting the mapping of data
 * across different layers of the application.
 * <p>
 * Used to simplify and standardize object transformations in the user
 * management module.
 *
 * @author Ray.Hao
 * @since 2022/6/8
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Mapper(componentModel = "spring")
public interface UserJpaConverter {

        /**
         * Converts a UserJpa entity to a UserPageVO.
         * Ignores deptName and roleNames fields which are handled separately by the
         * service layer.
         *
         * @param entity the user entity
         * @return the user page view object
         */
        @Mappings({
                        @Mapping(target = "deptName", ignore = true),
                        @Mapping(target = "roleNames", ignore = true)
        })
        UserPageVO toPageVO(UserJpa entity);

        /**
         * Converts a list of UserJpa entities to a list of UserPageVO objects.
         *
         * @param entities the list of user entities
         * @return the list of user page view objects
         */
        List<UserPageVO> toPageVOList(List<UserJpa> entities);

        /**
         * Converts a UserJpa entity to a UserForm.
         * Ignores roleIds field and maps openid to openId.
         *
         * @param entity the user entity
         * @return the user form data
         */
        @Mappings({
                        @Mapping(target = "roleIds", ignore = true),
                        @Mapping(target = "openId", source = "openid")
        })
        UserForm toForm(UserJpa entity);

        /**
         * Converts a UserForm to a UserJpa entity.
         * Inherits inverse configuration from toForm method and ignores audit fields
         * and password.
         *
         * @param form the user form data
         * @return the user entity
         */
        @InheritInverseConfiguration(name = "toForm")
        @Mappings({
                        @Mapping(target = "createTime", ignore = true),
                        @Mapping(target = "updateTime", ignore = true),
                        @Mapping(target = "createBy", ignore = true),
                        @Mapping(target = "updateBy", ignore = true),
                        @Mapping(target = "isDeleted", ignore = true),
                        @Mapping(target = "openid", source = "openId"),
                        @Mapping(target = "password", ignore = true)
        })
        UserJpa toEntity(UserForm form);

        /**
         * Converts a UserJpa entity to a CurrentUserDTO.
         * Maps user ID to userId and ignores roles and permissions fields.
         *
         * @param entity the user entity
         * @return the current user DTO
         */
        @Mappings({
                        @Mapping(target = "userId", source = "id"),
                        @Mapping(target = "roles", ignore = true),
                        @Mapping(target = "perms", ignore = true)
        })
        CurrentUserDTO toCurrentUserDTO(UserJpa entity);

        /**
         * Converts a UserImportDTO to a UserJpa entity for user import functionality.
         * Ignores all system-managed fields during import process.
         *
         * @param dto the user import DTO
         * @return the user entity
         */
        @Mappings({
                        @Mapping(target = "id", ignore = true),
                        @Mapping(target = "createTime", ignore = true),
                        @Mapping(target = "updateTime", ignore = true),
                        @Mapping(target = "createBy", ignore = true),
                        @Mapping(target = "updateBy", ignore = true),
                        @Mapping(target = "isDeleted", ignore = true),
                        @Mapping(target = "password", ignore = true),
                        @Mapping(target = "openid", ignore = true),
                        @Mapping(target = "avatar", ignore = true),
                        @Mapping(target = "deptId", ignore = true),
                        @Mapping(target = "gender", ignore = true),
                        @Mapping(target = "status", ignore = true)
        })
        UserJpa toEntity(UserImportDTO dto);

        /**
         * Converts a UserJpa entity to a UserBO (Business Object).
         * Ignores deptName and roleNames fields which are handled separately.
         *
         * @param entity the user entity
         * @return the user business object
         */
        @Mappings({
                        @Mapping(target = "deptName", ignore = true),
                        @Mapping(target = "roleNames", ignore = true)
        })
        UserBO toBO(UserJpa entity);

        /**
         * Converts a UserBO to a UserProfileVO for user profile display.
         *
         * @param bo the user business object
         * @return the user profile view object
         */
        UserProfileVO toProfileVO(UserBO bo);

        /**
         * Converts a UserProfileForm to a UserJpa entity for profile updates.
         * Ignores system-managed fields and security-related fields during profile
         * update.
         *
         * @param form the user profile form data
         * @return the user entity
         */
        @Mappings({
                        @Mapping(target = "id", ignore = true),
                        @Mapping(target = "username", ignore = true),
                        @Mapping(target = "password", ignore = true),
                        @Mapping(target = "createTime", ignore = true),
                        @Mapping(target = "updateTime", ignore = true),
                        @Mapping(target = "createBy", ignore = true),
                        @Mapping(target = "updateBy", ignore = true),
                        @Mapping(target = "isDeleted", ignore = true),
                        @Mapping(target = "status", ignore = true),
                        @Mapping(target = "openid", ignore = true),
                        @Mapping(target = "deptId", ignore = true)
        })
        UserJpa toEntity(UserProfileForm form);

        /**
         * Converts a UserJpa entity to an Option object for dropdown selection.
         * Maps user nickname to 'label' and user ID to 'value', ignoring children and
         * tag properties.
         *
         * @param entity the user entity
         * @return Option containing the user ID as value and nickname as label
         */
        @Mappings({
                        @Mapping(target = "label", source = "nickname"),
                        @Mapping(target = "value", source = "id"),
                        @Mapping(target = "children", ignore = true),
                        @Mapping(target = "tag", ignore = true)
        })
        Option<Long> toOption(UserJpa entity);

        /**
         * Converts a list of UserJpa entities to a list of Option objects for dropdown
         * selection.
         *
         * @param entities the list of user entities
         * @return the list of user options
         */
        List<Option<Long>> toOptions(List<UserJpa> entities);
}

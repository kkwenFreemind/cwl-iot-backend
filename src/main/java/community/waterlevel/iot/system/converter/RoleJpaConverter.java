package community.waterlevel.iot.system.converter;

import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.system.model.entity.RoleJpa;
import community.waterlevel.iot.system.model.form.RoleForm;
import community.waterlevel.iot.system.model.vo.RolePageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.data.domain.Page;
import java.util.List;
/**
 * RoleJpaConverter is a MapStruct converter interface for transforming
 * role-related objects between entity, form, option, and view representations.
 * <p>
 * It defines methods for converting between {@link RoleJpa}, {@link RoleForm},
 * {@link RolePageVO}, and {@link Option}, supporting the mapping of data across
 * different layers of the application.
 * <p>
 * Used to simplify and standardize object transformations in the role
 * management module.
 *
 * @author haoxr
 * @since 2022/5/29
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Mapper(componentModel = "spring")
public interface RoleJpaConverter {

    /**
     * Converts a JPA Page of RoleJpa entities to a Page of RolePageVO objects.
     *
     * @param jpaPage the paginated role entities
     * @return the paginated role page view objects
     */
    default Page<RolePageVO> toPageVo(Page<RoleJpa> jpaPage) {
        return jpaPage.map(this::toPageVO);
    }

    /**
     * Converts a RoleJpa entity to a RolePageVO.
     *
     * @param roleJpa the role entity
     * @return the role page view object
     */
    RolePageVO toPageVO(RoleJpa roleJpa);

    /**
     * Converts a RoleJpa entity to an Option object for dropdown selection.
     * Maps the role ID to 'value' and role name to 'label', ignoring children and
     * tag properties.
     *
     * @param roleJpa the role entity
     * @return Option containing the role ID as value and name as label
     */
    @Mappings({
            @Mapping(target = "value", source = "id"),
            @Mapping(target = "label", source = "name"),
            @Mapping(target = "children", ignore = true),
            @Mapping(target = "tag", ignore = true)
    })
    Option<Long> toOption(RoleJpa roleJpa);

    /**
     * Converts a list of RoleJpa entities to a list of Option objects for dropdown
     * selection.
     *
     * @param roles the list of role entities
     * @return the list of role options
     */
    List<Option<Long>> toOptions(List<RoleJpa> roles);

    /**
     * Converts a RoleForm to a RoleJpa entity.
     * Ignores audit fields (createTime, updateTime, createBy, updateBy) and
     * isDeleted during mapping.
     *
     * @param roleForm the role form data
     * @return the role entity
     */
    @Mappings({
            @Mapping(target = "createTime", ignore = true),
            @Mapping(target = "updateTime", ignore = true),
            @Mapping(target = "createBy", ignore = true),
            @Mapping(target = "updateBy", ignore = true),
            @Mapping(target = "isDeleted", ignore = true)
    })
    RoleJpa toEntity(RoleForm roleForm);

    /**
     * Converts a RoleJpa entity to a RoleForm.
     *
     * @param roleJpa the role entity
     * @return the role form data
     */
    RoleForm toForm(RoleJpa roleJpa);
}

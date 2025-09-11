package community.waterlevel.iot.system.converter;

import community.waterlevel.iot.system.model.entity.MenuJpa;
import community.waterlevel.iot.system.model.form.MenuForm;
import community.waterlevel.iot.system.model.vo.MenuVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MenuJpaConverter is a MapStruct converter interface for transforming
 * menu-related objects between entity, form, and view representations.
 * <p>
 * It defines methods for converting between {@link MenuJpa}, {@link MenuForm}, and
 * {@link MenuVO}, supporting the mapping of data across different layers of the
 * application.
 * <p>
 * Used to simplify and standardize object transformations in the menu
 * management module.
 *
 * @author Ray Hao
 * @since 2024/5/26
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Mapper(componentModel = "spring")
public interface MenuJpaConverter {

 /**
     * Converts a MenuJpa entity to a MenuVO (View Object).
     * Ignores the 'children' property during mapping.
     *
     * @param entity the menu entity
     * @return the menu view object
     */
    @Mapping(target = "children", ignore = true)
    MenuVO toVo(MenuJpa entity);

    /**
     * Converts a MenuJpa entity to a MenuForm.
     * Ignores the 'params' field, which is handled separately by the service layer.
     *
     * @param entity the menu entity
     * @return the menu form data
     */
    @Mapping(target = "params", ignore = true)
    MenuForm toForm(MenuJpa entity);

    /**
     * Converts a MenuForm to a MenuJpa entity.
     * Ignores params, createTime, updateTime, and treePath fields which are handled
     * separately.
     *
     * @param menuForm the menu form data
     * @return the menu entity
     */
    @Mapping(target = "params", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "treePath", ignore = true)
    MenuJpa toEntity(MenuForm menuForm);

    /**
     * Converts MyBatis Menu to MenuJpa entity (used during migration process).
     *
     * @param menu the MyBatis menu object
     * @return the JPA menu entity
     */
    MenuJpa fromMenu(MenuJpa menu);

    /**
     * Converts MenuJpa entity to MyBatis Menu (for backward compatibility).
     *
     * @param menuJpa the JPA menu entity
     * @return the MyBatis menu object
     */
    MenuJpa toMenu(MenuJpa menuJpa);
}

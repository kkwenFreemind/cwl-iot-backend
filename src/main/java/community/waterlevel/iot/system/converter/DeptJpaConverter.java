package community.waterlevel.iot.system.converter;

import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.system.model.entity.DeptJpa;
import community.waterlevel.iot.system.model.form.DeptForm;
import community.waterlevel.iot.system.model.vo.DeptVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * DeptJpaConverter is a MapStruct converter interface for transforming
 * department-related objects between entity, form, and view representations.
 * <p>
 * It defines methods for converting between {@link DeptJpa}, {@link DeptForm}, and
 * {@link DeptVO}, supporting the mapping of data across different layers of the
 * application.
 * <p>
 * Used to simplify and standardize object transformations in the department
 * management module.
 *
 * @author haoxr
 * @since 2022/7/29
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Mapper(componentModel = "spring")
public interface DeptJpaConverter {

    /**
     * Converts a DeptJpa entity to a DeptVO (View Object).
     * Ignores the 'children' property during mapping.
     * The centerGeom field is automatically excluded as it's not present in DeptVO.
     *
     * @param entity the department entity
     * @return the department view object
     */
    @Mapping(target = "children", ignore = true)
    DeptVO toVo(DeptJpa entity);

    /**
     * Converts a DeptForm to a DeptJpa entity.
     * Ignores id, createBy, createTime, updateBy, updateTime, isDeleted, treePath,
     * and centerGeom fields during mapping.
     * The centerGeom field is automatically calculated by the database trigger.
     *
     * @param form the department form data
     * @return the department entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "treePath", ignore = true)
    @Mapping(target = "centerGeom", ignore = true)
    DeptJpa toEntity(DeptForm form);

    /**
     * Converts a DeptJpa entity to a DeptForm.
     *
     * @param entity the department entity
     * @return the department form data
     */
    DeptForm toForm(DeptJpa entity);

    /**
     * Converts a DeptJpa entity to an Option object for dropdown selection.
     *
     * @param entity the department entity
     * @return Option containing the department ID and name, or null if entity is
     *         null
     */
    default Option<Long> toOption(DeptJpa entity) {
        if (entity == null) {
            return null;
        }
        return new Option<>(entity.getId(), entity.getName());
    }
}

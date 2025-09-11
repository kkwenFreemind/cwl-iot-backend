package community.waterlevel.iot.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.system.model.entity.DictItemJpa;
import community.waterlevel.iot.system.model.form.DictItemForm;
import community.waterlevel.iot.system.model.vo.DictPageVO;

import org.mapstruct.Mapper;

import java.util.List;

/**
 * DictItemJpaConverter is a MapStruct converter interface for transforming
 * dictionary item-related objects between entity, form, and view
 * representations.
 * <p>
 * It defines methods for converting between {@link DictItemJpa},
 * {@link DictItemForm}, {@link DictPageVO}, and {@link Option}, supporting the
 * mapping of data across different layers of the application.
 * <p>
 * Used to simplify and standardize object transformations in the dictionary
 * management module.
 *
 * @author Ray.Hao
 * @since 2022/6/8
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Mapper(componentModel = "spring")
public interface DictItemJpaConverter {

    /**
     * Converts a Page of DictItemJpa entities to a Page of DictPageVO objects.
     *
     * @param page the paginated dictionary item entities
     * @return the paginated dictionary page view objects
     */
    Page<DictPageVO> toPageVo(Page<DictItemJpa> page);

    /**
     * Converts a DictItemJpa entity to a DictItemForm.
     *
     * @param entity the dictionary item entity
     * @return the dictionary item form data
     */
    DictItemForm toForm(DictItemJpa entity);

    /**
     * Converts a DictItemForm to a DictItemJpa entity.
     *
     * @param formData the dictionary item form data
     * @return the dictionary item entity
     */
    DictItemJpa toEntity(DictItemForm formData);

    /**
     * Converts a single DictItemJpa entity to an Option object for dropdown
     * selection.
     *
     * @param dictItem the dictionary item entity
     * @return Option containing the dictionary item ID and value
     */
    Option<Long> toOption(DictItemJpa dictItem);

    /**
     * Converts a list of DictItemJpa entities to a list of Option objects for
     * dropdown selection.
     *
     * @param dictData the list of dictionary item entities
     * @return the list of dictionary item options
     */
    List<Option<Long>> toOption(List<DictItemJpa> dictData);
}

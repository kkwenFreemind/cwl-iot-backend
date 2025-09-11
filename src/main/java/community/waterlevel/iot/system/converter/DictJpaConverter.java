package community.waterlevel.iot.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import community.waterlevel.iot.system.model.vo.DictPageVO;
import community.waterlevel.iot.system.model.entity.DictJpa;
import community.waterlevel.iot.system.model.form.DictForm;
import org.mapstruct.Mapper;

/**
 * DictJpaConverter is a MapStruct converter interface for transforming
 * dictionary-related objects between entity, form, and view representations.
 * <p>
 * It defines methods for converting between {@link DictJpa}, {@link DictForm}, and
 * {@link DictPageVO}, supporting the mapping of data across different layers of
 * the application.
 * <p>
 * Used to simplify and standardize object transformations in the dictionary
 * management module.
 *
 * @author Ray Hao
 * @since 2022/6/8
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Mapper(componentModel = "spring")
public interface DictJpaConverter {

    /**
     * Converts a Page of DictJpa entities to a Page of DictPageVO objects.
     *
     * @param page the paginated dictionary entities
     * @return the paginated dictionary page view objects
     */
    Page<DictPageVO> toPageVo(Page<DictJpa> page);

    /**
     * Converts a DictJpa entity to a DictForm.
     *
     * @param entity the dictionary entity
     * @return the dictionary form data
     */
    DictForm toForm(DictJpa entity);

    /**
     * Converts a DictForm to a DictJpa entity.
     *
     * @param entity the dictionary form data
     * @return the dictionary entity
     */
    DictJpa toEntity(DictForm entity);
}
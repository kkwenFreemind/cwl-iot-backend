package community.waterlevel.iot.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.system.model.entity.DictItem;
import community.waterlevel.iot.system.model.form.DictItemForm;
import community.waterlevel.iot.system.model.vo.DictPageVO;

import org.mapstruct.Mapper;

import java.util.List;

/**
 * 字典项对象转换器
 *
 * @author Ray.Hao
 * @since 2022/6/8
 */
@Mapper(componentModel = "spring")
public interface DictItemConverter {

    Page<DictPageVO> toPageVo(Page<DictItem> page);

    DictItemForm toForm(DictItem entity);

    DictItem toEntity(DictItemForm formFata);

    Option<Long> toOption(DictItem dictItem);
    List<Option<Long>> toOption(List<DictItem> dictData);
}

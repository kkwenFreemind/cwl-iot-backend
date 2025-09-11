package community.waterlevel.iot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import community.waterlevel.iot.system.model.entity.DictItem;
import community.waterlevel.iot.system.model.query.DictItemPageQuery;
import community.waterlevel.iot.system.model.vo.DictItemPageVO;

import org.apache.ibatis.annotations.Mapper;

/**
 * 字典项映射层
 *
 * @author Ray Hao
 * @since 2.9.0
 */
@Mapper
public interface DictItemMapper extends BaseMapper<DictItem> {

    /**
     * 字典项分页列表
     */
    Page<DictItemPageVO> getDictItemPage(Page<DictItemPageVO> page, DictItemPageQuery queryParams);
}





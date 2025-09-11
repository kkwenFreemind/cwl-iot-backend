package community.waterlevel.iot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import community.waterlevel.iot.system.model.entity.Config;

import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置 访问层
 *
 * @author Theo
 * @since 2024-7-29 11:41:04
 */
@Mapper
public interface ConfigMapper extends BaseMapper<Config> {

}

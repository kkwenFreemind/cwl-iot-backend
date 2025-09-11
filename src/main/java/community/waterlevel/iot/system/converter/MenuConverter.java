package community.waterlevel.iot.system.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import community.waterlevel.iot.system.model.entity.Menu;
import community.waterlevel.iot.system.model.form.MenuForm;
import community.waterlevel.iot.system.model.vo.MenuVO;

/**
 * 菜单对象转换器
 *
 * @author Ray Hao
 * @since 2024/5/26
 */
@Mapper(componentModel = "spring")
public interface MenuConverter {

    MenuVO toVo(Menu entity);

    @Mapping(target = "params", ignore = true)
    MenuForm toForm(Menu entity);

    @Mapping(target = "params", ignore = true)
    Menu toEntity(MenuForm menuForm);

}
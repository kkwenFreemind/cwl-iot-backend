package community.waterlevel.iot.system.converter;

import org.mapstruct.Mapper;

import community.waterlevel.iot.system.model.entity.Dept;
import community.waterlevel.iot.system.model.form.DeptForm;
import community.waterlevel.iot.system.model.vo.DeptVO;

/**
 * 部门对象转换器
 *
 * @author haoxr
 * @since 2022/7/29
 */
@Mapper(componentModel = "spring")
public interface DeptConverter {

    DeptForm toForm(Dept entity);
    
    DeptVO toVo(Dept entity);

    Dept toEntity(DeptForm deptForm);

}
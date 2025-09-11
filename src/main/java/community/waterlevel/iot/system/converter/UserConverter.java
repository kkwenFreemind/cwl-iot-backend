package community.waterlevel.iot.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.system.model.bo.UserBO;
import community.waterlevel.iot.system.model.dto.CurrentUserDTO;
import community.waterlevel.iot.system.model.dto.UserImportDTO;
import community.waterlevel.iot.system.model.entity.User;
import community.waterlevel.iot.system.model.form.UserForm;
import community.waterlevel.iot.system.model.form.UserProfileForm;
import community.waterlevel.iot.system.model.vo.UserPageVO;
import community.waterlevel.iot.system.model.vo.UserProfileVO;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * 用户对象转换器
 *
 * @author Ray.Hao
 * @since 2022/6/8
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    UserPageVO toPageVo(UserBO bo);

    Page<UserPageVO> toPageVo(Page<UserBO> bo);

    UserForm toForm(User entity);

    @InheritInverseConfiguration(name = "toForm")
    User toEntity(UserForm entity);

    @Mappings({
            @Mapping(target = "userId", source = "id")
    })
    CurrentUserDTO toCurrentUserDto(User entity);

    User toEntity(UserImportDTO vo);


    UserProfileVO toProfileVo(UserBO bo);

    User toEntity(UserProfileForm formData);

    @Mappings({
            @Mapping(target = "label", source = "nickname"),
            @Mapping(target = "value", source = "id")
    })
    Option<String> toOption(User entity);

    List<Option<String>> toOptions(List<User> list);
}

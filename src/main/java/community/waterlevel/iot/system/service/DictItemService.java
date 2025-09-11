package community.waterlevel.iot.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import community.waterlevel.iot.system.model.entity.DictItem;
import community.waterlevel.iot.system.model.form.DictItemForm;
import community.waterlevel.iot.system.model.query.DictItemPageQuery;
import community.waterlevel.iot.system.model.vo.DictItemOptionVO;
import community.waterlevel.iot.system.model.vo.DictItemPageVO;

import java.util.List;

/**
 * 字典项接口
 *
 * @author Ray Hao
 * @since 2023/3/4
 */
public interface DictItemService extends IService<DictItem> {

    /**
     * 字典项分页列表
     *
     * @param queryParams 查询参数
     * @return 字典项分页列表
     */
    Page<DictItemPageVO> getDictItemPage(DictItemPageQuery queryParams);

    /**
     * 获取字典项列表
     *
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    List<DictItemOptionVO> getDictItems(String dictCode);

    /**
     * 获取字典项表单
     *
     * @param itemId 字典项ID
     * @return 字典项表单
     */
    DictItemForm getDictItemForm(Long itemId);

    /**
     * 保存字典项
     *
     * @param formData 字典项表单
     * @return 是否成功
     */
    boolean saveDictItem(DictItemForm formData);

    /**
     * 更新字典项
     *
     * @param formData 字典项表单
     * @return 是否成功
     */
    boolean updateDictItem(DictItemForm formData);

    /**
     * 删除字典项
     *
     * @param ids 字典项ID,多个逗号分隔
     */
    void deleteDictItemByIds(String ids);

}

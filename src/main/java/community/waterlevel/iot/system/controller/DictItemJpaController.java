package community.waterlevel.iot.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import community.waterlevel.iot.common.result.PageResult;
import community.waterlevel.iot.common.result.Result;
import community.waterlevel.iot.system.model.form.DictItemForm;
import community.waterlevel.iot.system.model.query.DictItemPageQuery;
import community.waterlevel.iot.system.model.vo.DictItemOptionVO;
import community.waterlevel.iot.system.model.vo.DictItemPageVO;
import community.waterlevel.iot.system.service.DictItemJpaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Dictionary Item JPA Controller.
 *
 * Provides RESTful endpoints for managing dictionary items, including CRUD
 * operations and pagination.
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Tag(name = "Dictionary Item Controller", description = "Dictionary Item JPA Test Controller")
@RestController
@RequestMapping("/api/v1/dict-item")
@RequiredArgsConstructor
@Slf4j
public class DictItemJpaController {

    /**
     * Service for dictionary item JPA operations
     */
    private final DictItemJpaService dictItemJpaService;

    /**
     * Get paginated list of dictionary items.
     *
     * @param queryParams Query parameters for pagination and filtering
     * @return Paginated result of dictionary item value objects
     */
    @Operation(summary = "Get paginated list of dictionary items")
    @GetMapping("/page")
    public PageResult<DictItemPageVO> getDictItemPage(DictItemPageQuery queryParams) {
        Page<DictItemPageVO> result = dictItemJpaService.getDictItemPage(queryParams);
        return PageResult.success(result);
    }

    /**
     * 字典項列表
     * Get the list of dictionary items by dictionary code.
     *
     * @param dictCode Dictionary code
     * @return Result containing the list of dictionary item options
     */
    @Operation(summary = "Get the list of dictionary items by dictionary code")
    @GetMapping("/{dictCode}/items")
    public Result<List<DictItemOptionVO>> getDictItems(
            @Parameter(description = "Dictionary code") @PathVariable String dictCode) {
        List<DictItemOptionVO> list = dictItemJpaService.getDictItems(dictCode);
        return Result.success(list);
    }

    /**
     * 字典項表單
     * Get dictionary item form data by item ID.
     *
     * @param id Dictionary item ID
     * @return Result containing dictionary item form data
     */
    @Operation(summary = "Get dictionary item form data by item ID.")
    @GetMapping("/{id}/form")
    public Result<DictItemForm> getDictItemForm(
            @Parameter(description = "Dictionary code") @PathVariable Long id) {
        DictItemForm formData = dictItemJpaService.getDictItemForm(id);
        return Result.success(formData);
    }

    /**
     * 新增字典項
     * Create a new dictionary item.
     *
     * @param dictItemForm Dictionary item form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Create a new dictionary item.")
    @PostMapping
    public Result<Void> saveDictItem(@RequestBody DictItemForm dictItemForm) {
        boolean result = dictItemJpaService.saveDictItem(dictItemForm);
        return result ? Result.success() : Result.failed("Create failed");
    }

    /**
     * 修改字典項
     * Update an existing dictionary item.
     *
     * @param dictItemForm Dictionary item form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Update an existing dictionary item.")
    @PutMapping
    public Result<Void> updateDictItem(@RequestBody DictItemForm dictItemForm) {
        boolean result = dictItemJpaService.updateDictItem(dictItemForm);
        return result ? Result.success() : Result.failed("Edit failed");
    }

    /**
     * 刪除字典項
     * Delete one or more dictionary items by IDs.
     *
     * @param ids Dictionary item IDs, separated by commas
     * @return Result indicating success or failure
     */
    @Operation(summary = "Delete one or more dictionary items by IDs.")
    @DeleteMapping("/{ids}")
    public Result<Void> deleteDictItemByIds(
            @Parameter(description = "Dictionary item IDs, separated by commas") @PathVariable String ids) {
        dictItemJpaService.deleteDictItemByIds(ids);
        return Result.success();
    }
}

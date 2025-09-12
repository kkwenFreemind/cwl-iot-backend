package community.waterlevel.iot.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import community.waterlevel.iot.common.result.PageResult;
import community.waterlevel.iot.common.result.Result;
import community.waterlevel.iot.system.model.form.DictForm;
import community.waterlevel.iot.system.model.form.DictItemForm;
import community.waterlevel.iot.system.model.query.DictPageQuery;
import community.waterlevel.iot.system.model.query.DictItemPageQuery;
import community.waterlevel.iot.system.model.vo.DictPageVO;
import community.waterlevel.iot.system.model.vo.DictItemPageVO;
import community.waterlevel.iot.system.model.vo.DictItemOptionVO;
import community.waterlevel.iot.system.service.DictJpaService;
import community.waterlevel.iot.system.service.DictItemJpaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DictJpaController is a REST controller that provides endpoints for managing
 * dictionaries and dictionary items.
 * <p>
 * It exposes APIs for listing, creating, updating, and deleting dictionaries
 * and their items, supporting pagination, form data retrieval, and real-time
 * update notifications via WebSocket. The controller delegates business logic
 * to the {@link DictJpaService}, {@link DictItemJpaService}, and
 * {@link WebSocketService}, and returns standardized API responses.
 * <p>
 * Designed for use in the IoT backend's administration interface to manage
 * system dictionaries and their values.
 *
 * @author Ray.Hao
 * @since 2.9.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Tag(name = "06.Dict Controller")
@RestController
@SuppressWarnings("SpellCheckingInspection")
@RequestMapping("/api/v1/dicts")
@RequiredArgsConstructor
public class DictJpaController {

    private final DictJpaService dictJpaService;
    private final DictItemJpaService dictItemJpaService;

    /**
     * Retrieves a paginated list of dictionaries.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return PageResult containing dictionary page data
     */
    @Operation(summary = "Retrieves a paginated list of dictionaries")
    @GetMapping("/page")
    public PageResult<DictPageVO> getDictPage(DictPageQuery queryParams) {
        Page<DictPageVO> result = dictJpaService.getDictPage(queryParams);
        return PageResult.success(result);
    }

    /**
     * Retrieves the form data for a specific dictionary by ID.
     *
     * @param id the dictionary ID
     * @return Result containing the dictionary form data
     */
    @Operation(summary = "Retrieves the form data for a specific dictionary by ID.")
    @GetMapping("/{id}/form")
    public Result<DictForm> getDictForm(
            @Parameter(description = "dictionary ID") @PathVariable Long id) {
        DictForm formData = dictJpaService.getDictForm(id);
        return Result.success(formData);
    }

    /**
     * Creates a new dictionary.
     *
     * @param dictForm the dictionary form data
     * @return Result indicating success or failure
     */
    @Operation(summary = " Creates a new dictionary.")
    @PostMapping
    public Result<Void> saveDict(@RequestBody DictForm dictForm) {
        boolean result = dictJpaService.saveDict(dictForm);
        return result ? Result.success() : Result.failed("Create failed");
    }

    /**
     * Updates an existing dictionary by ID.
     *
     * @param id the dictionary ID
     * @param dictForm the dictionary form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Updates an existing dictionary by ID.")
    @PutMapping("/{id}")
    public Result<Void> updateDict(
            @Parameter(description = "dictionary ID") @PathVariable Long id,
            @RequestBody DictForm dictForm) {
        boolean result = dictJpaService.updateDict(id, dictForm);
        return result ? Result.success() : Result.failed("修改失敗");
    }

    /**
     * Deletes one or more dictionaries by IDs.
     *
     * @param ids the dictionary IDs, separated by commas
     * @return Result indicating success or failure
     */
    @Operation(summary = "Deletes one or more dictionaries by IDs.")
    @DeleteMapping("/{ids}")
    public Result<Void> deleteDictByIds(
            @Parameter(description = "the dictionary IDs, separated by commas") @PathVariable String ids) {
        List<String> idList = List.of(ids.split(","));
        dictJpaService.deleteDictByIds(idList);
        return Result.success();
    }


    /**
     * Retrieves a paginated list of dictionary items for a specific dictionary code.
     *
     * @param dictCode the dictionary code
     * @param queryParams the query parameters for pagination and filtering
     * @return PageResult containing dictionary item page data
     */
    @Operation(summary = "Retrieves a paginated list of dictionary items for a specific dictionary code.")
    @GetMapping("/{dictCode}/items/page")
    public PageResult<DictItemPageVO> getDictItemPage(
            @Parameter(description = "dict Code") @PathVariable String dictCode,
            DictItemPageQuery queryParams) {
        queryParams.setDictCode(dictCode);
        Page<DictItemPageVO> result = dictItemJpaService.getDictItemPage(queryParams);
        return PageResult.success(result);
    }

    /**
     * Retrieves the list of dictionary items for a specific dictionary code.
     *
     * @param dictCode the dictionary code
     * @return Result containing the list of dictionary item options
     */
    @Operation(summary = "Retrieves the list of dictionary items for a specific dictionary code.")
    @GetMapping("/{dictCode}/items")
    public Result<List<DictItemOptionVO>> getDictItems(
            @Parameter(description = "dict Code") @PathVariable String dictCode) {
        List<DictItemOptionVO> list = dictItemJpaService.getDictItems(dictCode);
        return Result.success(list);
    }

    /**
     * Retrieves the form data for a specific dictionary item.
     *
     * @param dictCode the dictionary code
     * @param itemId the dictionary item ID
     * @return Result containing the dictionary item form data
     */
    @Operation(summary = "Retrieves the form data for a specific dictionary item.")
    @GetMapping("/{dictCode}/items/{itemId}/form")
    public Result<DictItemForm> getDictItemForm(
            @Parameter(description = "dict Code") @PathVariable String dictCode,
            @Parameter(description = "item Id") @PathVariable Long itemId) {
        DictItemForm formData = dictItemJpaService.getDictItemForm(itemId);
        return Result.success(formData);
    }

    /**
     * Creates a new dictionary item for a specific dictionary code.
     *
     * @param dictCode the dictionary code
     * @param dictItemForm the dictionary item form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Creates a new dictionary item for a specific dictionary code.")
    @PostMapping("/{dictCode}/items")
    public Result<Void> saveDictItem(
            @Parameter(description = "dict Code") @PathVariable String dictCode,
            @RequestBody DictItemForm dictItemForm) {
        dictItemForm.setDictCode(dictCode);
        boolean result = dictItemJpaService.saveDictItem(dictItemForm);
        return result ? Result.success() : Result.failed("Create failed");
    }

    /**
     * Updates an existing dictionary item by dictionary code and item ID.
     *
     * @param dictCode the dictionary code
     * @param itemId the dictionary item ID
     * @param dictItemForm the dictionary item form data
     * @return Result indicating success or failure
     */
    @Operation(summary = "Updates an existing dictionary item by dictionary code and item ID.")
    @PutMapping("/{dictCode}/items/{itemId}")
    public Result<Void> updateDictItem(
            @Parameter(description = "dictCode") @PathVariable String dictCode,
            @Parameter(description = "itemId") @PathVariable Long itemId,
            @RequestBody DictItemForm dictItemForm) {

        dictItemForm.setDictCode(dictCode);
        dictItemForm.setId(itemId);
        boolean result = dictItemJpaService.updateDictItem(dictItemForm);
        return result ? Result.success() : Result.failed("Update failed");
    }

    /**
     * Deletes one or more dictionary items by dictionary code and item IDs.
     *
     * @param dictCode the dictionary code
     * @param ids the dictionary item IDs, separated by commas
     * @return Result indicating success or failure
     */
    @Operation(summary = "Deletes one or more dictionary items by dictionary code and item IDs.")
    @DeleteMapping("/{dictCode}/items/{ids}")
    public Result<Void> deleteDictItemByIds(
            @Parameter(description = "dictCode") @PathVariable String dictCode,
            @Parameter(description = "the dictionary item IDs, separated by commas") @PathVariable String ids) {
        dictItemJpaService.deleteDictItemByIds(ids);
        return Result.success();
    }
}


package community.waterlevel.iot.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import community.waterlevel.iot.system.model.entity.DictItemJpa;
import community.waterlevel.iot.system.model.form.DictItemForm;
import community.waterlevel.iot.system.model.query.DictItemPageQuery;
import community.waterlevel.iot.system.model.vo.DictItemOptionVO;
import community.waterlevel.iot.system.model.vo.DictItemPageVO;
import community.waterlevel.iot.system.repository.DictItemJpaRepository;
import community.waterlevel.iot.system.service.DictItemJpaService;
import community.waterlevel.iot.system.service.SystemDictItemJpaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the dictionary item service business logic.
 * <p>
 * Provides methods for managing dictionary item entities, including CRUD operations, pagination,
 * option listing, and entity conversion. Integrates with JPA repositories for persistence.
 * </p>
 *
 * @author Ray.Hao
 * @since 2022/10/12
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Service("dictItemJpaService")
@Primary
@RequiredArgsConstructor
@Slf4j
public class DictItemJpaServiceImpl implements DictItemJpaService, SystemDictItemJpaService {

    private final DictItemJpaRepository dictItemJpaRepository;

    /**
     * Retrieves a paginated list of dictionary items based on the provided query parameters.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return a paginated result of dictionary item view objects
     */
    @Override
    public Page<DictItemPageVO> getDictItemPage(DictItemPageQuery queryParams) {

        Specification<DictItemJpa> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), 0));

            if (StringUtils.hasText(queryParams.getDictCode())) {
                predicates.add(criteriaBuilder.equal(root.get("dictCode"), queryParams.getDictCode()));
            }

            if (StringUtils.hasText(queryParams.getKeywords())) {
                String keywords = "%" + queryParams.getKeywords() + "%";
                Predicate labelLike = criteriaBuilder.like(root.get("label"), keywords);
                Predicate valueLike = criteriaBuilder.like(root.get("value"), keywords);
                predicates.add(criteriaBuilder.or(labelLike, valueLike));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize,
                Sort.by(Sort.Direction.ASC, "sort").and(Sort.by(Sort.Direction.ASC, "createTime")));

        org.springframework.data.domain.Page<DictItemJpa> pageResult = dictItemJpaRepository.findAll(spec, pageRequest);

        List<DictItemPageVO> records = pageResult.getContent().stream()
                .map(this::entityToPageVO)
                .collect(Collectors.toList());

        Page<DictItemPageVO> result = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
        result.setRecords(records);
        result.setTotal(pageResult.getTotalElements());

        return result;
    }

    /**
     * Retrieves a list of dictionary item options for a specific dictionary code.
     *
     * @param dictCode the code of the dictionary
     * @return a list of dictionary item option view objects
     */
    @Override
    public List<DictItemOptionVO> getDictItems(String dictCode) {

        List<DictItemJpa> dictItems = dictItemJpaRepository.findByDictCodeAndStatusOrderBySortAscCreateTimeAsc(dictCode,
                1);

        return dictItems.stream()
                .map(this::convertToDictItemOptionVO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the dictionary item form data for a specific item ID.
     *
     * @param itemId the unique identifier of the dictionary item
     * @return the dictionary item form data
     */
    @Override
    public DictItemForm getDictItemForm(Long itemId) {

        DictItemJpa dictItem = dictItemJpaRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("字典項不存在"));

        return convertToForm(dictItem);
    }

    /**
     * Saves a new dictionary item using the provided form data.
     *
     * @param formData the dictionary item form data
     * @return true if the operation was successful, false otherwise
     */
    @Override
    @Transactional
    public boolean saveDictItem(DictItemForm formData) {

        if (dictItemJpaRepository.existsByDictCodeAndValue(formData.getDictCode(), formData.getValue())) {
            throw new RuntimeException("Dictionary item value already exists");
        }

        DictItemJpa dictItem = convertToEntity(formData);
        dictItemJpaRepository.save(dictItem);

        return true;
    }

    /**
     * Updates an existing dictionary item with the provided form data.
     *
     * @param formData the dictionary item form data
     * @return true if the operation was successful, false otherwise
     */
    @Override
    @Transactional
    public boolean updateDictItem(DictItemForm formData) {

        DictItemJpa existingDictItem = dictItemJpaRepository.findById(formData.getId())
                .orElseThrow(() -> new RuntimeException("Dictionary item does not exist"));

        if (dictItemJpaRepository.existsByDictCodeAndValueAndIdNot(
                formData.getDictCode(), formData.getValue(), formData.getId())) {
            throw new RuntimeException("Dictionary item value already exists");
        }
        existingDictItem.setDictCode(formData.getDictCode());
        existingDictItem.setLabel(formData.getLabel());
        existingDictItem.setValue(formData.getValue());
        existingDictItem.setSort(formData.getSort());
        existingDictItem.setStatus(formData.getStatus());
        existingDictItem.setTagType(formData.getTagType());

        dictItemJpaRepository.save(existingDictItem);

        return true;
    }

    /**
     * Deletes dictionary items by a comma-separated string of item IDs.
     *
     * @param ids a comma-separated string of dictionary item IDs
     */
    @Override
    @Transactional
    public void deleteDictItemByIds(String ids) {

        if (StringUtils.hasText(ids)) {
            List<Long> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            dictItemJpaRepository.deleteAllById(idList);
        }
    }

    /**
     * Converts a dictionary item entity to a page view object.
     *
     * @param dictItem the dictionary item entity
     * @return the dictionary item page view object
     */
    private DictItemPageVO entityToPageVO(DictItemJpa dictItem) {
        DictItemPageVO vo = new DictItemPageVO();
        vo.setId(dictItem.getId());
        vo.setDictCode(dictItem.getDictCode());
        vo.setLabel(dictItem.getLabel());
        vo.setValue(dictItem.getValue());
        vo.setSort(dictItem.getSort());
        vo.setStatus(dictItem.getStatus());
        return vo;
    }

    /**
     * Converts a dictionary item entity to an option view object.
     *
     * @param dictItem the dictionary item entity
     * @return the dictionary item option view object
     */
    private DictItemOptionVO convertToDictItemOptionVO(DictItemJpa dictItem) {
        DictItemOptionVO vo = new DictItemOptionVO();
        vo.setValue(dictItem.getValue());
        vo.setLabel(dictItem.getLabel());
        vo.setTagType(dictItem.getTagType());
        return vo;
    }

    /**
     * Converts a dictionary item entity to a form object.
     *
     * @param dictItem the dictionary item entity
     * @return the dictionary item form object
     */
    private DictItemForm convertToForm(DictItemJpa dictItem) {
        DictItemForm form = new DictItemForm();
        form.setId(dictItem.getId());
        form.setDictCode(dictItem.getDictCode());
        form.setLabel(dictItem.getLabel());
        form.setValue(dictItem.getValue());
        form.setSort(dictItem.getSort());
        form.setStatus(dictItem.getStatus());
        form.setTagType(dictItem.getTagType());
        return form;
    }

    /**
     * Converts a dictionary item form object to an entity.
     *
     * @param form the dictionary item form object
     * @return the dictionary item entity
     */
    private DictItemJpa convertToEntity(DictItemForm form) {
        DictItemJpa dictItem = new DictItemJpa();
        dictItem.setId(form.getId());
        dictItem.setDictCode(form.getDictCode());
        dictItem.setLabel(form.getLabel());
        dictItem.setValue(form.getValue());
        dictItem.setSort(form.getSort());
        dictItem.setStatus(form.getStatus());
        dictItem.setTagType(form.getTagType());
        return dictItem;
    }
}

package community.waterlevel.iot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import community.waterlevel.iot.common.exception.BusinessException;
import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.system.converter.DictJpaConverter;
import community.waterlevel.iot.system.model.entity.DictItemJpa;
import community.waterlevel.iot.system.model.entity.DictJpa;
import community.waterlevel.iot.system.model.form.DictForm;
import community.waterlevel.iot.system.model.query.DictPageQuery;
import community.waterlevel.iot.system.model.vo.DictPageVO;
import community.waterlevel.iot.system.repository.DictJpaRepository;
import community.waterlevel.iot.system.repository.DictItemJpaRepository;
import community.waterlevel.iot.system.service.DictItemJpaService;
import community.waterlevel.iot.system.service.DictJpaService;
import community.waterlevel.iot.system.service.SystemDictJpaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the dictionary service business logic.
 * <p>
 * Provides methods for managing dictionary entities, including CRUD operations,
 * pagination,
 * option listing, and entity conversion. Integrates with JPA repositories for
 * persistence.
 * </p>
 *
 * @author haoxr
 * @since 2022/10/12
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Service("dictJpaService")
@Primary
@RequiredArgsConstructor
@Slf4j
public class DictJpaServiceImpl implements DictJpaService, SystemDictJpaService {

    private final DictJpaRepository dictJpaRepository;
    private final DictItemJpaRepository dictItemJpaRepository;

    /**
     * Retrieves a paginated list of dictionaries based on the provided query
     * parameters.
     *
     * @param queryParams the query parameters for pagination and filtering
     * @return a paginated result of dictionary view objects
     */
    @Override
    public Page<DictPageVO> getDictPage(DictPageQuery queryParams) {

        Specification<DictJpa> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), 0));

            if (StringUtils.hasText(queryParams.getKeywords())) {
                String keywords = "%" + queryParams.getKeywords() + "%";
                Predicate nameLike = criteriaBuilder.like(root.get("name"), keywords);
                Predicate codeLike = criteriaBuilder.like(root.get("dictCode"), keywords);
                predicates.add(criteriaBuilder.or(nameLike, codeLike));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createTime"));

        org.springframework.data.domain.Page<DictJpa> pageResult = dictJpaRepository.findAll(spec, pageRequest);

        List<DictPageVO> records = pageResult.getContent().stream()
                .map(this::entityToPageVO)
                .collect(Collectors.toList());

        Page<DictPageVO> result = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
        result.setRecords(records);
        result.setTotal(pageResult.getTotalElements());

        return result;
    }

    /**
     * Retrieves a list of dictionary options for selection components.
     *
     * @return a list of dictionary options
     */
    @Override
    public List<Option<String>> getDictList() {

        List<DictJpa> dictList = dictJpaRepository.findByStatusOrderByCreateTimeDesc(1);

        return dictList.stream()
                .map(dict -> new Option<>(dict.getDictCode(), dict.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the dictionary form data for a specific dictionary ID.
     *
     * @param id the unique identifier of the dictionary
     * @return the dictionary form data
     */
    @Override
    public DictForm getDictForm(Long id) {

        DictJpa dict = dictJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dictionary encoding already exists"));

        return convertToForm(dict);
    }

    /**
     * Saves a new dictionary using the provided form data.
     *
     * @param dictForm the dictionary form data
     * @return true if the operation was successful, false otherwise
     */
    @Override
    @Transactional
    public boolean saveDict(DictForm dictForm) {

        if (dictJpaRepository.existsByDictCode(dictForm.getDictCode())) {
            throw new RuntimeException("Dictionary encoding already exists");
        }

        DictJpa dict = convertToEntity(dictForm);
        dictJpaRepository.save(dict);

        return true;
    }

    /**
     * Updates an existing dictionary with the provided form data.
     *
     * @param id       the unique identifier of the dictionary
     * @param dictForm the dictionary form data
     * @return true if the operation was successful, false otherwise
     */
    @Override
    @Transactional
    public boolean updateDict(Long id, DictForm dictForm) {

        DictJpa existingDict = dictJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dictionary does not exist"));

        if (dictJpaRepository.existsByDictCodeAndIdNot(dictForm.getDictCode(), id)) {
            throw new RuntimeException("Dictionary encoding already exists");
        }

        existingDict.setDictCode(dictForm.getDictCode());
        existingDict.setName(dictForm.getName());
        existingDict.setStatus(dictForm.getStatus());
        existingDict.setRemark(dictForm.getRemark());

        dictJpaRepository.save(existingDict);

        return true;
    }

    /**
     * Deletes dictionaries by a list of string IDs and logically deletes their
     * associated dictionary items.
     *
     * @param ids the list of dictionary IDs as strings
     */
    @Override
    @Transactional
    public void deleteDictByIds(List<String> ids) {

        List<Long> longIds = ids.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        List<DictJpa> dictsToDelete = dictJpaRepository.findByIdIn(longIds);
        List<String> dictCodes = dictsToDelete.stream()
                .map(DictJpa::getDictCode)
                .collect(Collectors.toList());

        dictJpaRepository.deleteAllById(longIds);

        if (!dictCodes.isEmpty()) {
            dictItemJpaRepository.logicalDeleteByDictCodes(dictCodes);
        }
    }

    /**
     * Retrieves a list of dictionary codes by a list of string IDs.
     *
     * @param ids the list of dictionary IDs as strings
     * @return a list of dictionary codes
     */
    @Override
    public List<String> getDictCodesByIds(List<String> ids) {

        List<Long> longIds = ids.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        List<DictJpa> dicts = dictJpaRepository.findByIdIn(longIds);
        return dicts.stream()
                .map(DictJpa::getDictCode)
                .collect(Collectors.toList());
    }

    /**
     * Converts a dictionary entity to a page view object.
     *
     * @param dict the dictionary entity
     * @return the dictionary page view object
     */
    private DictPageVO entityToPageVO(DictJpa dict) {
        DictPageVO vo = new DictPageVO();
        vo.setId(dict.getId());
        vo.setName(dict.getName());
        vo.setDictCode(dict.getDictCode());
        vo.setStatus(dict.getStatus());
        return vo;
    }

    /**
     * Converts a dictionary entity to a form object.
     *
     * @param dict the dictionary entity
     * @return the dictionary form object
     */
    private DictForm convertToForm(DictJpa dict) {
        DictForm form = new DictForm();
        form.setId(dict.getId());
        form.setDictCode(dict.getDictCode());
        form.setName(dict.getName());
        form.setStatus(dict.getStatus());
        form.setRemark(dict.getRemark());
        return form;
    }

    /**
     * Converts a dictionary form object to an entity.
     *
     * @param form the dictionary form object
     * @return the dictionary entity
     */
    private DictJpa convertToEntity(DictForm form) {
        DictJpa dict = new DictJpa();
        dict.setId(form.getId());
        dict.setDictCode(form.getDictCode());
        dict.setName(form.getName());
        dict.setStatus(form.getStatus());
        dict.setRemark(form.getRemark());
        return dict;
    }
}

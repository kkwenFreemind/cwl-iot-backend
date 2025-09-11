package community.waterlevel.iot.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import community.waterlevel.iot.system.enums.MenuTypeEnum;
import community.waterlevel.iot.common.enums.StatusEnum;
import community.waterlevel.iot.common.model.KeyValue;
import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.core.security.util.SecurityUtils;
import community.waterlevel.iot.system.converter.MenuJpaConverter;
import community.waterlevel.iot.system.model.entity.MenuJpa;
import community.waterlevel.iot.system.model.form.MenuForm;
import community.waterlevel.iot.system.model.query.MenuQuery;
import community.waterlevel.iot.system.model.vo.MenuVO;
import community.waterlevel.iot.system.model.vo.RouteVO;
import community.waterlevel.iot.system.repository.MenuJpaRepository;
import community.waterlevel.iot.system.repository.RoleMenuJpaRepository;
import community.waterlevel.iot.system.repository.RoleJpaRepository;
import community.waterlevel.iot.system.service.MenuJpaService;
import community.waterlevel.iot.system.service.SystemMenuJpaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of menu-related business logic and operations for the system.
 * <p>
 * Provides services for menu management, including CRUD operations, menu tree
 * construction,
 * route generation, option listing, and validation. Integrates with role-based
 * access control
 * to determine menu visibility and user-specific routes. Handles menu parameter
 * serialization,
 * hierarchical relationships, and supports transactional updates.
 * </p>
 *
 * @author Ray.Hao
 * @since 2020/11/06
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class MenuJpaServiceImpl implements MenuJpaService, SystemMenuJpaService {

    private final MenuJpaRepository menuJpaRepository;
    private final RoleMenuJpaRepository roleMenuJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final MenuJpaConverter menuJpaConverter;

    /**
     * Retrieves the list of menus associated with the given set of role codes.
     * Filters out menus of type 4 and sorts the result by the menu sort order.
     *
     * @param roleCodes the set of role codes
     * @return the list of menus accessible by the specified roles
     */
    @Override
    public List<MenuJpa> getMenusByRoleCodes(Set<String> roleCodes) {
        try {
            if (CollectionUtil.isEmpty(roleCodes)) {
                return new ArrayList<>();
            }
            List<Long> roleIds = roleJpaRepository.findActiveRoleIdsByCodes(roleCodes);
            if (CollectionUtil.isEmpty(roleIds)) {
                return new ArrayList<>();
            }
            Set<Long> roleIdSet = new HashSet<>(roleIds);
            List<Long> menuIds = roleMenuJpaRepository.findMenuIdsByRoleIds(roleIdSet);
            if (CollectionUtil.isEmpty(menuIds)) {
                return new ArrayList<>();
            }
            List<MenuJpa> allMenus = menuJpaRepository.findAllById(menuIds);
            return allMenus.stream()
                    .filter(menu -> menu.getType() != 4)
                    .sorted(Comparator.comparing(MenuJpa::getSort))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get the menu list based on the role code", e);
            throw new RuntimeException("Failed to get the menu list based on the role code: " + e.getMessage(), e);
        }
    }

    /**
     * Lists all menus, optionally filtered by keywords, and returns them as a
     * hierarchical tree structure.
     *
     * @param queryParams the query parameters for filtering menus
     * @return the list of menu view objects in a tree structure
     */
    @Override
    public List<MenuVO> listMenus(MenuQuery queryParams) {
        try {
            List<MenuJpa> menuList = menuJpaRepository.findAll();
            if (queryParams != null) {
                if (StrUtil.isNotBlank(queryParams.getKeywords())) {
                    menuList = menuList.stream()
                            .filter(menu -> menu.getName().contains(queryParams.getKeywords()))
                            .collect(Collectors.toList());
                }
            }
            List<MenuVO> menuVOList = menuList.stream()
                    .map(menuJpaConverter::toVo)
                    .collect(Collectors.toList());
            return buildMenuTree(0L, menuVOList);
        } catch (Exception e) {
            log.error("Failed to obtain the menu table list", e);
            throw new RuntimeException("Failed to obtain the menu table list: " + e.getMessage(), e);
        }
    }

    /**
     * Lists menu options for dropdowns, either only parent menus or all menus
     * except buttons, as a tree.
     *
     * @param onlyParent whether to include only parent (catalog) menus
     * @return the list of menu options
     */
    @Override
    public List<Option<Long>> listMenuOptions(boolean onlyParent) {
        try {
            List<MenuJpa> menuList;
            if (onlyParent) {
                menuList = menuJpaRepository.findByTypeOrderBySortAsc(MenuTypeEnum.CATALOG.getValue());
                return menuList.stream()
                        .map(menu -> new Option<>(menu.getId(), menu.getName()))
                        .collect(Collectors.toList());
            } else {
                menuList = menuJpaRepository.findAll().stream()
                        .filter(menu -> !MenuTypeEnum.BUTTON.getValue().equals(menu.getType()))
                        .sorted(Comparator.comparing(MenuJpa::getSort))
                        .collect(Collectors.toList());
                return buildMenuOptionTree(0L, menuList);
            }
        } catch (Exception e) {
            log.error("Failed to get the menu drop-down list", e);
            throw new RuntimeException("Failed to get the menu drop-down list: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the route list for the current user based on their roles.
     *
     * @return the list of route view objects for the current user
     */
    @Override
    public List<RouteVO> getCurrentUserRoutes() {
        try {
            Set<String> roles = SecurityUtils.getRoleCodes();
            List<MenuJpa> menuList = getMenusByRoleCodes(roles);
            return buildRoutes(0L, menuList);
        } catch (Exception e) {
            log.error("Failed to obtain the current user's routing list", e);
            throw new RuntimeException("Failed to obtain the current user's routing list: " + e.getMessage(), e);
        }
    }

    /**
     * Saves a menu entity to the database.
     *
     * @param menuJpa the menu entity to save
     * @return the saved menu entity
     */
    @Override
    public MenuJpa save(MenuJpa menuJpa) {
        try {
            return menuJpaRepository.save(menuJpa);
        } catch (Exception e) {
            log.error("Failed to save menu", e);
            throw new RuntimeException("Failed to save menu: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a menu entity by its ID.
     *
     * @param id the menu ID
     * @return the found menu entity
     * @throws RuntimeException if the menu does not exist
     */
    @Override
    public MenuJpa findById(Long id) {
        try {
            return menuJpaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Menu does not exist"));
        } catch (Exception e) {
            log.error("Failed to find menu by ID", e);
            throw new RuntimeException("Failed to find menu by ID: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a menu entity by its ID.
     *
     * @param id the menu ID to delete
     */
    @Override
    public void deleteById(Long id) {
        try {
            menuJpaRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to delete menu by ID", e);
            throw new RuntimeException("Failed to delete menu by ID: " + e.getMessage(), e);
        }
    }

    /**
     * Saves or updates a menu based on the provided form data.
     * Validates menu name uniqueness and parent-child relationships, serializes
     * parameters, and sets timestamps.
     *
     * @param menuForm the menu form data
     * @return true if the menu was saved successfully
     */
    @Override
    @Transactional
    public boolean saveMenu(MenuForm menuForm) {
        try {
            Long menuId = menuForm.getId();
            if (existsByName(menuForm.getName(), menuId)) {
                throw new RuntimeException("Menu name already exists");
            }
            if (MenuTypeEnum.EXTLINK.getValue().equals(menuForm.getType())) {
                menuForm.setComponent(null);
            }
            if (Objects.equals(menuForm.getParentId(), menuForm.getId())) {
                throw new RuntimeException("The parent menu cannot be the current menu");
            }
            MenuJpa entity = menuJpaConverter.toEntity(menuForm);
            String treePath = generateMenuTreePath(menuForm.getParentId());
            entity.setTreePath(treePath);
            List<KeyValue> params = menuForm.getParams();
            if (CollectionUtil.isNotEmpty(params)) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> paramMap = params.stream()
                        .collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue));
                entity.setParams(objectMapper.writeValueAsString(paramMap));
            }
            LocalDateTime now = LocalDateTime.now();
            if (entity.getId() == null) {
                entity.setCreateTime(now);
            }
            entity.setUpdateTime(now);
            menuJpaRepository.save(entity);
            return true;
        } catch (Exception e) {
            log.error("Failed to save menu", e);
            throw new RuntimeException("Failed to save menu: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the menu form data for the specified menu ID, including
     * deserialized parameters.
     *
     * @param id the menu ID
     * @return the menu form data
     */
    @Override
    public MenuForm getMenuForm(Long id) {
        try {
            MenuJpa menuJpa = findById(id);
            MenuForm formData = menuJpaConverter.toForm(menuJpa);
            String paramsJson = menuJpa.getParams();
            if (StrUtil.isNotBlank(paramsJson)) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    Map<String, String> paramMap = objectMapper.readValue(paramsJson, new TypeReference<>() {
                    });
                    List<KeyValue> transformedList = paramMap.entrySet().stream()
                            .map(entry -> new KeyValue(entry.getKey(), entry.getValue()))
                            .collect(Collectors.toList());
                    formData.setParams(transformedList);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse parameters", e);
                }
            }
            return formData;
        } catch (Exception e) {
            log.error("Failed to obtain menu form data", e);
            throw new RuntimeException("Failed to obtain menu form data: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the visibility status of a menu.
     *
     * @param menuId  the menu ID
     * @param visible the new visibility status
     * @return true if the update was successful
     */
    @Override
    @Transactional
    public boolean updateMenuVisible(Long menuId, Integer visible) {
        try {
            MenuJpa menuJpa = findById(menuId);
            menuJpa.setVisible(visible);
            menuJpa.setUpdateTime(LocalDateTime.now());
            menuJpaRepository.save(menuJpa);
            return true;
        } catch (Exception e) {
            log.error("Failed to modify menu display status", e);
            throw new RuntimeException("Failed to modify menu display status: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a menu by its ID, ensuring it has no child menus.
     *
     * @param id the menu ID to delete
     * @return true if the menu was deleted successfully
     */
    @Override
    @Transactional
    public boolean deleteMenu(Long id) {
        try {
            List<MenuJpa> children = menuJpaRepository.findByParentIdOrderBySortAsc(id);
            if (CollectionUtil.isNotEmpty(children)) {
                throw new RuntimeException("There is a submenu and cannot be deleted.");
            }
            deleteById(id);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete menu", e);
            throw new RuntimeException("Failed to delete menu: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if a menu with the given name exists, excluding the specified ID.
     *
     * @param name the menu name to check
     * @param id   the menu ID to exclude from the check
     * @return true if a menu with the name exists, false otherwise
     */
    @Override
    public boolean existsByName(String name, Long id) {
        try {
            if (id == null) {
                return menuJpaRepository.existsByNameAndIdNot(name, 0L);
            }
            return menuJpaRepository.existsByNameAndIdNot(name, id);
        } catch (Exception e) {
            log.error("Checking if the menu name exists failed", e);
            return false;
        }
    }

    /**
     * Recursively builds a tree structure of menu view objects from a flat list.
     *
     * @param parentId the parent menu ID
     * @param menuList the flat list of menu view objects
     * @return the hierarchical list of menu view objects
     */
    private List<MenuVO> buildMenuTree(Long parentId, List<MenuVO> menuList) {
        return menuList.stream()
                .filter(menu -> Objects.equals(parentId, menu.getParentId()))
                .peek(menu -> {
                    List<MenuVO> children = buildMenuTree(menu.getId(), menuList);
                    menu.setChildren(children);
                })
                .sorted(Comparator.comparing(MenuVO::getSort))
                .collect(Collectors.toList());
    }

    /**
     * Recursively builds a list of route view objects for frontend routing from a
     * flat menu list.
     *
     * @param parentId the parent menu ID
     * @param menuList the flat list of menu entities
     * @return the hierarchical list of route view objects
     */
    private List<RouteVO> buildRoutes(Long parentId, List<MenuJpa> menuList) {
        return menuList.stream()
                .filter(menu -> Objects.equals(parentId, menu.getParentId()))
                .filter(menu -> StatusEnum.ENABLE.getValue().equals(menu.getVisible()))
                .map(this::toRouteVo)
                .peek(route -> {
                    List<MenuJpa> childMenus = menuList.stream()
                            .filter(menu -> menu.getParentId() != null &&
                                    menu.getParentId().equals(getMenuIdFromRoute(route, menuList)))
                            .collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(childMenus)) {
                        List<RouteVO> children = buildRoutes(getMenuIdFromRoute(route, menuList), menuList);
                        route.setChildren(children);
                    }
                })
                .sorted(Comparator.comparing(route -> getMenuFromRoute(route, menuList).getSort()))
                .collect(Collectors.toList());
    }

    /**
     * Converts a menu entity to a route view object for frontend routing.
     *
     * @param menu the menu entity
     * @return the corresponding route view object
     */
    private RouteVO toRouteVo(MenuJpa menu) {
        RouteVO routeVO = new RouteVO();
        String routeName = menu.getRouteName();
        if (StrUtil.isBlank(routeName)) {
            routeName = StringUtils.capitalize(StrUtil.toCamelCase(menu.getRoutePath(), '-'));
        }
        routeVO.setName(routeName);
        routeVO.setPath(menu.getRoutePath());
        routeVO.setRedirect(menu.getRedirect());
        routeVO.setComponent(menu.getComponent());
        RouteVO.Meta meta = new RouteVO.Meta();
        meta.setTitle(menu.getName());
        meta.setIcon(menu.getIcon());
        meta.setHidden(StatusEnum.DISABLE.getValue().equals(menu.getVisible()));
        if (MenuTypeEnum.MENU.getValue().equals(menu.getType()) &&
                ObjectUtil.equals(menu.getKeepAlive(), 1)) {
            meta.setKeepAlive(true);
        }
        meta.setAlwaysShow(ObjectUtil.equals(menu.getAlwaysShow(), 1));
        String paramsJson = menu.getParams();
        if (StrUtil.isNotBlank(paramsJson)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, String> paramMap = objectMapper.readValue(paramsJson, new TypeReference<>() {
                });
                meta.setParams(paramMap);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse parameters", e);
            }
        }
        routeVO.setMeta(meta);
        return routeVO;
    }

    /**
     * Generates the tree path string for a menu based on its parent ID.
     *
     * @param parentId the parent menu ID
     * @return the generated tree path string
     */
    private String generateMenuTreePath(Long parentId) {
        if (parentId == null || parentId == 0) {
            return "0";
        }
        MenuJpa parentMenu = menuJpaRepository.findById(parentId).orElse(null);
        if (parentMenu == null) {
            return "0";
        }
        return parentMenu.getTreePath() + "," + parentId;
    }

    /**
     * Recursively builds a tree structure of menu options from a flat menu list.
     *
     * @param parentId the parent menu ID
     * @param menuList the flat list of menu entities
     * @return the hierarchical list of menu options
     */
    private List<Option<Long>> buildMenuOptionTree(Long parentId, List<MenuJpa> menuList) {
        return menuList.stream()
                .filter(menu -> Objects.equals(parentId, menu.getParentId()))
                .map(menu -> {
                    List<Option<Long>> children = buildMenuOptionTree(menu.getId(), menuList);
                    if (CollectionUtil.isNotEmpty(children)) {
                        return new Option<>(menu.getId(), menu.getName(), children);
                    } else {
                        return new Option<>(menu.getId(), menu.getName());
                    }
                })
                .sorted(Comparator.comparing(option -> menuList.stream()
                        .filter(menu -> Objects.equals(menu.getId(), option.getValue()))
                        .findFirst()
                        .map(MenuJpa::getSort)
                        .orElse(0)))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the menu ID corresponding to a given route view object.
     *
     * @param route    the route view object
     * @param menuList the list of menu entities
     * @return the menu ID, or 0L if not found
     */
    private Long getMenuIdFromRoute(RouteVO route, List<MenuJpa> menuList) {
        return menuList.stream()
                .filter(menu -> Objects.equals(route.getName(), menu.getRouteName()) ||
                        Objects.equals(route.getPath(), menu.getRoutePath()))
                .map(MenuJpa::getId)
                .findFirst()
                .orElse(0L);
    }

    /**
     * Retrieves the menu entity corresponding to a given route view object.
     *
     * @param route    the route view object
     * @param menuList the list of menu entities
     * @return the menu entity, or a new MenuJpa if not found
     */
    private MenuJpa getMenuFromRoute(RouteVO route, List<MenuJpa> menuList) {
        return menuList.stream()
                .filter(menu -> Objects.equals(route.getName(), menu.getRouteName()) ||
                        Objects.equals(route.getPath(), menu.getRoutePath()))
                .findFirst()
                .orElse(new MenuJpa());
    }
}

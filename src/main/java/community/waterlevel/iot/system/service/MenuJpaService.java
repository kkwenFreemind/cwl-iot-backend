package community.waterlevel.iot.system.service;

import community.waterlevel.iot.common.model.Option;
import community.waterlevel.iot.system.model.entity.MenuJpa;
import community.waterlevel.iot.system.model.form.MenuForm;
import community.waterlevel.iot.system.model.query.MenuQuery;
import community.waterlevel.iot.system.model.vo.MenuVO;
import community.waterlevel.iot.system.model.vo.RouteVO;

import java.util.List;
import java.util.Set;

/**
 * Service interface for managing menu data and operations.
 * <p>
 * Provides methods for listing, retrieving, saving, updating, and deleting menu
 * records,
 * as well as generating menu options, checking for name existence, and
 * retrieving user routes.
 * Used in menu management and navigation features.
 * </p>
 *
 * @author haoxr
 * @since 2020/11/06
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface MenuJpaService {

    /**
     * Retrieves a list of menu entities by a set of role codes.
     *
     * @param roleCodes the set of role codes
     * @return a list of menu entities
     */
    List<MenuJpa> getMenusByRoleCodes(Set<String> roleCodes);

    /**
     * Retrieves a list of menu view objects based on query parameters.
     *
     * @param queryParams the parameters for filtering menus
     * @return a list of menu view objects
     */
    List<MenuVO> listMenus(MenuQuery queryParams);

    /**
     * Retrieves a list of menu options for selection components.
     *
     * @param onlyParent whether to only include parent menus
     * @return a list of menu options with IDs
     */
    List<Option<Long>> listMenuOptions(boolean onlyParent);

    /**
     * Retrieves the list of route view objects for the current user.
     *
     * @return a list of route view objects
     */
    List<RouteVO> getCurrentUserRoutes();

    /**
     * Saves a new menu entity.
     *
     * @param menuJpa the menu entity to save
     * @return the saved menu entity
     */
    MenuJpa save(MenuJpa menuJpa);

    /**
     * Finds a menu entity by its ID.
     *
     * @param id the ID of the menu
     * @return the menu entity if found, or null if not found
     */
    MenuJpa findById(Long id);

    /**
     * Deletes a menu entity by its ID.
     *
     * @param id the ID of the menu to delete
     */
    void deleteById(Long id);

    /**
     * Saves a new menu using a form object.
     *
     * @param menuForm the form object containing menu data to save
     * @return true if the save operation was successful, false otherwise
     */
    boolean saveMenu(MenuForm menuForm);

    /**
     * Retrieves the form data for a specific menu by its ID.
     *
     * @param id the ID of the menu
     * @return the form object containing menu data
     */
    MenuForm getMenuForm(Long id);

    /**
     * Updates the visibility status of a menu by its ID.
     *
     * @param menuId  the ID of the menu
     * @param visible the visibility status (1 for visible, 0 for hidden)
     * @return true if the update operation was successful, false otherwise
     */
    boolean updateMenuVisible(Long menuId, Integer visible);

    /**
     * Deletes a menu by its ID.
     *
     * @param id the ID of the menu to delete
     * @return true if the delete operation was successful, false otherwise
     */
    boolean deleteMenu(Long id);

    /**
     * Checks if a menu name exists, excluding a specific menu ID.
     *
     * @param name the menu name to check
     * @param id   the menu ID to exclude from the check
     * @return true if the name exists, false otherwise
     */
    boolean existsByName(String name, Long id);
}

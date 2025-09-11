package community.waterlevel.iot.system.model.bo;

import lombok.Data;

import java.util.Set;

/**
 * RolePermsBO is a business object representing the permissions assigned to a
 * specific role.
 * <p>
 * This class encapsulates the role code and the set of permission identifiers
 * associated with that role.
 * Used for transferring role-permission data between service and presentation
 * layers in the IoT backend.
 *
 * @author haoxr
 * @since 2023/11/29
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
public class RolePermsBO {

    /**
     * Unique role identifier code for system recognition.
     * Used programmatically for role-based access control logic.
     */
    private String roleCode;

    /**
     * Collection of permission identifiers granted to this role.
     * Set of unique permission strings for authorization checks and access control.
     */
    private Set<String> perms;

}

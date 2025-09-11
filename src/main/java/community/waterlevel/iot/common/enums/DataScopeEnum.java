package community.waterlevel.iot.common.enums;

import community.waterlevel.iot.common.base.IBaseEnum;
import lombok.Getter;

/**
 * Enumeration representing different levels of data access permissions.
 * Defines the scope of data a user or role can access, such as all data,
 * department and sub-department data, department-only data, or self-only data.
 * Commonly used for data permission and access control in multi-tenant or organizational systems.
 
 *
 * @author Ray.Hao
 * @since 2.3.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Getter
public enum DataScopeEnum implements IBaseEnum<Integer> {

    /**
     * All data permission (full access).
     */
    ALL(1, "All data"),

    /**
     * Department and sub-department data permission.
     */
    DEPT_AND_SUB(2, "Department and sub-department data"),

    /**
     * Department-only data permission.
     */
    DEPT(3, "Department data"),

    /**
     * Self-only data permission.
     */
    SELF(4, "Self data");

    /**
     * Numeric value representing the data scope.
     */
    private final Integer value;

    /**
     * Label/description of the data scope.
     */
    private final String label;

    /**
     * Constructor for DataScopeEnum.
     * 
     * @param value numeric value of the data scope
     * @param label label/description of the data scope
     */
    DataScopeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
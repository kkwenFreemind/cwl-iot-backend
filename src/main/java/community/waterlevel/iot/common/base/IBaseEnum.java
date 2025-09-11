package community.waterlevel.iot.common.base;

import cn.hutool.core.util.ObjectUtil;

import java.util.EnumSet;
import java.util.Objects;

/**
 * Common interface for application enums with value-label pairs.
 * Provides methods for retrieving enum constants, values, and labels by value or label.
 * Facilitates type-safe and consistent enum handling across the application.
 *
 * @author haoxr
 * @since 2022/3/27 12:06
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
public interface IBaseEnum<T> {

    /**
     * Gets the value associated with the enum constant.
     * 
     * @return value of the enum constant
     */
    T getValue();

    /**
     * Gets the label (display name) associated with the enum constant.
     * 
     * @return label of the enum constant
     */
    String getLabel();

    /**
     * Returns the enum constant of the specified enum type with the specified
     * value.
     *
     * @param value the value to match
     * @param clazz the enum class
     * @return the matching enum constant, or null if not found
     */
    static <E extends Enum<E> & IBaseEnum> E getEnumByValue(Object value, Class<E> clazz) {
        Objects.requireNonNull(value);
        EnumSet<E> allEnums = EnumSet.allOf(clazz); // Get all enums of the type
        E matchEnum = allEnums.stream()
                .filter(e -> ObjectUtil.equal(e.getValue(), value))
                .findFirst()
                .orElse(null);
        return matchEnum;
    }

    /**
     * Returns the label of the enum constant with the specified value.
     *
     * @param value the value to match
     * @param clazz the enum class
     * @return the label of the matching enum constant, or null if not found
     */
    static <E extends Enum<E> & IBaseEnum> String getLabelByValue(Object value, Class<E> clazz) {
        Objects.requireNonNull(value);
        EnumSet<E> allEnums = EnumSet.allOf(clazz); // Get all enums of the type
        E matchEnum = allEnums.stream()
                .filter(e -> ObjectUtil.equal(e.getValue(), value))
                .findFirst()
                .orElse(null);

        String label = null;
        if (matchEnum != null) {
            label = matchEnum.getLabel();
        }
        return label;
    }

    /**
     * Returns the value of the enum constant with the specified label.
     *
     * @param label the label to match
     * @param clazz the enum class
     * @return the value of the matching enum constant, or null if not found
     */
    static <E extends Enum<E> & IBaseEnum> Object getValueByLabel(String label, Class<E> clazz) {
        Objects.requireNonNull(label);
        EnumSet<E> allEnums = EnumSet.allOf(clazz);
        String finalLabel = label;
        E matchEnum = allEnums.stream()
                .filter(e -> ObjectUtil.equal(e.getLabel(), finalLabel))
                .findFirst()
                .orElse(null);

        Object value = null;
        if (matchEnum != null) {
            value = matchEnum.getValue();
        }
        return value;
    }

}
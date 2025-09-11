
package community.waterlevel.iot.common.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.format.annotation.DateTimeFormat;

import java.lang.reflect.Field;

/**
 * Utility class for date formatting and conversion.
 * Provides methods to format date fields of objects to database-compatible string formats,
 * supporting custom patterns and annotation-based parsing.
 *
 * @author haoxr
 * @since 2.4.2
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
public class DateUtils {

    /**
     * Converts the start and end time fields of the given object to a standardized
     * database format.
     * <p>
     * The start time is set to the beginning of the day (00:00:00), and the end
     * time is set to the end of the day (23:59:59),
     * using the provided field names. This is useful for constructing queries that
     * require full-day time ranges.
     *
     * @param obj                the object containing the date fields to be
     *                           formatted
     * @param startTimeFieldName the name of the start time field
     * @param endTimeFieldName   the name of the end time field
     */
    public static void toDatabaseFormat(Object obj, String startTimeFieldName, String endTimeFieldName) {
        Field startTimeField = ReflectUtil.getField(obj.getClass(), startTimeFieldName);
        Field endTimeField = ReflectUtil.getField(obj.getClass(), endTimeFieldName);

        if (startTimeField != null) {
            processDateTimeField(obj, startTimeField, startTimeFieldName, "yyyy-MM-dd 00:00:00");
        }

        if (endTimeField != null) {
            processDateTimeField(obj, endTimeField, endTimeFieldName, "yyyy-MM-dd 23:59:59");
        }
    }

    /**
     * Processes a date/time field of the given object, converting its value to the
     * specified target pattern.
     * <p>
     * If the field is annotated with {@link DateTimeFormat}, its pattern is used
     * for parsing; otherwise, the default pattern "yyyy-MM-dd" is used.
     * The field value is then reformatted and set back to the object using the
     * target pattern.
     *
     * @param obj           the object containing the field
     * @param field         the field to process
     * @param fieldName     the name of the field
     * @param targetPattern the target date/time pattern to format the value
     */
    private static void processDateTimeField(Object obj, Field field, String fieldName, String targetPattern) {
        Object fieldValue = ReflectUtil.getFieldValue(obj, fieldName);
        if (fieldValue != null) {

            String pattern = field.isAnnotationPresent(DateTimeFormat.class)
                    ? field.getAnnotation(DateTimeFormat.class).pattern()
                    : "yyyy-MM-dd";

            DateTime dateTime = DateUtil.parse(StrUtil.toString(fieldValue), pattern);

            ReflectUtil.setFieldValue(obj, fieldName, dateTime.toString(targetPattern));
        }
    }
}

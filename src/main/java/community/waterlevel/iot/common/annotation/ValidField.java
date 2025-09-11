package community.waterlevel.iot.common.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import community.waterlevel.iot.core.validator.FieldValidator;

/**
 * Custom validation annotation to check if a field's value is legal.
 * Supports specifying allowed values and integrates with Jakarta Bean Validation.
 * Used on fields or parameters to enforce value constraints at runtime.

 *
 * @author Ray.Hao
 * @since 2.18.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Documented
@Constraint(validatedBy = FieldValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidField {

    /**
     * Error message to be returned when the field value is invalid.
     * 
     * @return error message
     */
    String message() default "Illegal fields";

    /**
     * Validation groups for advanced constraint grouping.
     * 
     * @return validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Payload for clients to specify additional information about the validation
     * failure.
     * 
     * @return payload classes
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * List of allowed values for the field.
     * 
     * @return array of allowed values
     */
    String[] allowedValues();

}

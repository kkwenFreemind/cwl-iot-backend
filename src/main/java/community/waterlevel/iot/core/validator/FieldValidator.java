package community.waterlevel.iot.core.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import community.waterlevel.iot.common.annotation.ValidField;

/**
 * FieldValidator is a custom constraint validator for validating that a string
 * field's value is within a predefined set of allowed values.
 * <p>
 * Used in conjunction with the
 * {@link community.waterlevel.iot.common.annotation.ValidField} annotation,
 * this validator checks if the field value matches any of the allowed values
 * specified in the annotation.
 * Returns true if the value is null (to allow optional fields) or if it is
 * present in the allowed values list.
 *
 * @author Ray.Hao
 * @since 2024/11/18
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public class FieldValidator implements ConstraintValidator<ValidField, String> {

    /**
     * The list of allowed values specified by the {@link ValidField} annotation.
     */
    private String[] allowedValues;

    @Override
    /**
     * Initializes the validator by retrieving the allowed values from the annotation.
     *
     * @param constraintAnnotation the annotation instance for a given constraint declaration
     */
    public void initialize(ValidField constraintAnnotation) {
        this.allowedValues = constraintAnnotation.allowedValues();
    }

    @Override
    /**
     * Checks if the provided value is valid according to the allowed values.
     *
     * @param value   the value to validate
     * @param context context in which the constraint is evaluated
     * @return {@code true} if the value is {@code null} or is contained in the allowed values; {@code false} otherwise
     */
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Allow null values if permitted by the field definition
        }
        return Arrays.asList(allowedValues).contains(value);
    }
}

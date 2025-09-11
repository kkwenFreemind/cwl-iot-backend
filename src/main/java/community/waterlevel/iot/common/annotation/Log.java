package community.waterlevel.iot.common.annotation;

import java.lang.annotation.*;
import community.waterlevel.iot.common.enums.LogModuleEnum;

/**
 * Annotation for recording operation logs on methods.
 * Supports specifying the log description, module, and whether to log method parameters and results.
 * Commonly used for auditing, monitoring, and business operation tracking in REST APIs.
 *
 * @author Ray
 * @since 2024/6/25
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Log {

    /**
     * Description of the log event or business operation.
     * 
     * @return log description
     */
    String value() default "";

    /**
     * Module to which the log belongs (for categorization).
     * 
     * @return log module enum
     */
    LogModuleEnum module();

    /**
     * Whether to record method parameters in the log.
     * 
     * @return true to log parameters, false otherwise
     */
    boolean params() default true;

    /**
     * Whether to record the method result/return value in the log.
     * 
     * @return true to log result, false otherwise
     */
    boolean result() default false;

}
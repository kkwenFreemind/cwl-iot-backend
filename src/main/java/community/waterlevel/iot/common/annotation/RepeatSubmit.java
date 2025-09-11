package community.waterlevel.iot.common.annotation;


import java.lang.annotation.*;

/**
 * Annotation to prevent duplicate submissions of a method within a specified timeout period.
 * Commonly used on controller methods to enforce idempotency and improve user experience.
 * The default expiration time is 5 seconds.
 *
 * @author Ray.Hao
 * @since 2.3.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RepeatSubmit {

    /**
     * Expiration time (in seconds) for preventing repeated submissions.
     * Default is 5 seconds.
     * 
     * @return expiration time in seconds
     */
    int expire() default 5;

}

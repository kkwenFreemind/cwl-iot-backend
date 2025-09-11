package community.waterlevel.iot.common.annotation;

import java.lang.annotation.*;

/**
 * Annotation for specifying data access permissions on classes or methods.
 * Supports configuration of department and user aliases and column names for data filtering.
 * Commonly used to enforce data scope restrictions in service or controller layers.
 *
 * @author zc
 * @since 2.0.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface DataPermission {

    /**
     * Data permissions
     * {@link com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor}
     */
    String deptAlias() default "";

    String deptIdColumnName() default "dept_id";

    String userAlias() default "";

    String userIdColumnName() default "create_by";

}
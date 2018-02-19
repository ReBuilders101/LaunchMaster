package dev.lb.launchmaster;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation für main-Parameter
 *
 * @author Lars Bündgen
 * @version 1.0
 */

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Param
{
    String desc();
    double def() default 0;
    String defStr() default "";
    double min() default Double.NaN;
    double max() default Double.NaN;
}

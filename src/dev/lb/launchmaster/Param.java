package dev.lb.launchmaster;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import dev.lb.launchmaster.Binding.BindingType;
import dev.lb.launchmaster.Binding.BindingWarning;

/**
 * Annotation for paramters of the main method
 * <br>
 * Fields: <br>
 * desc - A description of the paramter <br>
 * def - A numeric default value <br>
 * defStr - A String default value <br>
 * min - A numeric minimum, or the Strings minimal length <br>
 * max - A numeric maximum, or the Strings maximal length <br>
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
    String[] combo() default {};
    Binding[] bind() default @Binding(param="",bind=BindingType.NULL,warn=BindingWarning.NULL);
}

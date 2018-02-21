package dev.lb.launchmaster;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

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
    String id() default "";
    double def() default 0;
    String defStr() default "";
    double min() default Double.NaN;
    double max() default Double.NaN;
    String[] combo() default {};
    //Bind[] bind() default @Bind(to="",bind=BindingType.NULL,warn=BindingWarning.NULL);
}

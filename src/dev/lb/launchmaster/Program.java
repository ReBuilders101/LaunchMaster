package dev.lb.launchmaster;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for Subprograms. 
 * <br>
 * Fields: <br>
 * desc - Description of the program
 * value - Name of the program
 * 
 * @author Lars Bündgen
 * @version 1.2
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Program
{
    String desc();
    String name();
}

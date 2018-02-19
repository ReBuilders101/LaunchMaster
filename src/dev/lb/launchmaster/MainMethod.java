package dev.lb.launchmaster;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a Subprograms main method
 * @author Lars Bündgen
 * @version 1.0
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface MainMethod{
    
}

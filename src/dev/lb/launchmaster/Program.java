package dev.lb.launchmaster;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation für Unterprogramme. Werden zur Laufzeit per Reflection zu SubProgram-Objekten konvertiert
 * <br>
 * Werte: <br>
 * desc - Beschreibung des Programmes - default: 'Keine Beschreibung'
 * value - Der Name des Programmes 
 * 
 * @author Lars Bündgen
 * @version 1.0
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Program
{
    String desc();
    String name();
}

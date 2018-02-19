package dev.lb.launchmaster;

import java.util.function.Consumer;
import java.util.List;
import java.util.Arrays;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

/**
 * Implementierende Klassen sind eigenständige Programme, die Informationen über
 * ihre Aufrufparameter für ein Launcher-Programm bereitstellen
 *
 * @author Lars Bündgen
 * @version 1.0
 */
public final class SubProgram
{
    private Consumer<Object[]> mainmethod;
    private String name;
    private String description;
    private List<Parameter> params;
    
    private SubProgram(String name, String description, Consumer<Object[]> mainmethod, List<Parameter> params){
        this.name = name;
        this.description = description;
        this.mainmethod = mainmethod;
        this.params = params;
    }
    
    /**
     * Die Main-Methode des Unterprogrammes, die zum Starten aufgerufen werden soll.
     * Die Methode muss null als Parameter akzeptieren.
     */
    public void startProgram(Object...args){
        mainmethod.accept(args);
    }
    /**
     * Eine Liste von möglichen Startparametern
     */
    public List<Parameter> getLaunchParameters(){
        return params;
    }
    /**
     * Eine Beschreibung des Programmes, kann in HTML formatiert sein
     */
    public String getDescription(){
        return description;
    }
    /**
     * Der Anzeigename des Programmes
     */
    public String getName(){
        return name;
    }
    
    public String toString(){
        return name;
    }
    
    public static SubProgram create(String name, String description, Consumer<Object[]> mainmethod, Parameter...params){
        return new SubProgram(name, description, mainmethod, Arrays.asList(params));
    }
    public static SubProgram create(String name, String description, Consumer<Object[]> mainmethod, List<Parameter> params){
        return new SubProgram(name, description, mainmethod, params);
    }
    public static SubProgram create(Class<?> clazz) throws Exception{
        if(!clazz.isAnnotationPresent(Program.class)){
            throw new Exception("The class " + clazz.getName() + " does not implement the @Program annotation and can't be used");
        }else{
            Program p = clazz.getDeclaredAnnotation(Program.class);
            String desc = p.desc();
            String name = p.name();
            //Method
            Method[] methods = clazz.getDeclaredMethods();
            Method main1 = null;
            for(Method m : methods){
                if(m.isAnnotationPresent(MainMethod.class) && Modifier.isStatic(m.getModifiers())){
                    main1 = m;
                    break;
                }
            }
            final Method main = main1; //Sonst kann diese Variable nicht in der Lambda-Expression unten verwendet werden
            
            if(main == null){
                throw new Exception("Found no static Method with the @MainMethod annotation present");
            }
            Annotation[][] paramAnnotations = main.getParameterAnnotations();
            Parameter[] paramObjects = new Parameter[main.getParameterCount()];
            Class<?>[] paramTypes = main.getParameterTypes();
            for(int i = 0; i < main.getParameterCount(); i++){
                Class<?> typeClass = paramTypes[i];

                Param param = null;
                for(Annotation a : paramAnnotations[i]){
                    if(a instanceof Param) param = (Param) a;
                }
                if(param == null) throw new Exception("Argument " + i + " is missing a @Param annotation");
                
                Object defVal = null;
                Parameter.Type type = null;
                if(typeClass == int.class){
                    type = Parameter.Type.INT;
                    defVal = param.def();
                }else if(typeClass == float.class || typeClass == double.class){
                    type = Parameter.Type.FLOAT;
                    defVal = param.def();
                }else if(typeClass == String.class){
                    type = Parameter.Type.STRING;
                    defVal = param.defStr();
                }else if(typeClass == boolean.class){
                    type = Parameter.Type.BOOLEAN;
                    defVal = param.def() != 0;
                }else{
                    throw new Exception("All arguments must be double, float, boolean, String or int, but argument " + i + " was: " + typeClass.getName());
                }
                
                paramObjects[i] = new Parameter(param.desc(), type, param.min(), param.max(), defVal);
            }
            return SubProgram.create(name, desc, (a) -> {
                try{
                    main.invoke(null, a);
                }catch(Exception e){
                    throw new RuntimeException(e);
                }
            }, paramObjects);
        }
    }
}

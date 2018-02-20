package dev.lb.launchmaster;

import java.util.function.Consumer;
import java.util.List;
import java.util.Arrays;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

/**
 * @author Lars Bündgen
 * @version 1.0
 */
final class SubProgram {
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
    
    public void startProgram(Object...args){
        mainmethod.accept(args);
    }

    public List<Parameter> getLaunchParameters(){
        return params;
    }

    public String getDescription(){
        return description;
    }

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
    
    public static SubProgram create(Class<?> clazz) throws AnnotationParsingException{
        if(!clazz.isAnnotationPresent(Program.class)){
            throw new AnnotationParsingException("The class " + clazz.getName() + " does not implement the @Program annotation and can't be used", clazz, null);
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
            final Method main = main1; //That's reqiured for the lambda
            
            if(main == null){
                throw new AnnotationParsingException("Found no static Method with the @MainMethod annotation present", clazz, null);
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
                if(param == null) throw new AnnotationParsingException("Argument " + i + " is missing a @Param annotation", clazz, null);
                paramObjects[i] = Parameter.create(param, typeClass, clazz);
            }
            return SubProgram.create(name, desc, (a) -> {
            	try {
					main.invoke(null, a);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}, paramObjects);
        }
    }
}

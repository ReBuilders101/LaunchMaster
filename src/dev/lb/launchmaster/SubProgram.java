package dev.lb.launchmaster;

import java.util.function.Consumer;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

/**
 * @author Lars Bündgen
 * @version 1.0
 */
class SubProgram {
    private Consumer<Object[]> mainmethod;
    private String name;
    private String description;
    private List<Parameter> params;
    private Map<String,Parameter> mappedParams;
    
    //ALWAYS CALL INIT
    private SubProgram(){
    }
    
    private void init(String name, String description, Consumer<Object[]> mainmethod, List<Parameter> params, Map<String,Parameter> mappedParams){
        this.name = name;
        this.description = description;
        this.mainmethod = mainmethod;
        this.params = params;
        this.mappedParams = mappedParams;
    }
    
    public void startProgram(Object...args){
        mainmethod.accept(args);
    }

    public List<Parameter> getLaunchParameters(){
        return Collections.unmodifiableList(params);
    }
    
    public Map<String,Parameter> getMappedParameters(){
    	return Collections.unmodifiableMap(mappedParams);
    }
    
    public Parameter getMappedParameter(String name){
    	return mappedParams.get(name);
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
    
    public boolean updateUI(boolean message){
    	//System.out.println("Update");
    	for(Parameter p : this.params){
    		if(!p.updateUI(this, message)) return false;
    	}
    	return true;
    }
    
    public static SubProgram create(Class<?> clazz) throws AnnotationParsingException{
        if(!clazz.isAnnotationPresent(Program.class)){
            throw new AnnotationParsingException("The class " + clazz.getName() + " does not implement the @Program annotation and can't be used", clazz, null);
        }else{
            Program p = clazz.getDeclaredAnnotation(Program.class);
            SubProgram sp = new SubProgram();
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
            Map<String,Parameter> paramMap = new HashMap<>();
            Parameter[] paramObjects = new Parameter[main.getParameterCount()];
            Class<?>[] paramTypes = main.getParameterTypes();
            for(int i = 0; i < main.getParameterCount(); i++){
                Class<?> typeClass = paramTypes[i];

                Param param = null;
                for(Annotation a : paramAnnotations[i]){
                    if(a instanceof Param) param = (Param) a;
                }
                if(param == null) throw new AnnotationParsingException("Argument " + i + " is missing a @Param annotation", clazz, null);
                paramObjects[i] = Parameter.create(param, typeClass, clazz,sp);
                if(param.id() != "" && !paramMap.containsKey(param.id())){
                	paramMap.put(param.id(), paramObjects[i]); //Put if mapped with name
                }
            }
            sp.init(name, desc, (a) -> {
            	try {
					main.invoke(null, a);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}, Arrays.asList(paramObjects), paramMap);
            return sp;
        }
    }
}

package dev.lb.launchmaster;

/**
 * 
 * @author Lars Bündgen
 * @version 1.1
 */

class Parameter{
    private Type type;
    private String desc;
    private double min;
    private double max;
    private Object def;

    public Parameter(String desc, Type type, double min, double max, Object def){
        this.type = type;
        this.desc = desc;
        this.min = min;
        this.max = max;
        this.def = def;
    }

    public Type getParamType(){
        return type;
    }

    public String getDescription(){
        return desc;
    }
    
    public double getMinimumValue(){
        return min;
    }
    
    public double getMaximumValue(){
        return max;
    }
    
    public Object getDefaultValue(){
        return def;
    }
    
    public static enum Type{
        INT,FLOAT,STRING,BOOLEAN;
    }
}

package dev.lb.launchmaster;

/**
 * Beschreibt einen Startparamter für eine Main-Methode
 *
 * @author Lars Bündgen
 * @version 1.1
 */

public class Parameter{
    private Type type;
    private String desc;
    private double min;
    private double max;
    private Object def;

    /**
     * Erstellt einen neuen Parameter
     * @param code Der Parameter (z.B. -radius oder -hoehe)
     * @param desc Eine Parameterbeschreibung
     */
    public Parameter(String desc, Type type, double min, double max, Object def){
        this.type = type;
        this.desc = desc;
        this.min = min;
        this.max = max;
        this.def = def;
    }

    /**
     * Gibt den Parameter-Typ zurück
     * @return Den Parametertyp
     */
    public Type getParamType(){
        return type;
    }

    /**
     * Gibt eine Beschreibung für diesen Parameter an
     * @return Die Parameterbeschreibung
     */
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

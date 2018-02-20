package dev.lb.launchmaster;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 * 
 * @author Lars Bündgen
 * @version 1.1
 */

class Parameter{
	
	public static final BufferedImage errorImage;
	
	static{
		errorImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = errorImage.createGraphics();
		g.setColor(Color.YELLOW);
		g.fillPolygon(new int[]{16, 4, 28}, new int[]{4, 28, 28}, 3);
		g.setColor(Color.DARK_GRAY);
		g.drawPolygon(new int[]{16, 4, 28}, new int[]{4, 28, 28}, 3);
		g.drawLine(16, 12, 16, 20);
	}
	
    private Type type;
    private String desc;
    private double min;
    private double max;
    private Object def;
    private Class<? extends Enum<?>> enumClass;
    private String[] strEnumVals;
    private JComponent component;
    private JComponent error; //For the error symbol

    protected Parameter(String desc, Type type, double min, double max, Object def, Class<? extends Enum<?>> enc, String[] en){
        this.type = type;
        this.desc = desc;
        this.min = min;
        this.max = max;
        this.def = def;
        this.enumClass = enc;
        this.strEnumVals = en;
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
    
    public JComponent createComponent(Dimension preferredLabelSize, Dimension preferredControlSize){
    	JLabel label = new JLabel(desc);
    	label.setToolTipText(desc);
    	if(preferredLabelSize != null) label.setPreferredSize(preferredLabelSize);
    	if(preferredControlSize != null){
    		component.setPreferredSize(preferredControlSize);
    		error.setPreferredSize(new Dimension(preferredControlSize.height, preferredControlSize.height));
    	}
    	JPanel con = new JPanel();
    	con.add(label);
    	con.add(component);
    	con.add(error);
    	return con;
    }
    
    private void createControl(){
    	try{
    		switch (getParamType()) {
    			case BOOLEAN: component = new JCheckBox("Add this paramter");
    				break;
    			case BYTE: component = new JSpinner(new SpinnerNumberModel((byte) (double) def, Double.isNaN(min) ? Byte.MIN_VALUE : (byte) min,
    				Double.isNaN(max) ? Byte.MAX_VALUE : (byte) max, 1)); 	
    				break;
    			case DOUBLE: component = new JSpinner(new SpinnerNumberModel((double) def, Double.isNaN(min) ?	Double.MIN_VALUE : (double) min,
    				Double.isNaN(max) ? Double.MAX_VALUE : (double) max, 1));
    				break;
    			case ENUM: Enum<?>[] constants = enumClass.getEnumConstants();
    					component = new JComboBox<>(constants);
    					((JComboBox<?>) component).setSelectedIndex((int) (double) def);
    					break;
    			case STRENUM: component = new JComboBox<>(strEnumVals);
					((JComboBox<?>) component).setSelectedIndex((int) (double) def);
					break;
    			case FLOAT: component = new JSpinner(new SpinnerNumberModel((float) (double) def, Double.isNaN(min) ? Float.MIN_VALUE : (float) min,
    				Double.isNaN(max) ? Float.MAX_VALUE : (float) max, 1));
    				break;
    			case INT: component = new JSpinner(new SpinnerNumberModel((int) (double) def, Double.isNaN(min) ? Integer.MIN_VALUE : (int) min,
    				Double.isNaN(max) ? Integer.MAX_VALUE : (int) max, 1));
    				break;
    			case LONG: component = new JSpinner(new SpinnerNumberModel((long) (double) def, Double.isNaN(min) ? Long.MIN_VALUE : (long) min,
    				Double.isNaN(max) ? Long.MAX_VALUE : (long) max, 1));
    				break;
    			case SHORT: component = new JSpinner(new SpinnerNumberModel((short) (double) def, Double.isNaN(min) ? Short.MIN_VALUE : (short) min,
    				Double.isNaN(max) ? Short.MAX_VALUE : (short) max, 1));
    				break;
    			case STRING: component = new JTextField((String) def);
    				break;
    			default: component = new JLabel("No component associated with this type");
    				break;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	error = new JLabel();
    }
    
    //TODO Add checks
    public Object readValue() throws ValueOutOfRangeException{
    	switch(type){
			case BOOLEAN: return ((JCheckBox) component).isSelected();
			case BYTE: return (byte) ((JSpinner) component).getValue();
			case ENUM: return ((JComboBox<?>) component).getSelectedItem();
			case FLOAT: return (float) ((JSpinner) component).getValue();
			case INT: return (int) ((JSpinner) component).getValue();
			case LONG: return (long) ((JSpinner) component).getValue();
			case SHORT: return (short) ((JSpinner) component).getValue();
			case DOUBLE: return (double) ((JSpinner) component).getValue();
			case STRENUM: return ((JComboBox<?>) component).getSelectedItem();
			case STRING: return ((JTextField) component).getText();
			default: return null;
    	}
    }
    
    public Class<? extends Enum<?>> getEnumClass(){
    	return enumClass;
    }
    
    /**
     * 
     * @param p The parameters annotation
     * @param paramType The parameters type
     * @param traceClass The class containing the method with this parameter. Only used for error tracing
     * @return
     * @throws AnnotationParsingException
     */
    @SuppressWarnings("unchecked")
	public static Parameter create(Param p, Class<?> paramType, Class<?> traceClass) throws AnnotationParsingException{
    	Type type = Type.getType(paramType, p);
    	if(type == null){
    		throw new AnnotationParsingException("Found Parameter with invalid type: " + paramType.getName(),traceClass,p);
    	}
    	Class<? extends Enum<?>> enumClass = null;
    	String[] strEn = null;
    	if(type == Type.ENUM){
    		enumClass = (Class<? extends Enum<?>>) paramType;
    	}else if(type == Type.STRENUM){
    		strEn = p.combo();
    	}
    	String description = p.desc();
    	double minVal = p.min();
    	double maxVal = p.max();
    	Object defVal = type == Type.STRING ? p.defStr() : p.def(); 
    	
    	Parameter par = new Parameter(description, type, minVal, maxVal, defVal, enumClass, strEn);
    	par.createControl();
    	return par;
    }
    
    
    public static enum Type{
        INT,FLOAT,STRING,BOOLEAN,SHORT,BYTE,DOUBLE,LONG,ENUM,STRENUM;
    	
    	public static Type getType(Class<?> c, Param p){
    		if(c == int.class) return INT;
    		if(c == float.class) return FLOAT;
    		if(c == String.class) return p.combo().length > 0 ? STRENUM : STRING;
    		if(c == boolean.class) return BOOLEAN;
    		if(c == short.class) return SHORT;
    		if(c == byte.class) return BYTE;
    		if(c == double.class) return DOUBLE;
    		if(c == long.class) return LONG;
    		if(c.isEnum()) return ENUM;
    		return null;
    	}
    }
}

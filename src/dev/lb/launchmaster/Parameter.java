package dev.lb.launchmaster;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;

import dev.lb.launchmaster.Bind.BindingType;
import dev.lb.launchmaster.Bind.BindingWarning;

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
    private List<Binding> bindings;
    private String[] strEnumVals;
    private JComponent component;
    private JComponent error; //For the error symbol

    protected Parameter(String desc, Type type, double min, double max, Object def, Class<? extends Enum<?>> enc, String[] en, List<Binding> bindings){
        this.type = type;
        this.desc = desc;
        this.min = min;
        this.max = max;
        this.def = def;
        this.enumClass = enc;
        this.strEnumVals = en;
        this.bindings = bindings;
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
    
    public List<Binding> getBindings(){
    	return Collections.unmodifiableList(bindings);
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
    	con.setMaximumSize(new Dimension(con.getMaximumSize().width, Math.max(preferredControlSize.height, preferredLabelSize.height)));
    	return con;
    }
    
    private void createControl(SubProgram updateHandler){
    	try{
    		switch (getParamType()) {
    			case BOOLEAN: component = new JCheckBox("Add this paramter");
    				break;
    			case BYTE: component = new JSpinner(new SpinnerNumberModel((byte) (double) def, Double.isNaN(min) ? Byte.MIN_VALUE : (byte) min,
    				Double.isNaN(max) ? Byte.MAX_VALUE : (byte) max, 1));
    				break;
    			case DOUBLE: component = new JSpinner(new SpinnerNumberModel((double) def, Double.isNaN(min) ?	Double.MIN_VALUE : (double) min,
    				Double.isNaN(max) ? Double.MAX_VALUE : (double) max, 0.1));
    				break;
    			case ENUM: Enum<?>[] constants = enumClass.getEnumConstants();
    					component = new JComboBox<>(constants);
    					((JComboBox<?>) component).setSelectedIndex((int) (double) def);
    					break;
    			case STRENUM: component = new JComboBox<>(strEnumVals);
					((JComboBox<?>) component).setSelectedIndex((int) (double) def);
					break;
    			case FLOAT: component = new JSpinner(new SpinnerNumberModel((float) (double) def, Double.isNaN(min) ? Float.MIN_VALUE : (float) min,
    				Double.isNaN(max) ? Float.MAX_VALUE : (float) max, 0.1));
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
    		if(component instanceof JCheckBox){
    			((JCheckBox) component).addActionListener((e) -> updateHandler.updateUI(false));
    		}else if(component instanceof JSpinner){
    			((DefaultFormatter) ((JFormattedTextField) ((JSpinner) component).getEditor()
    				.getComponent(0)).getFormatter()).setCommitsOnValidEdit(true); //Thats a lot of casts
				((JSpinner) component).addChangeListener((e) -> updateHandler.updateUI(false));
    		}else if(component instanceof JTextField){
    			((JTextField) component).getDocument().addDocumentListener(new DocumentListener() { //WHY SWING???
    				
					@Override
					public void removeUpdate(DocumentEvent e) {
						updateHandler.updateUI(false);
					}
					
					@Override
					public void insertUpdate(DocumentEvent e) {
						updateHandler.updateUI(false);
					}
					
					@Override
					public void changedUpdate(DocumentEvent e) {
						updateHandler.updateUI(false);
					}
				});
    		}else if(component instanceof JComboBox){
    			((JComboBox<?>) component).addActionListener((e) -> updateHandler.updateUI(false));
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	error = new JLabel();
    }
    
    public Object readValue() throws ValueOutOfRangeException{
    	switch(type){
			case BOOLEAN: return ((JCheckBox) component).isSelected();
			case BYTE: byte retB = (byte) ((JSpinner) component).getValue();
				if(retB < min || retB > max) throw new ValueOutOfRangeException("<html>The value for '" + desc + "' is not in the specified range:<br>Minimum: " +
						min + "<br>Maximum: " + max + "<br>Value: " + retB, desc, retB, min + "|" + max);
				return retB;
			case ENUM: Object retO = ((JComboBox<?>) component).getSelectedItem();
				return retO;
			case FLOAT: float retF = (float) ((JSpinner) component).getValue();
				if(retF < min || retF > max) throw new ValueOutOfRangeException("<html>The value for '" + desc + "' is not in the specified range:<br>Minimum: " +
					min + "<br>Maximum: " + max + "<br>Value: " + retF, desc, retF, min + "|" + max);
				return retF;
			case INT: int retI = (int) ((JSpinner) component).getValue();
				if(retI < min || retI > max) throw new ValueOutOfRangeException("<html>The value for '" + desc + "' is not in the specified range:<br>Minimum: " +
					min + "<br>Maximum: " + max + "<br>Value: " + retI, desc, retI, min + "|" + max);
				return retI;
			case LONG: long retL = (long) ((JSpinner) component).getValue();
				if(retL < min || retL > max) throw new ValueOutOfRangeException("<html>The value for '" + desc + "' is not in the specified range:<br>Minimum: " +
					min + "<br>Maximum: " + max + "<br>Value: " + retL, desc, retL, min + "|" + max);
				return retL;
			case SHORT: short retS = (short) ((JSpinner) component).getValue();
				if(retS < min || retS > max) throw new ValueOutOfRangeException("<html>The value for '" + desc + "' is not in the specified range:<br>Minimum: " +
					min + "<br>Maximum: " + max + "<br>Value: " + retS, desc, retS, min + "|" + max);
				return retS;
			case DOUBLE: double retD = (double) ((JSpinner) component).getValue();
				if(retD < min || retD > max) throw new ValueOutOfRangeException("<html>The value for '" + desc + "' is not in the specified range:<br>Minimum: " +
					min + "<br>Maximum: " + max + "<br>Value: " + retD, desc, retD, min + "|" + max);
				return retD;
			case STRENUM: Object retSte = ((JComboBox<?>) component).getSelectedItem();
				//Actually nothing to check, bc min and max dont apply here and the combobox is uneditable and preselected
				return retSte;
			case STRING: String retStr = ((JTextField) component).getText();
				if(retStr.length() < min || retStr.length() > max) throw new ValueOutOfRangeException("<html>The length of '" + desc + "' is not in the specified range:<br>Minimum: " +
					min + "<br>Maximum: " + max + "<br>Value: " + retStr.length(), desc, retStr.length(), min + "|" + max);
				return retStr;
			default: return null;
    	}
    }
    
    /**
     * Check if the value is still valid, display/hide error icon, handle bindings
     */
    public boolean updateUI(SubProgram sp, boolean message){
    	boolean valid = true;
    	boolean removeWarn = true;
    	for(Binding b : bindings){
    		JComponent dependant = sp.getMappedParameter(b.getDependantName()).component;
    		if(dependant == null) continue;
    		if(!b.validate(component, dependant)){
    			if(message && b.getWarnOnLaunch()){
    				JOptionPane.showMessageDialog(SwingUtilities.getRoot(component), "<html>The following condition is not fulfilled: <br>" + 
    					"The value of '" + this.desc +"' has to be " + b.getBindingType() + " the value of '" + sp.getMappedParameter(b.getDependantName()).desc +
    					"'.",
    				"Launch", JOptionPane.ERROR_MESSAGE);
    			}
    			if(b.getWarning() == BindingWarning.DISABLE){
    				component.setEnabled(false);
    			}else if(b.getWarning() == BindingWarning.WARN){
    				((JLabel) error).setIcon(new ImageIcon(errorImage.getScaledInstance(error.getPreferredSize().width, error.getPreferredSize().height, Image.SCALE_SMOOTH)));
    			}
    			valid = !b.getWarnOnLaunch(); //Only cancel launch if this is true
    			removeWarn = false;
    		}
    	}
    	if(removeWarn){//Iterate again to remove warnings
    		for(Binding b: bindings){
    			if(b.getWarning() == BindingWarning.DISABLE){
    				component.setEnabled(true);    				
    			}else if(b.getWarning() == BindingWarning.WARN){
    				((JLabel) error).setIcon(null);
    			}
    		}
    	}
    	return valid;
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
	public static Parameter create(Param p, Class<?> paramType, Class<?> traceClass, SubProgram updateHandler, List<Bind> bindAnnotations) throws AnnotationParsingException{
    	Type type = Type.getType(paramType, p);
    	if(type == null){
    		throw new AnnotationParsingException("Found Parameter with invalid type: " + paramType.getName(),traceClass,p);
    	}
    	Class<? extends Enum<?>> enumClass = null;
    	List<Binding> binds = new ArrayList<>();
    	for(Bind b : bindAnnotations){
    		if(b.bind() != BindingType.NULL)
    			binds.add(Binding.create(b));
    	}
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
    	
    	Parameter par = new Parameter(description, type, minVal, maxVal, defVal, enumClass, strEn, binds);
    	par.createControl(updateHandler);
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

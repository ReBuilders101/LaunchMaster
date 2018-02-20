package dev.lb.launchmaster;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import dev.lb.launchmaster.Bind.BindingType;
import dev.lb.launchmaster.Bind.BindingWarning;

class Binding {
	
	private String dep;
	private double val;
	private BindingType type;
	private BindingWarning warning;
	
	private Binding(String dep, BindingType type, BindingWarning warning) {
		this.dep = dep;
		this.type = type;
		this.warning = warning;
	}
	
	public boolean validate(JComponent main, JComponent dependant) throws ValueOutOfRangeException{
		if(hasNum(main) && hasNum(dependant)){
			return validate(readNum(main), readNum(dependant));
		}else if(dependant instanceof JCheckBox){ //If dependant is a checkbox, only look for t/f
			return validate(((JCheckBox) dependant).isSelected());
		}else if(dependant instanceof JComboBox<?>){
			int index = ((JComboBox<?>) dependant).getSelectedIndex();
			switch(type){
			case IS: return index == (int) val;
			case ISNOT: return index != (int) val;
			default: return true;
			}
		}
		return false; //STRINGS,
	}
	
	private boolean hasNum(JComponent comp){
		return comp instanceof JSpinner || comp instanceof JTextField;
	}
	
	private double readNum(JComponent comp){
		if(comp instanceof JSpinner) return (double) ((JSpinner) comp).getValue();
		if(comp instanceof JTextField) return (double) ((JTextField) comp).getText().length();
		return 0;
	}
	
	
	public boolean validate(boolean check){
		switch (type) {
		case FALSE: return !check;
		case TRUE: return check;
		default: return true;
		}
	}
	
	public boolean validate(double main, double dependant){
		switch(type){
		case FALSE: return false; //always valid
		case LESS: return main < dependant;
		case LESSEQ: return main <= dependant;
		case MORE: return main > dependant;
		case MOREEQ: return main >= dependant;
		case IS: return main == dependant;
		case ISNOT: return main != dependant;
		default: return true;
		}
	}
	
	
	public Binding create(Bind b){
		return new Binding(b.to(), b.bind(), b.warn());
	}

	public String getDependantName() {
		return dep;
	}

	public double getCompareValue() {
		return val;
	}

	public BindingType getBindingType() {
		return type;
	}

	public BindingWarning getWarning() {
		return warning;
	}
	
	
}

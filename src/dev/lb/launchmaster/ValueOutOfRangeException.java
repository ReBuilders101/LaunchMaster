package dev.lb.launchmaster;

public class ValueOutOfRangeException extends Exception {
	private static final long serialVersionUID = 1729628431612113751L;
	
	private Object value;
	private Object range;
	private String valueName;
	public ValueOutOfRangeException(String message, String valueName, Object value, Object range){
		super(message);
		this.value = value;
		this.range = range;
		this.valueName = valueName;
	}
	public Object getValue() {
		return value;
	}
	public Object getRange() {
		return range;
	}
	public String getValueName(){
		return valueName;
	}
	
}


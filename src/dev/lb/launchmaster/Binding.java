package dev.lb.launchmaster;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface Binding {
	public String param();
	public BindingType bind();
	public BindingWarning warn();
	
	public enum BindingType{
		MORE,LESS,MOREEQ,LESSEQ,TRUE,FALSE,NULL;
	}
	
	public enum BindingWarning{
		DISABLE,WARN,NULL;
	}
}

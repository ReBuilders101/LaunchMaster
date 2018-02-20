package dev.lb.launchmaster;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;

@Repeatable(Binds.class)
@Retention(RUNTIME)
public @interface Bind {
	String to();
	BindingType bind();
	BindingWarning warn();
	double value() default 0;
	
	public enum BindingType{
		MORE,LESS,MOREEQ,LESSEQ,TRUE,FALSE,NULL,ISNOT,IS;
	}
	
	public enum BindingWarning{
		DISABLE,WARN,NULL;
	}
}

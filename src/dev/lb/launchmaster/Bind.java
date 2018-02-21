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
	boolean block() default true;
	
	public enum BindingType{
		MORE("more tha"),LESS("less than"),MOREEQ("more than or equal to"),LESSEQ("less than or equal to"),
		TRUE("true"),FALSE("false"),NULL("NULL"),ISNOT("not the same as"),IS("the same value as");
		private String name;
		private BindingType(String name){
			this.name= name;
		}
		public String toString(){
			return name;
		}
	}
	
	public enum BindingWarning{
		DISABLE,WARN,NULL;
	}
}

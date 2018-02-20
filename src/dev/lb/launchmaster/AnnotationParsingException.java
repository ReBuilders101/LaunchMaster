package dev.lb.launchmaster;

import java.lang.annotation.Annotation;

public class AnnotationParsingException extends Exception{

	private static final long serialVersionUID = -5553000940462341875L;

	private Class<?> clazz;
	private Annotation anno;
	
	
	public AnnotationParsingException(String message, Class<?> clazz, Annotation anno){
		super(message);
		this.clazz = clazz;
		this.anno = anno;
	}
	
	public Class<?> getParsedClass(){
		return clazz;
	}
	
	public Annotation getParsedAnnotation(){
		return anno;
	}
	
}

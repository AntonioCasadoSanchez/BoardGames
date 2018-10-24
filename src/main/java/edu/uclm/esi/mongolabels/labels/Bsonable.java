package edu.uclm.esi.mongolabels.labels;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Bsonable {
	public final String FK = "FK";
	String StatePattern = "State pattern";

	String type() default "";

}

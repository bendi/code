package pl.bedkowski.code.memoize.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pl.bedkowski.code.memoize.KeyGenerator;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Memoize {
	public enum TYPE{EXCLUDE_ALL, INCLUDE_ALL};

	TYPE type() default TYPE.INCLUDE_ALL;
	Class<? extends KeyGenerator> keyGenerator() default KeyGenerator.class;
}

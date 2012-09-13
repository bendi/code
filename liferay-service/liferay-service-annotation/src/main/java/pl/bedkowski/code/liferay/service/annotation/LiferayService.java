package pl.bedkowski.code.liferay.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface LiferayService {
	public String value() default "portalClassLoader";
	public String initMethod() default "afterPropertiesSet";
}

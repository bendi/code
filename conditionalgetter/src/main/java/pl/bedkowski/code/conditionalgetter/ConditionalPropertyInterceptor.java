package pl.bedkowski.code.conditionalgetter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

public class ConditionalPropertyInterceptor<T> implements MethodInterceptor {

	private T myBean;
	private String suffix;

	public ConditionalPropertyInterceptor(T myBean, String suffix) {
		this.myBean = myBean;
		this.suffix = suffix;
	}

	@SuppressWarnings("unchecked")
	public static <T>  T create(T myBean, String suffix) {
		return (T) Enhancer.create(myBean.getClass(), new ConditionalPropertyInterceptor<T>(myBean, suffix));
	}

	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy methodProxy) throws Throwable {

		String propertyName = getHandledPropertyName(method);
		if (propertyName == null) {
			return method.invoke(myBean, args);
		} else {
			return handleGetter(method, propertyName);
		}
	}

	private Object handleGetter(Method method, String propertyName) throws Throwable {
		String conditionPropertyName = StringUtils.uncapitalize(propertyName) + suffix;
		PropertyDescriptor conditionDescriptor = PropertyUtils.getPropertyDescriptor(myBean, conditionPropertyName);
		if (conditionDescriptor == null) {
			throw new NoSuchMethodException("Missing is"+StringUtils.capitalize(conditionPropertyName) + " method for property: " + StringUtils.uncapitalize(propertyName));
		}
		Method condition = conditionDescriptor.getReadMethod();
		if (condition == null) {
			throw new NoSuchMethodException("Missing is"+StringUtils.capitalize(conditionPropertyName) + " method for property: " + StringUtils.uncapitalize(propertyName));
		}
		if ((Boolean) condition.invoke(myBean)) {
			return method.invoke(myBean);
		}
		return null;
	}

	private String getHandledPropertyName(Method method) {
		String methodName = method.getName();
		if (!(methodName.startsWith("get") ||
			(methodName.startsWith("is") &&
			!methodName.endsWith(suffix)))) {
			return null;
		}
		else if (method.getParameterTypes().length != 0 ||
			void.class == method.getReturnType()) {
			return null;
		}
		else if (methodName.startsWith("get")) {
			return methodName.substring(3);
		} else {
			return methodName.substring(2);
		}
	}
}

package pl.bedkowski.code.liferay.service.processor.dispatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import pl.bedkowski.code.liferay.service.processor.exception.InvalidListenerException;

class MethodProxy<T> {

	private Method m;
	private T target;

	@SuppressWarnings("rawtypes")
	public MethodProxy(T target, String methodName, Class...classes) throws InvalidListenerException {
		try {
			this.target = target;
			m = target.getClass().getDeclaredMethod(methodName, classes);
			m.setAccessible(true);
		} catch (SecurityException e) {
			throw new InvalidListenerException(e);
		} catch (NoSuchMethodException e) {
			throw new InvalidListenerException(e);
		}
	}

	/**
	 *
	 * @param args
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void call(Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		m.invoke(target, args);
	}

	/**
	 *
	 * @return
	 */
	public T getTarget() {
		return target;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodProxy<?> other = (MethodProxy<?>) obj;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

}

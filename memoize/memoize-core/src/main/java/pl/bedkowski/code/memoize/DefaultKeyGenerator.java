package pl.bedkowski.code.memoize;

import java.lang.reflect.Method;

import com.google.common.base.Joiner;

public class DefaultKeyGenerator implements KeyGenerator {

	private static final Joiner j = Joiner.on(",");

	@Override
	public String generateKey(Method method, Object[] args) {
		return String.format(
			"%s.%s(%s)",
			method.getDeclaringClass().getCanonicalName(),
			method.getName(),
			j.join(args)
		);
	}
};
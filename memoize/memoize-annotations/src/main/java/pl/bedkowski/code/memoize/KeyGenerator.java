package pl.bedkowski.code.memoize;

import java.lang.reflect.Method;

public interface KeyGenerator {
	String generateKey(Method method, Object[] args);
}

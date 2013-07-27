package pl.bedkowski.code.memoize.ejb3;

import java.lang.reflect.Method;

import javax.ejb.Local;

import pl.bedkowski.code.memoize.keygenerator.KeyGenerator;

@Local
public interface KeyGeneratorEJB3 extends KeyGenerator {

	/**
	 * <p>
	 * Used to generated caching key based on targetObject, method and params
	 *  </p>
	 *
	 * @param target
	 * @param m
	 * @param params
	 * @return
	 */
	public String generate(Object target, Method m, Object... params);

}

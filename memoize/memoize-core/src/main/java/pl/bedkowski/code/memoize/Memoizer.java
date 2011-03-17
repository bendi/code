package pl.bedkowski.code.memoize;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pl.bedkowski.code.memoize.annotation.Exclude;
import pl.bedkowski.code.memoize.annotation.Include;
import pl.bedkowski.code.memoize.annotation.Memoize;
import pl.bedkowski.code.memoize.annotation.Memoize.TYPE;

public class Memoizer<T> implements InvocationHandler {

	private Log log;
	private KeyGenerator keyGenerator;
	private TYPE type;

	@SuppressWarnings("unchecked")
	public static <T> T memoize(T object, Cache cache, Memoize.TYPE type) {
		return (T) Proxy.newProxyInstance(
			object.getClass().getClassLoader(),
			object.getClass().getInterfaces(),
			new Memoizer<T>(object, cache, (Class<T>)object.getClass(), type)
		);
	}

	@SuppressWarnings("unchecked")
	public static <T> T memoize(T object, Cache cache, Memoize.TYPE type, KeyGenerator keyGenerator) {
		return (T) Proxy.newProxyInstance(
			object.getClass().getClassLoader(),
			object.getClass().getInterfaces(),
			new Memoizer<T>(object, cache, (Class<T>)object.getClass(), type, keyGenerator)
		);
	}


	@SuppressWarnings("unchecked")
	public static <T> T memoize(Class<T> clazz, Cache cache, Memoize.TYPE type) throws InstantiationException, IllegalAccessException {
		T object = clazz.newInstance();
		return (T) Proxy.newProxyInstance(
			clazz.getClassLoader(),
			clazz.getInterfaces(),
			new Memoizer<T>(object, cache, clazz, type)
		);
	}

	@SuppressWarnings("unchecked")
	public static <T> T memoize(Class<T> clazz, Cache cache, Memoize.TYPE type, KeyGenerator keyGenerator) throws InstantiationException, IllegalAccessException {
		T object = clazz.newInstance();
		return (T) Proxy.newProxyInstance(
			clazz.getClassLoader(),
			clazz.getInterfaces(),
			new Memoizer<T>(object, cache, clazz, type, keyGenerator)
		);
	}

	private Memoizer(T target, Cache cache, Class<T> clazz, Memoize.TYPE type) {
		this(target, cache, clazz, type, new DefaultKeyGenerator());
	}

	private Memoizer(T target, Cache cache, Class<T> clazz, Memoize.TYPE type, KeyGenerator keyGenerator) {
		log = LogFactory.getLog(clazz);
		this.target = target;
		this.cache = cache;
		this.keyGenerator = keyGenerator;
		this.type = type;
	}

	private Cache cache;
	private T target;

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public void setTarget(T target) {
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


		if (method.getReturnType().equals(Void.TYPE) || skipCaching(method)) {
			log.info("Skipping caching to method: " + method.getDeclaringClass().getCanonicalName() + "." + method.getName());
			invoke(method, args);
		} else {
			String key = keyGenerator.generateKey(method, args);
			Element element = cache.get(key);

			if (element == null) {
				log.info("Not found result for key: " + key);
				Object result = invoke(method, args);
				element = new Element(key, result);
				cache.put(element);
			} else {
				log.info("Found result for key: " + key);
			}

			return element.getValue();
		}
		return null;
	}

	private Object invoke(Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(target, args);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private boolean skipCaching(Method method) throws SecurityException, NoSuchMethodException {
		Method realMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
		boolean skipCaching = false;
		switch(type) {
		case EXCLUDE_ALL:
			skipCaching = realMethod.getAnnotation(Include.class) != null;
			break;
		case INCLUDE_ALL:
		default:
			skipCaching = realMethod.getAnnotation(Exclude.class) != null;
			break;
		}
		return skipCaching;
	}
}

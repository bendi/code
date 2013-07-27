package pl.bedkowski.code.memoize.ejb3;

import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 * <p>
 * Interceptor aggregating {@link CacheManager} and {@link KeyGeneratorEJB3} in order to provide method cache.
 * </p>
 *
 */
public class CachingInterceptor {

	@EJB
	private CacheManager<String> cacheManager;

	@EJB
	private KeyGeneratorEJB3 keyGenerator;

	@AroundInvoke
	public Object cache(InvocationContext ctx) throws java.lang.Exception {
		String key = keyGenerator.generate(ctx.getTarget(), ctx.getMethod(), ctx.getParameters());
		Object value = cacheManager.get(key);
		if (value != null) {
			return value;
		}
		return ctx.proceed();
	}
}

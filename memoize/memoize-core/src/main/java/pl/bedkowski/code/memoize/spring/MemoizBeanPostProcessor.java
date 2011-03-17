package pl.bedkowski.code.memoize.spring;

import net.sf.ehcache.Cache;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import pl.bedkowski.code.memoize.DefaultKeyGenerator;
import pl.bedkowski.code.memoize.KeyGenerator;
import pl.bedkowski.code.memoize.Memoizer;
import pl.bedkowski.code.memoize.annotation.Memoize;

public class MemoizBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

	private BeanFactory beanFactory;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		Class<?> clz = bean.getClass();
		Memoize memoize = clz.getAnnotation(Memoize.class);
		if (memoize == null) {
			return bean;
		}
		Cache cache = beanFactory.getBean(UseMethodCacheTagParser.CACHE_MANAGER_ID, Cache.class);

		Class<? extends KeyGenerator> keyGeneratorClazz = memoize.keyGenerator();
		if (keyGeneratorClazz == null || keyGeneratorClazz == KeyGenerator.class) {
			keyGeneratorClazz = DefaultKeyGenerator.class;
		}
		KeyGenerator keyGenerator = null;
		try {
			keyGenerator = keyGeneratorClazz.newInstance();
		} catch (Exception e) {
			throw new BeanInitializationException("Cannot create keyGenerator", e);
		}
		return Memoizer.memoize(bean, cache, memoize.type(), keyGenerator);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}

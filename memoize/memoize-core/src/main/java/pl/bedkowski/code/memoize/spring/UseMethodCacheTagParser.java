package pl.bedkowski.code.memoize.spring;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.w3c.dom.Element;

public class UseMethodCacheTagParser extends
		AbstractSingleBeanDefinitionParser {

	public static final String CACHE_MANAGER_FACTORY_ID = "cacheManager";
	public static final String CACHE_MANAGER_ID = "methodCache";

	@Override
	protected Class<MemoizBeanPostProcessor> getBeanClass(Element element) {
		return MemoizBeanPostProcessor.class;
	}

	@Override
	protected void doParse(Element element, ParserContext ctx, BeanDefinitionBuilder builder) {
		super.doParse(element, ctx, builder);
		String cacheConfig = (String) element.getAttribute("config-location");

		BeanDefinitionRegistry registry = ctx.getRegistry();

		if (!registry.containsBeanDefinition(CACHE_MANAGER_FACTORY_ID)) {
			BeanDefinitionBuilder initializer = BeanDefinitionBuilder.rootBeanDefinition(EhCacheManagerFactoryBean.class);
			initializer.addPropertyValue("configLocation", cacheConfig);
			registry.registerBeanDefinition(CACHE_MANAGER_FACTORY_ID, initializer.getBeanDefinition());
		}

		String cacheName = (String) element.getAttribute("cache-name");
		if (StringUtils.isNotEmpty(cacheName) && !registry.containsBeanDefinition(cacheName)) {
			BeanDefinitionBuilder initializer = BeanDefinitionBuilder.rootBeanDefinition(EhCacheFactoryBean.class);
			initializer.addPropertyReference("cacheManager", CACHE_MANAGER_FACTORY_ID);
			initializer.addPropertyValue("cacheName", cacheName);
			registry.registerBeanDefinition(CACHE_MANAGER_ID, initializer.getBeanDefinition());
		}

	}
}
package pl.bedkowski.code.beantransformer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

public class BeanTransformer {

	public static interface Converter<T> extends org.apache.commons.beanutils.Converter {}
	private static class ConverterImpl<T> implements Converter<T> {
		private Class<T> self;
		private Converter<T> target;
		public ConverterImpl(Class<T> self, Converter<T> target) {
			this.self = self;
			this.target = target;
		}
		@SuppressWarnings("unchecked")
		public Object convert(Class arg0, Object arg1) {
			if (self.isAssignableFrom(arg1.getClass())) {
				return arg1;
			}
			return target.convert(arg0, arg1);
		}
	}

	public static class NotUsedPropertiesException extends Exception {
		private final static Joiner comma = Joiner.on(",");
		private static final long serialVersionUID = 1L;

		public NotUsedPropertiesException() {
		}

		NotUsedPropertiesException(String msg) {
			super(msg);
		}
		
		NotUsedPropertiesException(Collection<String> c) {
			super(comma.join(c));
		}

		NotUsedPropertiesException(Throwable e) {
			super(e);
		}
	}

	private Set<String> skipProperites = new HashSet<String>();
	private Map<String, String> renameProperties = new HashMap<String, String>();
	private Map<Class<?>, Converter<?>> converters = new HashMap<Class<?>, Converter<?>>();

	/**
	 * 
	 * @param firstProperty
	 * @param propertyNames
	 * @return this
	 */
	public BeanTransformer skip(String firstProperty, String... propertyNames) {
		skipProperites.add(firstProperty);
		if (propertyNames != null) {
			skipProperites.addAll(Arrays.asList(propertyNames));
		}
		return this;
	}

	/**
	 * 
	 * @param fromProperty
	 * @param toProperty
	 * @return this
	 */
	public BeanTransformer rename(String fromProperty, String toProperty) {
		renameProperties.put(fromProperty, toProperty);
		return this;
	}

	/**
	 * 
	 * @param <F>
	 * @param <T>
	 * @param fromBean
	 * @param toBean
	 * @return
	 * @throws NotUsedPropertiesException
	 */
	@SuppressWarnings("unchecked")
	public <F, T> T transform(F fromBean, T toBean) throws NotUsedPropertiesException {
		try {
			Map<String, Object> properties = (Map<String, Object>) PropertyUtils.describe(fromBean);

			Set<String> foundProperties = filterCorrectProperties(properties);

			if (!foundProperties.isEmpty()) {
				throw new NotUsedPropertiesException(foundProperties);
			}

			registerConverters();
			BeanUtils.populate(toBean, properties);
			deregisterConverters();
			
			return toBean;
		} catch (IllegalAccessException e) {
			throw new NotUsedPropertiesException(e);
		} catch (InvocationTargetException e) {
			throw new NotUsedPropertiesException(e);
		} catch (NoSuchMethodException e) {
			throw new NotUsedPropertiesException(e);
		}
	}

	/**
	 * This method adds converter and wraps it inside {@link ConverterImpl} which handles default case when
	 * incomming object is of the same type as target object
	 * 
	 * @param clz
	 * @param c
	 * @return
	 */
	public <T> BeanTransformer addConverter(Converter<T> c) {
		return addConverter(c, false);
	}
	
	/**
	 * 
	 * @param <T>
	 * @param c
	 * @param skipConverterImpl - set this to true if your converter MUST NOT be wrapped in {@link ConverterImpl}
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> BeanTransformer addConverter(Converter<T> c, boolean skipConverterImpl) {
		Type[] types = c.getClass().getGenericInterfaces();
		Class<T> clz = (Class<T>) ((ParameterizedType) types[0]).getActualTypeArguments()[0];
		if (!skipConverterImpl) {
			c = new ConverterImpl(clz, c);
		}
		converters.put(clz, c);
		return this;
	}	

	/**
	 * 
	 * @param properties
	 * @return
	 */
	private Set<String> filterCorrectProperties(final Map<String, Object> properties) {
		// first remove properties that should be skipped
		properties.keySet().removeAll(skipProperites);

		// than go over the rest of them
		Set<String> foundProperties = Sets.filter(renameProperties.keySet(),
				new Predicate<String>() {
					public boolean apply(String fromProperty) {
						if (properties.containsKey(fromProperty)) {
							Object v = properties.remove(fromProperty);
							String newKey = renameProperties.get(fromProperty);
							properties.put(newKey, v);
							return false;
						}
						return true;
					}
				});

		return foundProperties;
	}

	private void registerConverters() {
		for (Class<?> clz : converters.keySet()) {
			ConvertUtils.register(converters.get(clz), clz);
		}
	}

	private void deregisterConverters() {
		for (Class<?> clz : converters.keySet()) {
			ConvertUtils.deregister(clz);
		}
	}
}

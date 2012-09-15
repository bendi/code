package pl.bedkowski.code.liferay.service.processor.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public class ProcessorModel {

	public enum Suffix{Clp, Util};

	private static final String KEY_CLASS_NAME = "className",
			KEY_PACKAGE_NAME = "packageName", KEY_METHODS = "methods",
			KEY_CLASS_LOADER = "classLoader", KEY_INIT_METHOD = "initMethod";

	private final Set<String> initialKeys;

	@SuppressWarnings("serial")
	private final Map<String, Object> modelMap = new HashMap<String, Object>() {
		@Override
		public Object put(String key, Object value) {
			if (initialKeys != null && initialKeys.contains(key)) {
				throw new IllegalArgumentException(key);
			}
			return super.put(key, value);
		}
	};

	/**
	 *
	 * @param iface
	 * @param classLoader
	 * @param initMethod
	 * @param methods
	 */
	public ProcessorModel(TypeElement iface, String classLoader,
			String initMethod, Map<String, Element> methods) {
		modelMap.put(KEY_CLASS_NAME, iface.getSimpleName());
		modelMap.put(KEY_PACKAGE_NAME, pkg(iface));
		modelMap.put(KEY_METHODS, methods);
		modelMap.put(KEY_CLASS_LOADER, classLoader);
		modelMap.put(KEY_INIT_METHOD, initMethod);

		initialKeys = Collections.unmodifiableSet(modelMap.keySet());
	}

	public final Name getClassName() {
		return get(KEY_CLASS_NAME);
	}

	public final Name getPackageName() {
		return get(KEY_PACKAGE_NAME);
	}

	public final String getClassLoader() {
		return get(KEY_CLASS_LOADER);
	}

	public final String getInitMethod() {
		return get(KEY_INIT_METHOD);
	}

	public final Map<String, Element> getMethods() {
		return get(KEY_METHODS);
	}

	public final Map<String, Object> getModelMap() {
		return Collections.unmodifiableMap(modelMap);
	}

	public String getQualifiedName() {
		return getPackageName() + "." + getClassName();
	}

	public Set<Suffix> getSuffixes() {
		return EnumSet.allOf(Suffix.class);
	}

	/**
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public Object put(String key, Object value) {
		return modelMap.put(key, value);
	}

	@SuppressWarnings("unchecked")
	private <T> T get(String key) {
		return (T) modelMap.get(key);
	}

	/**
	 *
	 * @param typeElement
	 * @return
	 */
	private static final Name pkg(TypeElement typeElement) {
		PackageElement packageElement = (PackageElement) typeElement.getEnclosingElement();
		return packageElement.getQualifiedName();
	}
}
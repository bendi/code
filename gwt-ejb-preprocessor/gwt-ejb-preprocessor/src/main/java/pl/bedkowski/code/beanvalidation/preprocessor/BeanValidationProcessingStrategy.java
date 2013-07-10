package pl.bedkowski.code.beanvalidation.preprocessor;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Element;

/**
 *
 * @author marek.bedkowski
 *
 * @param <T>
 */
public abstract class BeanValidationProcessingStrategy<T extends Annotation> {
	private String name;
	private String paramsString;

	private Class<T> annotation;
	private String paramTemplate;

	/**
	 *
	 * @param annotation
	 * @param paramTemplate
	 */
	protected BeanValidationProcessingStrategy(Class<T> annotation,
			String paramTemplate) {
		this.annotation = annotation;
		this.paramTemplate = paramTemplate;
		this.name = dc(annotation.getSimpleName());
	}

	/**
	 *
	 * @param field
	 * @return
	 */
	public final boolean read(Element field) {
		T value = field.getAnnotation(annotation);
		if (value == null) {
			return false;
		}
		List<Object> params = new LinkedList<Object>();
		doRead(value, params);
		paramsString = MessageFormat.format(paramTemplate,
				params.toArray(new Object[0]));
		return true;
	}

	/**
	 *
	 * @return
	 */
	public String getMethodCall() {
		return name + "(" + paramsString + ")";
	}

	/**
	 *
	 * @param value
	 * @param params
	 */
	protected abstract void doRead(T value, List<Object> params);

	/**
	 *
	 * @param s
	 * @return
	 */
	private static String dc(String s) {
		String[] parts = s.split("\\.");
		s = parts[parts.length - 1];
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}
}
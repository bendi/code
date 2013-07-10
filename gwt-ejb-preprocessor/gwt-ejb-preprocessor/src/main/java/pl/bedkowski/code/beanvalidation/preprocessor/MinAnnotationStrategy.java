package pl.bedkowski.code.beanvalidation.preprocessor;

import java.util.List;

import javax.validation.constraints.Min;

/**
 * {@link Min}
 *
 * @author marek.bedkowski
 *
 */
public class MinAnnotationStrategy extends BeanValidationProcessingStrategy<Min> {
	protected MinAnnotationStrategy() {
		super(Min.class, "{0}");
	}

	@Override
	protected void doRead(Min value, List<Object> params) {
		params.add(value.value());
	}
}
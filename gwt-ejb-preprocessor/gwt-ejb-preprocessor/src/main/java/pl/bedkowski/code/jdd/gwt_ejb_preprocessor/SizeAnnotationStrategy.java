package pl.bedkowski.code.jdd.gwt_ejb_preprocessor;

import java.util.List;

import javax.validation.constraints.Size;

/**
 * {@link Size}
 *
 * @author marek.bedkowski
 *
 */
public class SizeAnnotationStrategy extends BeanValidationProcessingStrategy<Size> {

	protected SizeAnnotationStrategy() {
		super(Size.class, "{0}, {1}");
	}

	@Override
	protected void doRead(Size value, List<Object> params) {
		params.add(value.min());
		params.add(value.max());
	}
}
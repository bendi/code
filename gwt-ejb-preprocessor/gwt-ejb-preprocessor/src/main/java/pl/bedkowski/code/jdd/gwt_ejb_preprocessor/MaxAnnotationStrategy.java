package pl.bedkowski.code.jdd.gwt_ejb_preprocessor;

import java.util.List;

import javax.validation.constraints.Max;

/**
 * {@link Max}
 *
 * @author marek.bedkowski
 *
 */
public class MaxAnnotationStrategy extends BeanValidationProcessingStrategy<Max> {
	protected MaxAnnotationStrategy() {
		super(Max.class, "{0}");
	}

	@Override
	protected void doRead(Max value, List<Object> params) {
		params.add(value.value());
	}
}

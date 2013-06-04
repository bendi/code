package pl.bedkowski.code.jdd.gwt_ejb_preprocessor;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * {@link NotNull}
 *
 * @author marek.bedkowski
 *
 */
public class NotNullAnnotationStrategy extends BeanValidationProcessingStrategy<NotNull> {
	protected NotNullAnnotationStrategy() {
		super(NotNull.class, "");
	}

	@Override
	protected void doRead(NotNull value, List<Object> params) {}
}


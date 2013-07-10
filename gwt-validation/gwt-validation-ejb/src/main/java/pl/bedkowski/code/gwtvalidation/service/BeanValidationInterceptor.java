package pl.bedkowski.code.gwtvalidation.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Set;

import javax.annotation.Resource;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

public class BeanValidationInterceptor {

	@Resource
	private Validator validator;

	@AroundInvoke
	public Object intercept(InvocationContext ctx) throws Exception {

		Object[] params = ctx.getParameters();
		Method method = ctx.getMethod();
		Class<?>[] paramTypes = method.getParameterTypes();

		Annotation[][] annotationParameters = method.getParameterAnnotations();

		for (int i = 0; i < annotationParameters.length; i++) {
			Annotation[] annotations = annotationParameters[i];
			Object param = params[i];
			for(Annotation annotation : annotations) {
				Class<? extends Annotation> annotationClass = annotation.annotationType();
				if (annotationClass.equals(NotNull.class)) {
					if (param == null) {
						throw new ValidationException("Parameter of type: " + paramTypes[i].getSimpleName() + " cannot be null.");
					}
				} else if(annotationClass.equals(Valid.class)) {
					validateBean(param);
				}
			}
		}

		return ctx.proceed();

	}

	private void validateBean(Object bean) {
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(bean);

		if (!constraintViolations.isEmpty()) {
//			if (LOG.isDebugEnabled()) {
//				StringBuilder sb = new StringBuilder();
//				for(ConstraintViolation<Object> cv : constraintViolations) {
//					sb.append(cv.getMessage()).append("\n");
//				}
//				LOG.debug("Constraint violations: " + sb);
//			}
			String msg = MessageFormat.format("Bean validation for bean: {0} failed, number of errors: {1}",
				bean.getClass().getCanonicalName(),
				Integer.valueOf(constraintViolations.size())
			);
			throw new ValidationException(msg);
		}
	}
}

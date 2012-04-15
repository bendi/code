package pl.bedkowski.code.tapestry.services;

import static org.apache.commons.lang.StringUtils.capitalize;

import java.util.List;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.InstructionBuilder;
import org.apache.tapestry5.plastic.InstructionBuilderCallback;
import org.apache.tapestry5.plastic.LocalVariable;
import org.apache.tapestry5.plastic.LocalVariableCallback;
import org.apache.tapestry5.plastic.MethodDescription;
import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticField;
import org.apache.tapestry5.plastic.PlasticMethod;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.TransformationSupport;
import org.slf4j.Logger;


public class SelectModelPropertyWorker implements ComponentClassTransformWorker2 {

	private static final String SELECT_MODEL_FACTORY_FIELD_NAME = "____selectModelFactory";

	@Inject
	private Logger log;

	@Inject
	private SelectModelFactory selectModelFactory;

	@Override
	public void transform(PlasticClass cls, TransformationSupport support, MutableComponentModel model) {
		List<PlasticField> fields = getSelectModelFields(cls);
		PlasticField selectModelFactoryField = cls.introduceField(selectModelFactory.getClass(), SELECT_MODEL_FACTORY_FIELD_NAME);
		selectModelFactoryField.inject(selectModelFactory);

		for(PlasticField field : fields) {
			log.debug("Found field: " + field.getName());
			final String labelProperty = getLabelProperty(field);
			buildGetSelectModelMethod(cls, field, selectModelFactoryField, labelProperty);
		}
	}

	private static void buildGetSelectModelMethod(PlasticClass cls, final PlasticField field, final PlasticField selectModelFactoryField, final String labelProperty) {
		PlasticMethod method = cls.introduceMethod(new MethodDescription(name(SelectModel.class), "get" + capitalize(field.getName())));
		method.changeImplementation(new InstructionBuilderCallback() {
			public void doBuild(InstructionBuilder builder) {
				builder.loadThis().getField(selectModelFactoryField).loadThis().getField(field);

				builder.startVariable(name(String.class), new LocalVariableCallback() {
					public void doBuild(LocalVariable variable, InstructionBuilder builder) {
						builder.loadConstant(labelProperty);
					}
				});
				builder.invoke(SelectModelFactory.class, SelectModel.class, "create", List.class, String.class).checkcast(SelectModel.class);
				builder.returnResult();
			}
		});
	}

	private static String name(Class<?> clz) {
		return clz.getName();
	}

	private static String getLabelProperty(PlasticField field) {
		pl.bedkowski.code.tapestry.annotation.SelectModel s = field.getAnnotation(pl.bedkowski.code.tapestry.annotation.SelectModel.class);
		return s.labelProperty();
	}

	private static List<PlasticField> getSelectModelFields(PlasticClass cls) {
		return cls.getFieldsWithAnnotation(pl.bedkowski.code.tapestry.annotation.SelectModel.class);
	}
}

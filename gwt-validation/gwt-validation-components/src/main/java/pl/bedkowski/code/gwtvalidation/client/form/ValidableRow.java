package pl.bedkowski.code.gwtvalidation.client.form;

import java.util.List;

import pl.bedkowski.code.beanvalidation.iface.Constraint;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class ValidableRow<T> extends Composite implements HasValue<T> {

	private List<Constraint> validator;
	protected Label label;

	public void addLabel(Label label) {
		this.label = label;
	}

	public Label getLabel() {
		return label;
	}

	public void setValidator(List<Constraint> validator) {
		this.validator = validator;
	}

	public List<Constraint> getValidator() {
		return validator;
	}

	public List<Constraint> getConstraintsList() {
		return getValidator();
	}

	public abstract Widget getWidget();

}

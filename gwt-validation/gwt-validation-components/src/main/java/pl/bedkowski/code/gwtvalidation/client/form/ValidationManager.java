package pl.bedkowski.code.gwtvalidation.client.form;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import pl.bedkowski.code.beanvalidation.iface.Constraint;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;

public class ValidationManager {

	private List<Validator> validatorsList = new LinkedList<Validator>();
	public interface Validator {
		public boolean isValid();
	}

	public static class MyValidator implements Validator {
		private List<Constraint> constraints;
		private Label label;
		private HasValue<?> panel;

		public MyValidator(HasValue<?> panel, Label label, List<Constraint> constraints) {
			this.panel = panel;
			this.constraints = constraints;
			this.label = label;
		}

		public boolean isValid() {
			for(Constraint c : constraints) {
				if (!c.isValid(panel.getValue())) {
					label.setText(c.getErrorMessage());
					return false;
				}
			}

			return true;
		}
	}

	public Validator addValidator(HasValue<?> panel, Label label, List<Constraint> constraints) {
		Validator v = new MyValidator(panel, label, constraints);
		validatorsList.add(v);
		return v;
	}

	public Collection<Validator> getValidators() {
		return Collections.unmodifiableCollection(validatorsList);
	}

}

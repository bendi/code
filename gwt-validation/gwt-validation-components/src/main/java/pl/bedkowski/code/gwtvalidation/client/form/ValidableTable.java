package pl.bedkowski.code.gwtvalidation.client.form;

import java.util.Iterator;

import pl.bedkowski.code.gwtvalidation.client.DTO;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ValidableTable<T extends DTO> extends Composite implements HasWidgets {

	private FlexTable table = new FlexTable();

	private T dto;

	private ValidationManager validationManager;


	public ValidableTable(T dto, ValidationManager validationManager) {
		this.dto = dto;
		this.validationManager = validationManager;
		initWidget(table);
	}

	@Override
	public void add(Widget w) {
		if (w instanceof ValidableRow) {
			ValidableRow<?> row = ((ValidableRow<?>)w);
			Label errorLabel = new Label("");
			validationManager.addValidator(row, errorLabel, row.getConstraintsList());
			int currRow = table.getRowCount();
			table.setWidget(currRow, 0, row.getLabel());
			table.setWidget(currRow, 1, row.getWidget());
			table.setWidget(currRow+1, 1, errorLabel);
		}
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Widget> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Widget w) {
		throw new UnsupportedOperationException();
	}

	public T getDto() {
		return dto;
	}

	public void setDto(T dto) {
		this.dto = dto;
	}

}

package pl.bedkowski.code.gwtvalidation.client.form;

import java.util.Iterator;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TextRow extends ValidableRow<String> implements HasWidgets {

	private TextBox textBox;

	public void addTextBox(TextBox textBox) {
		this.textBox = textBox;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<String> handler) {
		return textBox.addValueChangeHandler(handler);
	}

	@Override
	public String getValue() {
		return textBox.getValue();
	}

	@Override
	public void setValue(String value) {
		setValue(value, false);
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		if (value == null || "null".equalsIgnoreCase(value)) {
			return;
		}
		textBox.setValue(value, fireEvents);
	}

	@Override
	public void add(Widget w) {
		if (label == null) {
			label = (Label)w;
		} else {
			textBox = (TextBox)w;
		}
	}

	@Override
	public void clear() {
		throw new RuntimeException();
	}

	@Override
	public Iterator<Widget> iterator() {
		throw new RuntimeException();
	}

	@Override
	public boolean remove(Widget w) {
		throw new RuntimeException();
	}

	@Override
	public Widget getWidget() {
		return textBox;
	}

}

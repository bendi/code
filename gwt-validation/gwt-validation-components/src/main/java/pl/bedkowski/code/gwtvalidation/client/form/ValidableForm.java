package pl.bedkowski.code.gwtvalidation.client.form;

import pl.bedkowski.code.gwtvalidation.client.DTO;
import pl.bedkowski.code.gwtvalidation.client.datasource.AsyncCreateUpdate;
import pl.bedkowski.code.gwtvalidation.client.form.ValidationManager.Validator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

abstract class ValidableFormBinderActivator {
	@UiField FlowPanel mainPanel;
	@UiField FlowPanel headerPanel;
	@UiField FlowPanel footerPanel;

	@UiField Button btnCancel;

	@UiHandler("btnCancel")
	abstract void handleCancel(ClickEvent e);

	@UiHandler("btnSave")
	abstract void handleSave(ClickEvent e);

}

public abstract class ValidableForm<T extends DTO> extends DialogBox {

	@UiTemplate("ValidableForm.ui.xml")
	interface ValidableFormBinder extends UiBinder<Widget, ValidableFormBinderActivator> {}

	private static ValidableFormBinder uiBinder = GWT.create(ValidableFormBinder.class);

	@UiField(provided = true)
	ValidableTable<T> table;

	private ValidableFormBinderActivator activator = new ValidableFormBinderActivator() {
		@Override
		void handleCancel(ClickEvent e) {
			cancel();
		}

		@Override
		void handleSave(ClickEvent e) {
			save();
		}
	};

	protected ValidationManager validatorManager = new ValidationManager();

	private boolean performUpdate = true;

	@UiField(provided = true)
	T dto;

	private AsyncCreateUpdate<T> asyncSaveUpdate;

	private AsyncCallback<Void> saveUpdateHandler = new AsyncCallback<Void>() {

		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSuccess(Void result) {
			hide();
		}
	};

	public void show(T dto) {
		if (dto == null) {
			dto = createEmptyDTO();
			performUpdate = false;
		}

		this.dto = dto;

		setWidget(uiBinder.createAndBindUi(activator));

		activator.mainPanel.add(buildPanel(dto));

		setModal(true);
		center();
	}

	protected void doBeforeCreateAndBindUi(T dto) {
	}

	public boolean validate() {
		boolean ret = true;
		for (Validator v : validatorManager.getValidators()) {
			if (!v.isValid()) {
				ret = false;
			}
		}
		return ret;
	}

	public void save() {
		if (!validate()) {
			// FIXME [mb] add some error message
			activator.mainPanel.addStyleName("error");
			return;
		}
		updateDTOFromFields(dto);
		if (performUpdate) {
			asyncSaveUpdate.update(dto, saveUpdateHandler);
		} else {
			asyncSaveUpdate.create(dto, saveUpdateHandler);
		}
	}

	protected void cancel() {
		hide();
	}

	protected abstract T createEmptyDTO();

	protected abstract void updateDTOFromFields(T dto);

	private Widget buildPanel(T dto) {
		// FIXME [mb] add events here
		table = new ValidableTable<T>(dto, validatorManager);
		createAndBindUi();
		return table;
	}

	public T getDto() {
		return dto;
	}

	protected abstract void createAndBindUi();

}

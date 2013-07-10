package pl.bedkowski.code.gwtvalidation.client.form;

import pl.bedkowski.code.gwtvalidation.client.UserDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HasValue;

public class UpdateUserForm extends ValidableForm<UserDTO> {

	@UiTemplate("UpdateUserForm.ui.xml")
	interface UpdateUserFormBinder extends UiBinder<ValidableTable<UserDTO>, UpdateUserForm> {}

	private static UpdateUserFormBinder uiBinder = GWT.create(UpdateUserFormBinder.class);

	@UiField HasValue<String> firstNameBox;
	@UiField HasValue<String> lastNameBox;

	@Override
	protected UserDTO createEmptyDTO() {
		return new UserDTO();
	}

	@Override
	protected void createAndBindUi() {
		uiBinder.createAndBindUi(this);
	}

	@Override
	protected void updateDTOFromFields(UserDTO dto) {
		dto.setFirstName(firstNameBox.getValue());
		dto.setLastName(lastNameBox.getValue());
	}

}

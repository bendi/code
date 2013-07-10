package pl.bedkowski.code.gwtvalidation.client;

import java.util.List;

import pl.bedkowski.code.gwtvalidation.client.form.UpdateUserForm;
import pl.bedkowski.code.gwtvalidation.client.service.UserGwtService;
import pl.bedkowski.code.gwtvalidation.client.service.UserGwtServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class GwtValidationEntryPoint implements EntryPoint {

	UserGwtServiceAsync userService = (UserGwtServiceAsync) GWT.create(UserGwtService.class);

	SimplePanel mainPanel = new SimplePanel();

	@Override
	public void onModuleLoad() {

		Panel left = buildTreePanel(mainPanel);

		RootPanel.get().add(left);

		UpdateUserForm form = new UpdateUserForm();
		form.show(null);
	}

	private Panel buildTreePanel(final SimplePanel mainPanel) {
	    final FlowPanel panel = new FlowPanel();
	    panel.setStyleName("menu-panel");

	    userService.findUsers(new SearchFilter(), new AsyncCallback<List<UserDTO>>() {

			@Override
			public void onSuccess(List<UserDTO> result) {
				for(UserDTO user : result) {
					panel.add(new Label(user.getFirstName()));
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// FIXME [mb] this should be really handled :)
				Window.alert("Sorry we're closed!");
			}
		});

	    return panel;
	}

}

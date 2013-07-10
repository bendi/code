package pl.bedkowski.code.gwtvalidation.client.service;

import java.util.List;

import pl.bedkowski.code.gwtvalidation.client.SearchFilter;
import pl.bedkowski.code.gwtvalidation.client.UserDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserGwtServiceAsync {

	public void findUsers(SearchFilter sf, AsyncCallback<List<UserDTO>> res);

}

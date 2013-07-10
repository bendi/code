package pl.bedkowski.code.gwtvalidation.client.service;

import java.util.List;

import pl.bedkowski.code.gwtvalidation.client.SearchFilter;
import pl.bedkowski.code.gwtvalidation.client.UserDTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath(".." + UserGwtService.URL_PATTERN)
public interface UserGwtService extends RemoteService {

	public static String URL_PATTERN = "/users";

	public List<UserDTO> findUsers(SearchFilter sf);
}

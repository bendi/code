package pl.bedkowski.code.gwtvalidation.server;

import java.util.List;

import javax.ejb.Local;

import pl.bedkowski.code.gwtvalidation.client.SearchFilter;
import pl.bedkowski.code.gwtvalidation.client.UserDTO;

@Local
public interface UserServiceLocal {
	public List<UserDTO> findUsers(SearchFilter filter);

}

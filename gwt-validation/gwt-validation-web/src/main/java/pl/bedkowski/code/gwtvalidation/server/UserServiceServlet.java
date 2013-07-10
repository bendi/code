package pl.bedkowski.code.gwtvalidation.server;

import java.util.List;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;

import pl.bedkowski.code.gwtvalidation.client.SearchFilter;
import pl.bedkowski.code.gwtvalidation.client.UserDTO;
import pl.bedkowski.code.gwtvalidation.client.service.UserGwtService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@WebServlet(name = "UserServiceServlet", urlPatterns = UserGwtService.URL_PATTERN)
public class UserServiceServlet extends RemoteServiceServlet implements UserGwtService {

	private static final long serialVersionUID = 1L;

	@EJB
	private UserServiceLocal userService;

	@Override
	public List<UserDTO> findUsers(SearchFilter sf) {
		return userService.findUsers(sf);
	}

}

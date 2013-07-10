package pl.bedkowski.code.gwtvalidation.service;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import pl.bedkowski.code.gwtvalidation.client.SearchFilter;
import pl.bedkowski.code.gwtvalidation.client.UserDTO;
import pl.bedkowski.code.gwtvalidation.server.UserServiceLocal;

@Stateless
@Interceptors(BeanValidationInterceptor.class)
public class UserService implements UserServiceLocal {

	@Override
	public List<UserDTO> findUsers(@NotNull @Valid SearchFilter filter) {
		return Arrays.asList(
			user("marek"),
			user("krzysiek")
		);
	}

	private static UserDTO user(String fName) {
		UserDTO ret = new UserDTO();
		ret.setFirstName(fName);
		return ret;
	}

}

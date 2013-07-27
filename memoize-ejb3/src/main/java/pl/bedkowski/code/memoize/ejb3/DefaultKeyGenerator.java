package pl.bedkowski.code.memoize.ejb3;

import java.lang.reflect.Method;

import javax.ejb.Stateless;

import pl.bedkowski.code.memoize.dto.UserDTO;
import pl.bedkowski.code.memoize.keygenerator.KeyGenerable;

@Stateless
public class DefaultKeyGenerator implements KeyGeneratorEJB3 {

	@Override
	public String generate(UserDTO dto) {
		return dto.getName();
	}

	@Override
	public String generate(Object target, Method m, Object... params) {
		StringBuilder sb = new StringBuilder(target.getClass().getPackage().toString());

		sb.append(".").append(m.getName()).append("(");

		if (params != null) {
			for (Object param : params) {
				if (param == null) {
					continue;
				}
				String key;
				if (param instanceof KeyGenerable) {
					key = ((KeyGenerable)param).generate(this);
				} else {
					key = param.toString();
				}
				sb.append(key).append(".");
			}
		}

		sb.append(")");


		return sb.toString();
	}

}

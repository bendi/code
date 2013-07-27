package pl.bedkowski.code.memoize.dto;

import pl.bedkowski.code.memoize.keygenerator.KeyGenerable;
import pl.bedkowski.code.memoize.keygenerator.KeyGenerator;

/**
 * <p>
 * Sample DTO used as part of cache key
 * @author marek.bedkowski
 *
 */
public class UserDTO implements KeyGenerable {

	/**
	 *
	 * @return
	 */
	public String getName() {
		return null;
	}

	@Override
	public String generate(KeyGenerator keyGenerator) {
		return keyGenerator.generate(this);
	}

}

package pl.bedkowski.code.memoize.keygenerator;

import pl.bedkowski.code.memoize.dto.UserDTO;

/**
 * <p>
 * Interface implemented by all key generators.
 * </p>
 *
 */
public interface KeyGenerator {

	/**
	 * <p>
	 * Method for generating key based on {@link UserDTO} object
	 * </p>
	 * @param dto
	 * @return
	 */
	public String generate(UserDTO dto);

}

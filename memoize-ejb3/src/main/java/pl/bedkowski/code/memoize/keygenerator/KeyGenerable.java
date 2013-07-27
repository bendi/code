package pl.bedkowski.code.memoize.keygenerator;

/**
 * <p>
 * Interface implemented by object that should be used as cache key.
 * </p>
 *
 */
public interface KeyGenerable {

	/**
	 *
	 * @param keyGenerator
	 * @return
	 */
	public String generate(KeyGenerator keyGenerator);

}

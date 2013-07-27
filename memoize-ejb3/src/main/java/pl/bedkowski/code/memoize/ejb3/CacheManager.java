package pl.bedkowski.code.memoize.ejb3;

import javax.ejb.Local;

/**
 * <p>
 * CacheManager iface - it only key and value pairs. Key can be any comparable.
 * </p>
 *
 */
@Local
public interface CacheManager<T extends Comparable<T>> {

	/**
	 * <p>
	 * Retrieve object from cache - returns null in case object wasn't cached yet.
	 * </p>
	 *
	 * @param key
	 * @return
	 */
	public Object get(T key);

	/**
	 * <p>
	 * Put object into cache using given key
	 * </p>
	 *
	 * @param key
	 * @param object
	 */
	public void put(T key, Object object);
}

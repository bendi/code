package pl.bedkowski.code.memoize.ejb3;

import javax.ejb.Stateless;

/**
 * FIXME: still lacks ehacache specifics
 * @author marek.bedkowski
 *
 */
@Stateless
public class EHCacheManager implements CacheManager<String> {

	public Object get(String key) {
		return null;
	}

	public void put(String key, Object object) {
	}

}

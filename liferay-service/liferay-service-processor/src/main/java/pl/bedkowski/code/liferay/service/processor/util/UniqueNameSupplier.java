package pl.bedkowski.code.liferay.service.processor.util;

import javax.lang.model.element.Name;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class UniqueNameSupplier {

	private Multiset<String> occurances = HashMultiset.create();

	/**
	 *
	 * @param name
	 * @return
	 */
	public String supplyUniqueName(Name name) {
		return checkName(name.toString(), occurances);
	}

	/**
	 *
	 * @param name
	 * @param occurances
	 * @return
	 */
	private static String checkName(String name, Multiset<String> occurances) {
		occurances.add(name);
		int count = occurances.count(name);
		if (count > 1) {
			name = checkName(name + (count - 1), occurances);
		}
		return name;
	}
}
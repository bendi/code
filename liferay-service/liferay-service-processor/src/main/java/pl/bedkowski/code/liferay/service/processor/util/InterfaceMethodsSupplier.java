package pl.bedkowski.code.liferay.service.processor.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;

public class InterfaceMethodsSupplier {

	private UniqueNameSupplier uns;

	public InterfaceMethodsSupplier(UniqueNameSupplier uns) {
		this.uns = uns;
	}

	/**
	 *
	 * @param allMembers
	 * @return
	 */
	public Map<String, Element> supplyMethods(List<? extends Element> allMembers) {
		Map<String, Element> methods = new HashMap<String, Element>();

		for (Element member : allMembers) {
			if (isInterfaceMethod(member)) {
				String methodName = uns.supplyUniqueName(member);
				methods.put(methodName, member);
			}
		}

		return methods;
	}


	/**
	 *
	 * @param element
	 * @return
	 */
	protected boolean isInterfaceMethod(Element element) {
		return element.getKind() == ElementKind.METHOD &&
				!element.getModifiers().contains(Modifier.NATIVE) &&
				element.getModifiers().contains(Modifier.ABSTRACT);
	}

}

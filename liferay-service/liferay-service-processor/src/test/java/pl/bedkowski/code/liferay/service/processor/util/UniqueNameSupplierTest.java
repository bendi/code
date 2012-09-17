package pl.bedkowski.code.liferay.service.processor.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static junit.framework.Assert.*;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class UniqueNameSupplierTest {

	UniqueNameSupplier uns;

	@Before
	public void init() {
		uns = new UniqueNameSupplier();
	}

	@Test
	public void testSupplyUniqueName() {

		List<Element> elems = Lists.newArrayList(
			elem("test"),
			elem("test"),
			elem("test1")
		);

		String[] expectedNames = {
			"test",
			"test1",
			"test11"
		};

		int i=0;
		for(Element elem : elems) {
			String name = uns.supplyUniqueName(elem);
			assertEquals(expectedNames[i++], name);
		}

	}

	private static Element elem(String n) {
		Element elem = mock(Element.class);
		{
			Name name = name(n);
			when(elem.getSimpleName()).thenReturn(name);
		}
		return elem;
	}

	private static Name name(String n) {
		Name name = mock(Name.class);
		when(name.toString()).thenReturn(n);
		return name;
	}
}

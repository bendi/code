package pl.bedkowski.code.conditionalgetter;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestConditionalPropertyInterceptor {

	private MyBean myBean, proxy;

	@Before
	public void setUp() throws Exception {
		myBean = new MyBean();
		proxy = ConditionalPropertyInterceptor.create(myBean, "Available");
	}

	@Test
	public void testProperPropertyNotAvailable() {
		myBean.setName("my name is");
		assertNull("Property should be null", proxy.getName());
	}

	@Test
	public void testProperPropertyAvailable() {
		myBean.setName("my name is");
		myBean.setNameAvailable(true);
		assertEquals("Bean and proxy should return the same values", myBean.getName(), proxy.getName());
	}

	@Test
	public void testPassNotHandled() {
		myBean.setEmail("bla");
		assertEquals(myBean.getEmail(true), proxy.getEmail(true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIncorrectProperty() {
		myBean.setAddress("my address");
		proxy.getAddress(); // should throw exception
	}

}

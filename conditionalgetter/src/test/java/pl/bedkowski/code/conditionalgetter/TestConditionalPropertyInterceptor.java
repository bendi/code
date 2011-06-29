package pl.bedkowski.code.conditionalgetter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.junit.Before;
import org.junit.Test;

public class TestConditionalPropertyInterceptor {

	DozerBeanMapper mapper;
	private MyBean myBean, proxy;

	@Before
	public void setUp() throws Exception {
		myBean = new MyBean();
		proxy = ConditionalPropertyInterceptor.create(myBean, "Available");
		mapper = new DozerBeanMapper();
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
		assertEquals("Bean and proxy should return the same values",
				myBean.getName(), proxy.getName());
	}

	@Test
	public void testPassNotHandled() {
		myBean.setEmail("bla");
		assertEquals(myBean.getEmail(true), proxy.getEmail(true));
	}

	@Test(expected = NoSuchMethodException.class)
	public void testIncorrectProperty() {
		myBean.setAddress("my address");
		proxy.getAddress(); // should throw exception
	}

	@Test
	public void testPropertyPairs() throws Exception {
		// if this passes without problems it means
		// that all getter have their isAvailable condition
		mapper.addMapping(new BeanMappingBuilder() {
			@Override
			protected void configure() {
				mapping(MyBean.class, MyBean.class)
				 .exclude("address")
				;
			}
		});
		// if this test fails you either need to
		// exclude your property as seen above
		// which means it should not be used in mapping
		// or add isAvailable method
		mapper.map(proxy, MyBean.class);
	}

}

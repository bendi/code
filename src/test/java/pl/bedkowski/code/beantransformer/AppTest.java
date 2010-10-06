package pl.bedkowski.code.beantransformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import pl.bedkowski.code.beantransformer.BeanTransformer.Converter;
import pl.bedkowski.code.beantransformer.BeanTransformer.NotUsedPropertiesException;

/**
 * Unit test for simple App.
 */
public class AppTest {

	public static class SuperClass {
		private String prop;

		public String getProp() {
			return prop;
		}

		public void setProp(String prop) {
			this.prop = prop;
		}
	}

	public static class A extends SuperClass {
		public static class B {
			public String bla = "dupa";
		}

		private Long bla;

		public void setBla(Long bla) {
			this.bla = bla;
		}

		public Long getBla() {
			return bla;
		}

		private String fromProperty;

		public String getFromProperty() {
			return fromProperty;
		}

		public void setFromProperty(String fromProperty) {
			this.fromProperty = fromProperty;
		}

		private A.B ab;

		public A.B getAb() {
			return ab;
		}

		public void setAb(A.B ab) {
			this.ab = ab;
		}
	}

	public static class B extends SuperClass {
		public static class A {
			public String bla;
		}

		private String bla;

		public void setBla(String bla) {
			this.bla = bla;
		}

		public String getBla() {
			return bla;
		}

		private String toProperty;

		public void setToProperty(String toProperty) {
			this.toProperty = toProperty;
		}

		public String getToProperty() {
			return toProperty;
		}

		private B.A ba;

		public B.A getBa() {
			return ba;
		}

		public void setBa(B.A ba) {
			this.ba = ba;
		}
	}

	private BeanTransformer trans;

	@Before
	public void init() {
		trans = new BeanTransformer();
	}

	@Test
	public void testApp() throws Exception {
		A a = new A();
		a.setBla(1L);
		a.setProp("prop");
		B b = trans.transform(a, new B());

		assertEquals(a.getBla().toString(), b.getBla());
	}

	@Test
	public void testAppSkipKeys() throws Exception {
		trans.skip("bla");

		A a = new A();
		a.setBla(1L);
		B b = trans.transform(a, new B());

		assertNull(b.getBla());
		assertEquals(a.getProp(), b.getProp());
	}

	@Test
	public void testRename() throws Exception {
		trans.skip("bla").rename("fromProperty", "toProperty");

		A a = new A();
		a.setBla(1L);
		a.setFromProperty("fromProperty");
		B b = trans.transform(a, new B());

		assertNull(b.getBla());
		assertEquals(a.getProp(), b.getProp());
		assertNotNull(b.getToProperty());
		assertEquals(a.getFromProperty(), b.getToProperty());
	}

	@Test(expected = NotUsedPropertiesException.class)
	public void testRenameWithException() throws Exception {
		trans.skip("bla").rename("fromProperty", "toProperty").rename(
				"oneProperty", "secondProperty");

		A a = new A();
		a.setBla(1L);
		trans.transform(a, null);
	}

	@Test
	public void testWithBeanConversion() throws Exception {
		trans.rename("ab", "ba")
			.addConverter(new Converter<B.A>() {
				public Object convert(Class arg0, Object arg1) {
					B.A ba = new B.A();
					if (arg1 instanceof A.B) {
						ba.bla = ((A.B) arg1).bla;
					}
					return ba;
				}
			});
		A a = new A();
		a.setAb(new A.B());
		B b = trans.transform(a, new B());
		assertNotNull("Ba wasn't found", b.ba);
		assertEquals(a.ab.bla, b.ba.bla);
	}
	
	@Test
	public void testBeanConversionWithTheSameType() throws Exception {
		trans.addConverter(new Converter<B.A>() {
				public Object convert(Class arg0, Object arg1) {
					throw new RuntimeException("This sucks!");
				}
			});
		B a = new B();
		a.setBa(new B.A());
		B b = trans.transform(a, new B());
		assertNotNull("Ba wasn't found", b.ba);
		assertEquals(a.ba, b.ba);
	}	
}

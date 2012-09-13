package pl.bedkowski.code.liferay.service.processor.controller;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.junit.Before;
import org.junit.Test;

import pl.bedkowski.code.liferay.service.annotation.LiferayService;
import pl.bedkowski.code.liferay.service.processor.event.AfterDoModelEvent;
import pl.bedkowski.code.liferay.service.processor.event.AfterDoViewEvent;
import pl.bedkowski.code.liferay.service.processor.exception.ProcessorLifecycleException;
import pl.bedkowski.code.liferay.service.processor.listener.ProcessorLifecycleEventListener;
import pl.bedkowski.code.liferay.service.processor.model.ProcessorModel;

import com.google.common.collect.Sets;

public class LiferayServiceProcessorTest {

	private StringWriter printWriter;
	private ProcessingEnvironment processingEnvironment;
	private Elements elementUtils;

	@Before
	public void init() throws Exception {
		printWriter = new StringWriter();
		processingEnvironment = processingEnv();
	}

	@Test
	public void test() throws Exception {
		List<Element> methods = new ArrayList<Element>();

		methods.add(method(String.class, "test1"));

		methods.add(method(String.class, "test1",
			new Class[]{Integer.class, String.class},
			new Class[]{UnsupportedEncodingException.class, IOException.class}
		));

		methods.add(method(String.class, "test1",
			new Class[]{String.class, String.class}
		));

		methods.add(method(String.class, "test1",
			new Class[]{String.class, String.class, String.class}
		));

		doReturn(methods).when(elementUtils).getAllMembers(any(TypeElement.class));

		LiferayServiceProcessor proc = new LiferayServiceProcessor();

		proc.registerListener(new ProcessorLifecycleEventListener<AfterDoModelEvent>() {
			@Override
			public void on(AfterDoModelEvent event) throws ProcessorLifecycleException {

				validateModel(event.getModel());

				try {
					event.getModel().put("className", "");
					fail("Model base keys should be read-only.");
				} catch(IllegalArgumentException e){
				}
			}
		});

		proc.registerListener(new ProcessorLifecycleEventListener<AfterDoViewEvent>() {
			@Override
			public void on(AfterDoViewEvent event) throws ProcessorLifecycleException {

				assertEquals("MyTestIface", event.getModel().getModelMap().get("className").toString());

				try {
					event.getModel().getModelMap().put("this should never happen", "");
					fail("Methods map should be read-only.");
				} catch(UnsupportedOperationException e){
				}
			}
		});

		proc.init(processingEnvironment);

		proc.process(null, roundEnv());

		Messager m = processingEnvironment.getMessager();
		verify(m).printMessage(eq(Kind.NOTE), eq("LiferayService for: pl.bedkowski.test.MyTestIface"), any(TypeElement.class));
		verify(m).printMessage(eq(Kind.ERROR), eq("LiferayService can only be applied to interface, found: METHOD"), any(TypeElement.class));

//		System.out.println(printWriter.getBuffer());
    }

	@Test
	public void invalidViewClassName() throws Exception {
		processingEnvironment.getOptions().put(LiferayServiceProcessor.OPTION_VIEW_CLASS, "invalid class name");
		try {
			Processor proc = new LiferayServiceProcessor();
			proc.init(processingEnvironment);
			fail("Should throw class not found.");
		} catch(RuntimeException e) {
			assertEquals("Should throw class not found.", ClassNotFoundException.class, e.getCause().getClass());
		}
	}

	private static void validateModel(ProcessorModel model) {
		assertEquals("MyTestIface", model.getClassName().toString());
		assertEquals("portalClassLoader", model.getClassLoader());
		assertEquals("myInitMethod", model.getInitMethod());

		Map<String, Element> methods = model.getMethods();

		try {
			methods.put("this should never happen", mock(Element.class));
			fail("Methods map should be read-only.");
		} catch(UnsupportedOperationException e){
		}

		assertEquals(4, methods.size());

		assertTrue(methods.containsKey("test1"));
		assertTrue(methods.containsKey("test11"));
		assertTrue(methods.containsKey("test12"));
		assertTrue(methods.containsKey("test13"));
	}

	private ProcessingEnvironment processingEnv() throws IOException {
		ProcessingEnvironment pe = mock(ProcessingEnvironment.class);

		elementUtils = mock(Elements.class);
		when(pe.getElementUtils()).thenReturn(elementUtils);

		Filer filer = mock(Filer.class);
		when(pe.getFiler()).thenReturn(filer);

		JavaFileObject jfo = mock(JavaFileObject.class);
		when(filer.createSourceFile(anyString())).thenReturn(jfo);

		when(jfo.openWriter()).thenReturn(printWriter);

		Messager m = mock(Messager.class);
		when(pe.getMessager()).thenReturn(m);

		Map<String, String> options = new HashMap<String, String>();
		when(pe.getOptions()).thenReturn(options);

		return pe;
	}

	private RoundEnvironment roundEnv() {
		RoundEnvironment re = mock(RoundEnvironment.class);

		Set<Element> elems = Sets.newHashSet();
		doReturn(elems).when(re).getElementsAnnotatedWith(LiferayService.class);
		elems.add(iface("MyTestIface", "pl.bedkowski.test", "myInitMethod", "portalClassLoader"));
		{
			TypeElement te = mock(TypeElement.class);
			when(te.getKind()).thenReturn(ElementKind.METHOD);
			elems.add(te);
		}

		return re;
	}

	private static Element iface(String sName, String sPkg, String initMethod, String loader) {
		TypeElement te = mock(TypeElement.class);
		when(te.getKind()).thenReturn(ElementKind.INTERFACE);
		{
			LiferayService ls = mock(LiferayService.class);
			when(ls.value()).thenReturn(loader);
			when(ls.initMethod()).thenReturn(initMethod);
			when(te.getAnnotation(LiferayService.class)).thenReturn(ls);
		}
		{
			Name name = name(sName);
			when(te.getSimpleName()).thenReturn(name);
		}
		{
			PackageElement pkg = mock(PackageElement.class);
			Name name = name(sPkg);
			when(pkg.getQualifiedName()).thenReturn(name);
			when(te.getEnclosingElement()).thenReturn(pkg);
		}
		{
			Name name = name(sPkg+"."+sName);
			when(te.getQualifiedName()).thenReturn(name);
		}
		return te;
	}

	private ExecutableElement method(Class<?> ret, String sName) {
		return method(ret, sName, null);
	}

	@SuppressWarnings("rawtypes")
	private ExecutableElement method(Class<?> ret, String sName, Class[] cParams) {
		return method(ret, sName, cParams, null);
	}

	@SuppressWarnings("rawtypes")
	private ExecutableElement method(Class<?> ret, String sName, Class[] cParams, Class[] cExceptions) {
		ExecutableElement execElem = mock(ExecutableElement.class);

		TypeMirror type = type(ret);
		when(execElem.getReturnType()).thenReturn(type);
		when(execElem.getKind()).thenReturn(ElementKind.METHOD);
		when(execElem.getModifiers()).thenReturn(EnumSet.of(Modifier.ABSTRACT));

		{
			Name name = name(sName);
			when(execElem.getSimpleName()).thenReturn(name);
		}
		if (cParams != null) {
			List<VariableElement> params = new ArrayList<VariableElement>();
			for(Class<?> clz : cParams) {
				params.add(param(clz));
			}

			doReturn(params).when(execElem).getParameters();
		}
		if (cExceptions != null) {
			List<TypeMirror> exceptions = new ArrayList<TypeMirror>();
			for(Class<?> clz : cExceptions) {
				exceptions.add(type(clz));
			}

			doReturn(exceptions).when(execElem).getThrownTypes();
		}
		return execElem;
	}

	private static TypeMirror type(Class<?> clz) {
		TypeMirror type = mock(TypeMirror.class);
		when(type.toString()).thenReturn(clz.getCanonicalName());
		return type;
	}

	private static VariableElement param(Class<?> clz) {
		VariableElement elem = mock(VariableElement.class);
		TypeMirror type = type(clz);
		when(elem.asType()).thenReturn(type);
		when(elem.toString()).thenReturn(clz.getCanonicalName());
		return elem;
	}

	private static Name name(String n) {
		Name name = mock(Name.class);
		when(name.toString()).thenReturn(n);
		return name;
	}

}

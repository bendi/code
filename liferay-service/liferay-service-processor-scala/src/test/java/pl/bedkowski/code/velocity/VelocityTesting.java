package pl.bedkowski.code.velocity;


import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;

import pl.bedkowski.code.liferay.service.processor2.LiferayserviceProcessor;

public class VelocityTesting {
	
	@Test
	public void test2() throws Exception {
		LiferayserviceProcessor lsp = new LiferayserviceProcessor();
	}
	
	@Test
	public void test() throws Exception {
		Properties props = new Properties();
        URL url = getClass().getClassLoader().getResource("velocity.properties");
        props.load(url.openStream());

        VelocityEngine ve = new VelocityEngine(props);
        ve.init();

        VelocityContext vc = new VelocityContext();
        
        vc.put("className", "TestClass");
        vc.put("packageName", "pl.bedkowski.code.test");
        vc.put("methods", methods());
        vc.put("classLoader", "portalClassLoader");
        vc.put("initMethod", "init");
        
        writeClass("tpl/LiferayClp.java.vm", ve, vc, new OutputStreamWriter(System.out));
        writeClass("tpl/LiferayUtil.java.vm", ve, vc, new OutputStreamWriter(System.out));
    }
	
	private static void writeClass(String tplName, VelocityEngine ve, VelocityContext vc, Writer out) throws IOException {
        Template vt = ve.getTemplate(tplName);
        vt.merge(vc, out);
        out.flush();
	}
	
	private Map<String, ExecutableElement> methods() {
		Map<String, ExecutableElement> ret = new HashMap<String, ExecutableElement>();
		
		{
			ExecutableElement execElem = mock(ExecutableElement.class);
			
			TypeMirror type = type(String.class);
			when(execElem.getReturnType()).thenReturn(type);
			
			{
				Name name = mock(Name.class);
				when(name.toString()).thenReturn("test1");
				when(execElem.getSimpleName()).thenReturn(name);
			}
			{
				List<VariableElement> params = new ArrayList<VariableElement>();
				params.add(param(Integer.class));
				params.add(param(String.class));

				doReturn(params).when(execElem).getParameters();
			}
			{
				List<TypeMirror> exceptions = new ArrayList<TypeMirror>();
				
				exceptions.add(type(UnsupportedEncodingException.class));
				exceptions.add(type(IOException.class));
				
				doReturn(exceptions).when(execElem).getThrownTypes();
			}
			ret.put("test1", execElem);
		}
		
		return ret;
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

}

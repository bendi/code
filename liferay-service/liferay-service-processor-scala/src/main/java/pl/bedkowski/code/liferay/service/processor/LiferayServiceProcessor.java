package pl.bedkowski.code.liferay.service.processor;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import pl.bedkowski.code.liferay.service.annotation.LiferayService;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;


@SupportedAnnotationTypes("pl.bedkowski.code.liferay.service.annotation.LiferayService")
@SupportedSourceVersion(SourceVersion.RELEASE_5)
public class LiferayServiceProcessor extends AbstractProcessor {

	private VelocityEngine ve;
	
	public LiferayServiceProcessor() throws IOException {
		Properties props = new Properties();
        URL url = getClass().getClassLoader().getResource("velocity.properties");
        props.load(url.openStream());
        
        ve = new VelocityEngine(props);
        ve.init();
	}
	
	public LiferayServiceProcessor(Properties props) {
        ve = new VelocityEngine(props);
        ve.init();		
	}
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element e : roundEnv.getElementsAnnotatedWith(LiferayService.class)) {
        	switch(e.getKind()) {
	        	case INTERFACE:
	        		LiferayService ls = e.getAnnotation(LiferayService.class);
	        		processIface((TypeElement) e, ls.initMethod(), ls.value());
	                break;
	        	default:
	        		// issue an error
	                processingEnv.getMessager().printMessage(
	                    Diagnostic.Kind.ERROR,
	                    "LiferayClp can only be applied to interface, found: " + ((TypeElement) e).getQualifiedName(), e
                    );
	        		break;
            }
		}
		return true;
	}
	
	/**
	 * 
	 * @param classElement
	 */
	private void processIface(TypeElement classElement, String initMethod, String classLoader) {
        Map<String, Element> methods = getMethods(classElement);

        VelocityContext vc = new VelocityContext();
        
		vc.put("className", classElement.getSimpleName().toString());
        vc.put("packageName", pkg(classElement));
        vc.put("methods", methods);
        vc.put("classLoader", classLoader);
        vc.put("initMethod", initMethod);
        
		try {
			String fqClassName = classElement.getQualifiedName().toString();

			writeClass(vc, fqClassName + "Clp", "tpl/LiferayClp.java.vm");
	        writeClass(vc, fqClassName + "Util", "tpl/LiferayUtil.java.vm");
			
			processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "annotated class: " + classElement.getQualifiedName(), classElement
            );
		} catch (IOException e1) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e1.getMessage());
		}
	}

	/**
	 * 
	 * @param vc
	 * @param ve
	 * @param targetClassName
	 * @param classTpl
	 * @throws IOException
	 */
	private void writeClass(VelocityContext vc, String targetClassName, String classTpl) throws IOException {
        Template vt = ve.getTemplate(classTpl);
        
        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(targetClassName);
        
        Writer writer = jfo.openWriter();
        
        vt.merge(vc, writer);
        writer.close();
	}
	
	/**
	 * 
	 * @param classElement
	 * @return
	 */
	private Map<String, Element> getMethods(TypeElement classElement) {
		Map<String, Element> methods = new HashMap<String, Element>();
		
		List<? extends Element> members = processingEnv.getElementUtils().getAllMembers(classElement);
		Multiset<String> occurances = HashMultiset.create();
		
		for(Element member : members) {
			switch(member.getKind()) {
				case METHOD:
					if (isNative(member) || !isAbstract(member)) {
						continue;
					}
					String methodName = member.getSimpleName().toString();
					occurances.add(methodName);
					int count = occurances.count(methodName);
					if (count > 1) {
						methodName += count-1;
					}
					methods.put(methodName, member);
					break;
			}
		}
		
		return methods;
	}
	
	/**
	 * 
	 * @param exeElement
	 * @return
	 */
	private static final boolean isNative(Element exeElement) {
		return exeElement.getModifiers().contains(Modifier.NATIVE);
	}
	
	/**
	 * 
	 * @param exeElement
	 * @return
	 */
	private static final boolean isAbstract(Element exeElement) {
		return exeElement.getModifiers().contains(Modifier.ABSTRACT);
	}
	
	/**
	 * 
	 * @param classElement
	 * @return
	 */
	private static final String pkg(TypeElement classElement) {
        PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
        return packageElement.getQualifiedName().toString();
	}
}

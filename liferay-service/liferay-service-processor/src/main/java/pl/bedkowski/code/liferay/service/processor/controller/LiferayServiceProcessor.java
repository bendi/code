package pl.bedkowski.code.liferay.service.processor.controller;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.apache.commons.lang.StringUtils;

import pl.bedkowski.code.liferay.service.annotation.LiferayService;
import pl.bedkowski.code.liferay.service.processor.dispatcher.DefaultProcessorLifecycleEventDispatcher;
import pl.bedkowski.code.liferay.service.processor.dispatcher.ProcessorLifecycleEventDispatcher;
import pl.bedkowski.code.liferay.service.processor.event.AfterDoModelEvent;
import pl.bedkowski.code.liferay.service.processor.event.AfterDoViewEvent;
import pl.bedkowski.code.liferay.service.processor.event.ProcessorLifecycleEvent;
import pl.bedkowski.code.liferay.service.processor.exception.InvalidListenerException;
import pl.bedkowski.code.liferay.service.processor.exception.ProcessorException;
import pl.bedkowski.code.liferay.service.processor.exception.ProcessorLifecycleException;
import pl.bedkowski.code.liferay.service.processor.listener.ProcessorLifecycleEventListener;
import pl.bedkowski.code.liferay.service.processor.model.ProcessorModel;
import pl.bedkowski.code.liferay.service.processor.view.ProcessorView;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 *
 * Available options:
 * <ul>
 * <li>{@value #OPTION_VIEW_CLASS} - default {@value #DEFAULT_PROCESSOR_VIEW} </li>
 * </ul>
 *
 * @author "Marek Będkowski" <marek@bedkowski.pl>
 *
 */
@SupportedAnnotationTypes("pl.bedkowski.code.liferay.service.annotation.LiferayService")
@SupportedSourceVersion(SourceVersion.RELEASE_5)
public class LiferayServiceProcessor extends AbstractProcessor implements ProcessorLifecycleEventDispatcher {

	public static final String OPTION_VIEW_CLASS = "view.class";

	private static final String DEFAULT_PROCESSOR_VIEW = "pl.bedkowski.code.liferay.service.processor.view.VelocityProcessorView";

	private ProcessorLifecycleEventDispatcher eventDispatcher = new DefaultProcessorLifecycleEventDispatcher();

	private ProcessorView processorView;

	private WriterFactory writerFactory;

	@Override
	public void init(ProcessingEnvironment processingEnvironment) {
		super.init(processingEnvironment);

		writerFactory = new WriterFactory();

		if (processorView == null) {
			try {
				String viewClassName = processingEnv.getOptions().get(OPTION_VIEW_CLASS);
				if (StringUtils.isEmpty(viewClassName)) {
					viewClassName = DEFAULT_PROCESSOR_VIEW;
				}

				processorView = newProcessor(viewClassName);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}

		processorView.init();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (Element element : roundEnv.getElementsAnnotatedWith(LiferayService.class)) {
			if (element.getKind() == ElementKind.INTERFACE) {
				TypeElement iface = (TypeElement) element;
				ProcessorModel model = doModel(iface);
				doView(iface, model);
			} else {
				// issue an error
				printError("LiferayService can only be applied to interface, found: " + element.getKind(), element);
			}
		}
		return true;
	}

	/**
	 *
	 * @param iface
	 * @return
	 */
	private ProcessorModel doModel(TypeElement iface) {
		LiferayService ls = iface.getAnnotation(LiferayService.class);

		ProcessorModel model = new ProcessorModel(pkg(iface), iface.getSimpleName(), ls.value(), ls.initMethod(), getMethods(iface));

		try {
			dispatch(new AfterDoModelEvent(iface, model));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return model;
	}

	/**
	 *
	 * @param iface
	 * @param model
	 */
	private void doView(TypeElement iface, ProcessorModel model) {

		try {
			processorView.writeClass(model, writerFactory);
		} catch (IOException e) {
			printError(e.getMessage(), iface);
		}

		try {
			dispatch(new AfterDoViewEvent(iface, model));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "LiferayService for: " + iface.getQualifiedName(), iface);
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

		for (Element member : members) {
			if (isInterfaceMethod(member)) {
				String methodName = member.getSimpleName().toString();
				methodName = checkMethodName(methodName, occurances);
				methods.put(methodName, member);
			}
		}

		return Collections.unmodifiableMap(methods);
	}

	private static String checkMethodName(String methodName, Multiset<String> occurances) {
		occurances.add(methodName);
		int count = occurances.count(methodName);
		if (count > 1) {
			methodName = checkMethodName(methodName + (count - 1), occurances);
		}
		return methodName;
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

	/**
	 *
	 * @param typeElement
	 * @return
	 */
	private static final Name pkg(TypeElement typeElement) {
		PackageElement packageElement = (PackageElement) typeElement.getEnclosingElement();
		return packageElement.getQualifiedName();
	}

	/**
	 *
	 * @param msg
	 * @param element
	 */
	private void printError(String msg, Element element) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, element);
	}

	/**
	 *
	 * @param viewClassName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private static ProcessorView newProcessor(String viewClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<? extends ProcessorView> viewClass = Class.forName(viewClassName).asSubclass(ProcessorView.class);
		return viewClass.newInstance();
	}

	public class WriterFactory {

		/**
		 *
		 * @param qualifiedTargetClassName
		 * @return
		 * @throws IOException
		 */
		public Writer openWriter(String qualifiedTargetClassName) throws IOException {
			JavaFileObject jfo = processingEnv.getFiler().createSourceFile(qualifiedTargetClassName);
			return jfo.openWriter();
		}
	}

	@Override
	public boolean registerListener(ProcessorLifecycleEventListener<? extends ProcessorLifecycleEvent> listener) throws InvalidListenerException {
		return eventDispatcher.registerListener(listener);
	}

	@Override
	public boolean unregisterListener(ProcessorLifecycleEventListener<? extends ProcessorLifecycleEvent> listener) {
		return eventDispatcher.unregisterListener(listener);
	}

	@Override
	public void dispatch(ProcessorLifecycleEvent e) throws ProcessorLifecycleException, ProcessorException {
		eventDispatcher.dispatch(e);
	}


}

package pl.bedkowski.code.jdd.gwt_ejb_preprocessor;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.persistence.Entity;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("javax.persistence.Entity")
@SupportedSourceVersion(SourceVersion.RELEASE_5)
public class Jsr303Processor extends AbstractProcessor {

	@SuppressWarnings("unchecked")
	private static final Class<BeanValidationProcessingStrategy<?>>[] SUPPORTED_CONSTRAINTS = new Class[] {
		NotNullAnnotationStrategy.class,
		SizeAnnotationStrategy.class,
		MinAnnotationStrategy.class,
		MaxAnnotationStrategy.class
	};

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Set<? extends Element> entities = roundEnv.getElementsAnnotatedWith(Entity.class);

		for(Element entity : entities) {
			try {
				processEntity((TypeElement)entity);
			} catch (Exception e) {
				e.printStackTrace();
				printError(e.getMessage(), entity);
			}
		}

		return true;
	}

	private void processEntity(TypeElement entity) throws IOException, InstantiationException, IllegalAccessException {
		List<? extends Element> members = processingEnv.getElementUtils().getAllMembers(entity);
		Map<String, List<BeanValidationProcessingStrategy<?>>> constraints = new HashMap<String, List<BeanValidationProcessingStrategy<?>>>();
		for(Element field : members) {
			if (field.getKind() != ElementKind.FIELD) {
				continue;
			}

			List<BeanValidationProcessingStrategy<?>> foundAvs = new LinkedList<BeanValidationProcessingStrategy<?>>();
			for(Class<BeanValidationProcessingStrategy<?>> avClass : SUPPORTED_CONSTRAINTS) {
				BeanValidationProcessingStrategy<?> av = avClass.newInstance();
				if (av.read(field)) {
					foundAvs.add(av);
				}
			}

			if (foundAvs.isEmpty()) {
				continue;
			}

			constraints.put(field.getSimpleName().toString(), foundAvs);
		}

		if (constraints.isEmpty()) {
			return;
		}

		writeClass(pkg(entity), entity.getSimpleName(), constraints);
	}

	private void writeClass(Name pkg, Name simpleName,
			Map<String, List<BeanValidationProcessingStrategy<?>>> constraints) throws IOException {

		JavaFileObject jfo = processingEnv.getFiler().createSourceFile(pkg+"."+simpleName+"Constraints");
		Writer writer = jfo.openWriter();

		writer.write("package " + pkg + ";\n\n");
		writer.write("import static pl.bedkowski.code.jdd.validation.Constraints.*; \n\n");
		writer.write("public class " + simpleName + "Constraints { \n");

		for(Entry<String, List<BeanValidationProcessingStrategy<?>>> e : constraints.entrySet()) {
			if (e.getValue().isEmpty()) {
				continue;
			}
			writer.write("\tpublic static java.util.List<pl.bedkowski.code.jdd.validation.Constraint> " + e.getKey() + "() {\n");
			writer.write("\t\treturn constraints(");
			boolean first = true;
			for(BeanValidationProcessingStrategy<?> av : e.getValue()) {
				writer.write((first ? "" : ", ") + av.getMethodCall());
				first = false;
			}
			writer.write(");\n\t}\n\n");
		}

		writer.write("}");
		writer.flush();
		writer.close();
	}

	private static final Name pkg(TypeElement typeElement) {
		PackageElement packageElement = (PackageElement) typeElement.getEnclosingElement();
		return packageElement.getQualifiedName();
	}

	private void printError(String msg, Element element) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, element);
	}

}

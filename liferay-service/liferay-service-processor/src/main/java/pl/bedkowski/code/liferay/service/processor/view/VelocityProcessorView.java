package pl.bedkowski.code.liferay.service.processor.view;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import pl.bedkowski.code.liferay.service.processor.controller.LiferayServiceProcessor.WriterFactory;
import pl.bedkowski.code.liferay.service.processor.model.ProcessorModel;
import pl.bedkowski.code.liferay.service.processor.model.ProcessorModel.Suffix;
import pl.bedkowski.code.liferay.service.processor.util.PropertiesUtil;

public class VelocityProcessorView implements ProcessorView {
	private static final String VELOCITY_TEMPLATE_PATH_PATTERN = "velocity/tpl/Liferay%s.java.vm";
	private static final String VELOCITY_PROPERTIES_DEFAULT_PATH = "velocity/init.properties";

	private final VelocityEngine velocityEngine = new VelocityEngine();

	private final Properties props;

	public VelocityProcessorView() throws IOException {
		this.props = PropertiesUtil.load(VELOCITY_PROPERTIES_DEFAULT_PATH, getClass());
	}

	/* (non-Javadoc)
	 * @see pl.bedkowski.code.liferay.service.processor.view.ProcessorView#init()
	 */
	@Override
	public void init() {
		velocityEngine.init(props);
	}

	/* (non-Javadoc)
	 * @see pl.bedkowski.code.liferay.service.processor.view.ProcessorView#writeClass(pl.bedkowski.code.liferay.service.processor.model.ProcessorModel)
	 */
	@Override
	public void writeClass(ProcessorModel model, WriterFactory writerFactory) throws IOException {
		VelocityContext velocityContext = new VelocityContext(new HashMap<String, Object>(model.getModelMap()));

		String qualifiedClassName = model.getQualifiedName();
		for(Suffix suffix : model.getSuffixes()) {
			String qualifiedTargetClassName = qualifiedClassName + suffix;
			writeClass(velocityContext, writerFactory.openWriter(qualifiedTargetClassName), String.format(VELOCITY_TEMPLATE_PATH_PATTERN, suffix.toString()));
		}
	}

	/**
	 *
	 * @param velocityContext
	 * @param targetClassName
	 * @param classTemplateName
	 * @throws IOException
	 */
	private void writeClass(VelocityContext velocityContext, Writer writer,String classTemplateName) throws IOException {
		Template vt = velocityEngine.getTemplate(classTemplateName);
		vt.merge(velocityContext, writer);
		writer.close();
	}
}

package pl.bedkowski.code.liferay.service.processor.view;

import java.io.IOException;

import pl.bedkowski.code.liferay.service.processor.controller.LiferayServiceProcessor.WriterFactory;
import pl.bedkowski.code.liferay.service.processor.model.ProcessorModel;

public interface ProcessorView {

	void init();

	/**
	 *
	 * @param model
	 * @param writerFactory
	 * @throws IOException
	 */
	void writeClass(ProcessorModel model, WriterFactory writerFactory) throws IOException;

}
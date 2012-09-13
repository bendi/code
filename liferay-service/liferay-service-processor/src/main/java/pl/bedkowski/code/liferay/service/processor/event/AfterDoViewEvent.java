package pl.bedkowski.code.liferay.service.processor.event;

import javax.lang.model.element.TypeElement;

import pl.bedkowski.code.liferay.service.processor.model.ProcessorModel;

public final class AfterDoViewEvent extends BaseProcessorEvent {

	public AfterDoViewEvent(TypeElement iface, ProcessorModel model) {
		super(iface, model);
	}

}

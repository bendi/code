package pl.bedkowski.code.liferay.service.processor.event;

import javax.lang.model.element.TypeElement;

import pl.bedkowski.code.liferay.service.processor.model.ProcessorModel;

abstract class BaseProcessorEvent implements ProcessorLifecycleEvent {

	private TypeElement iface;
	private ProcessorModel model;

	public BaseProcessorEvent(TypeElement iface, ProcessorModel model) {
		this.iface = iface;
		this.model = model;
	}

	public TypeElement getIface() {
		return iface;
	}

	public ProcessorModel getModel() {
		return model;
	}
}

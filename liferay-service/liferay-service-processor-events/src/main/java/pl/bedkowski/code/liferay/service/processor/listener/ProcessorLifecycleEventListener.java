package pl.bedkowski.code.liferay.service.processor.listener;

import pl.bedkowski.code.liferay.service.processor.event.ProcessorLifecycleEvent;
import pl.bedkowski.code.liferay.service.processor.exception.ProcessorLifecycleException;

/**
 *
 * @author marek
 *
 * @param <T>
 */
public interface ProcessorLifecycleEventListener<T extends ProcessorLifecycleEvent> {

	/**
	 *
	 * @param event
	 * @throws ProcessorLifecycleException
	 */
	void on(T event) throws ProcessorLifecycleException;
}

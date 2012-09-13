package pl.bedkowski.code.liferay.service.processor.dispatcher;

import pl.bedkowski.code.liferay.service.processor.event.ProcessorLifecycleEvent;
import pl.bedkowski.code.liferay.service.processor.exception.InvalidListenerException;
import pl.bedkowski.code.liferay.service.processor.exception.ProcessorException;
import pl.bedkowski.code.liferay.service.processor.exception.ProcessorLifecycleException;
import pl.bedkowski.code.liferay.service.processor.listener.ProcessorLifecycleEventListener;

public interface ProcessorLifecycleEventDispatcher {

	/**
	 *
	 * @param listener
	 * @return
	 * @throws InvalidListenerException
	 */
	boolean registerListener(ProcessorLifecycleEventListener<? extends ProcessorLifecycleEvent> listener) throws InvalidListenerException;

	/**
	 *
	 * @param listener
	 * @return
	 */
	boolean unregisterListener(ProcessorLifecycleEventListener<? extends ProcessorLifecycleEvent> listener);

	/**
	 *
	 * @param event
	 * @throws ProcessorLifecycleException
	 * @throws ProcessorException
	 */
	void dispatch(ProcessorLifecycleEvent event) throws ProcessorLifecycleException, ProcessorException;

}

package pl.bedkowski.code.liferay.service.processor.dispatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.bedkowski.code.liferay.service.processor.event.ProcessorLifecycleEvent;
import pl.bedkowski.code.liferay.service.processor.exception.InvalidListenerException;
import pl.bedkowski.code.liferay.service.processor.exception.ProcessorException;
import pl.bedkowski.code.liferay.service.processor.exception.ProcessorLifecycleException;
import pl.bedkowski.code.liferay.service.processor.listener.ProcessorLifecycleEventListener;

public class DefaultProcessorLifecycleEventDispatcher implements ProcessorLifecycleEventDispatcher {

	private Map<Class<? extends ProcessorLifecycleEvent>, List<MethodProxy<ProcessorLifecycleEventListener<? extends ProcessorLifecycleEvent>>>> eventsMap;

	{
		eventsMap = new HashMap<Class<? extends ProcessorLifecycleEvent>, List<MethodProxy<ProcessorLifecycleEventListener<? extends ProcessorLifecycleEvent>>>>();
	}

	@Override
	public boolean registerListener(ProcessorLifecycleEventListener<? extends ProcessorLifecycleEvent> listener) throws InvalidListenerException {
		Class<? extends ProcessorLifecycleEvent> clz = getEventClass(listener);
		return listeners(clz).add(new MethodProxy<ProcessorLifecycleEventListener<? extends ProcessorLifecycleEvent>>(listener, "on", clz));
	}

	@Override
	public boolean unregisterListener(ProcessorLifecycleEventListener<? extends ProcessorLifecycleEvent> listener) {
		List<MethodProxy<ProcessorLifecycleEventListener<? extends ProcessorLifecycleEvent>>> listeners = listeners(getEventClass(listener));
		for(int i=0; i < listeners.size(); i++) {
			MethodProxy<ProcessorLifecycleEventListener<? extends ProcessorLifecycleEvent>> item = listeners.get(i);
			if (item.getTarget().equals(listener)) {
				listeners.remove(i);
				return true;
			}
		}
		return false;
	}

	@Override
	public void dispatch(ProcessorLifecycleEvent event) throws ProcessorLifecycleException, ProcessorException {
		for(MethodProxy<ProcessorLifecycleEventListener<? extends ProcessorLifecycleEvent>> listener : listeners(event.getClass())) {
			try {
				listener.call(event);
			} catch(InvocationTargetException e) {
				if (e.getCause() instanceof ProcessorLifecycleException) {
					throw (ProcessorLifecycleException)e.getCause();
				};
			} catch (Exception e) {
				throw new ProcessorException("Problem processing listener: " + event.getClass(), e);
			}
		}
	}


	private List<MethodProxy<ProcessorLifecycleEventListener<? extends ProcessorLifecycleEvent>>> listeners(Class<? extends ProcessorLifecycleEvent> clz) {
		List<MethodProxy<ProcessorLifecycleEventListener<? extends ProcessorLifecycleEvent>>> ret;
		if (eventsMap.containsKey(clz)) {
			ret = eventsMap.get(clz);
		} else {
			ret = new ArrayList<MethodProxy<ProcessorLifecycleEventListener<? extends ProcessorLifecycleEvent>>>();
			eventsMap.put(clz, ret);
		}
		return ret;
	}


	@SuppressWarnings("unchecked")
	private Class<? extends ProcessorLifecycleEvent> getEventClass(ProcessorLifecycleEventListener<? extends ProcessorLifecycleEvent> listener) {
		Type[] ifaces = listener.getClass().getGenericInterfaces();
		ParameterizedType parameterizedType = (ParameterizedType) (ifaces[0]);
		return (Class<? extends ProcessorLifecycleEvent>) parameterizedType.getActualTypeArguments()[0];
	}

}

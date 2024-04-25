package dispatchers;


import events.Event;
import interfaces.IEventDispatcher;
import interfaces.IEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SimpleEventDispatcher implements IEventDispatcher
{
	private final Map<String, List<IEventListener>> listeners;
	
	public SimpleEventDispatcher() {
		this.listeners = new HashMap<>();
	}
	
	@Override
	public void addListener(String eventType, IEventListener listener) {
		this.listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
	}
	
	@Override
	public void removeListener(String eventType, IEventListener listener) {
		List<IEventListener> listenersOfType = this.listeners.get(eventType);
		if (listenersOfType != null) {
			listenersOfType.remove(listener);
		}
	}
	
	@Override
	public void dispatchEvent(String eventType, Event event) {
		List<IEventListener> listenersOfType = this.listeners.get(eventType);
		if (listenersOfType != null) {
			for (IEventListener listener : listenersOfType) {
				listener.handleEvent(event);
			}
		}
	}
}

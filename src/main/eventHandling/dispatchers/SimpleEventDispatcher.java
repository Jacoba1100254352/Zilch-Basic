package eventHandling.dispatchers;


import eventHandling.events.Event;
import eventHandling.events.GameEventType;
import eventHandling.listeners.IEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SimpleEventDispatcher implements IEventDispatcher
{
	private final Map<String, List<IEventListener>> listeners = new HashMap<>();
	
	@Override
	public void addListener(GameEventType eventType, IEventListener listener) {
		listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
	}
	
	@Override
	public void removeListener(GameEventType eventType, IEventListener listener) {
		List<IEventListener> listenersOfType = listeners.get(eventType);
		if (listenersOfType != null) {
			listenersOfType.remove(listener);
		}
	}
	
	@Override
	public void dispatchEvent(GameEventType eventType, Event event) {
		List<IEventListener> listenersOfType = listeners.get(eventType);
		if (listenersOfType != null) {
			for (IEventListener listener : listenersOfType) {
				listener.handleEvent(event);
			}
		}
	}
}


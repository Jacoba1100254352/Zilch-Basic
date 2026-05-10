package eventHandling.dispatchers;


import eventHandling.events.Event;
import eventHandling.events.GameEventType;
import eventHandling.listeners.IEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SimpleEventDispatcher implements IEventDispatcher
{
	private final Map<GameEventType, List<IEventListener>> listeners = new HashMap<>();
	
	@Override
	public void addListener(GameEventType eventType, IEventListener listener) {
		listeners.computeIfAbsent(eventType, _ -> new ArrayList<>()).add(listener);
	}
	
	@Override
	public void removeListener(GameEventType eventType, IEventListener listener) {
		List<IEventListener> listenersOfType = listeners.get(eventType);
		if (listenersOfType != null) {
			listenersOfType.remove(listener);
		}
	}
	
	@Override
	public void dispatchEvent(Event event) throws IOException {
		List<IEventListener> listenersOfType = listeners.get(event.getType());
		if (listenersOfType != null) {
			for (IEventListener listener : listenersOfType) {
				listener.handleEvent(event);
			}
		}
	}
}


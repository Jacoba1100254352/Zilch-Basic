package eventHandling.dispatchers;


import eventHandling.events.Event;
import eventHandling.events.GameEventType;
import eventHandling.listeners.IEventListener;

import java.io.IOException;


public interface IEventDispatcher
{
	void addListener(GameEventType eventType, IEventListener listener);
	
	void removeListener(GameEventType eventType, IEventListener listener);
	
	void dispatchEvent(Event event) throws IOException;
}

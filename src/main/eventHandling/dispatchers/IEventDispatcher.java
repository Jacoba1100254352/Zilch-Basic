package eventHandling.dispatchers;


import eventHandling.events.Event;
import eventHandling.events.GameEventType;
import eventHandling.listeners.IEventListener;


public interface IEventDispatcher
{
	void addListener(GameEventType eventType, IEventListener listener);
	
	void removeListener(GameEventType eventType, IEventListener listener);
	
	void dispatchEvent(GameEventType eventType, Event event);
}

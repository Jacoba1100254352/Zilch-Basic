package interfaces;


import events.Event;


public interface IEventDispatcher
{
	void addListener(String eventType, IEventListener listener);
	
	void removeListener(String eventType, IEventListener listener);
	
	void dispatchEvent(String eventType, Event event);
}

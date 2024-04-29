package eventHandling.listeners;


import eventHandling.events.Event;


public interface IEventListener
{
	void handleEvent(Event event);
}

package eventHandling.listeners;


import eventHandling.events.Event;

import java.io.IOException;


public interface IEventListener
{
	void handleEvent(Event event) throws IOException;
}

package eventHandling.listeners;


import eventHandling.events.Event;


public class ScoreUpdateListener implements IEventListener
{
	@Override
	public void handleEvent(Event event) {
		// Logic to handle score update
		System.out.println("Score has been updated!");
	}
}


package listeners;


import events.Event;
import interfaces.IEventListener;


public class ScoreUpdateListener implements IEventListener
{
	@Override
	public void handleEvent(Event event) {
		// Logic to handle score update
		System.out.println("Score has been updated!");
	}
}


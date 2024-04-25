package listeners;


import events.Event;
import interfaces.IEventListener;


public class RuleChangeListener implements IEventListener
{
	@Override
	public void handleEvent(Event event) {
		// Logic to handle game state change
		System.out.println("A rule has changed!");
	}
}

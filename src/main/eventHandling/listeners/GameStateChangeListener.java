package eventHandling.listeners;


import eventHandling.events.Event;


public class GameStateChangeListener implements IEventListener
{
	@Override
	public void handleEvent(Event event) {
		// Logic to handle game state change
		System.out.println("Game state has changed!");
	}
}

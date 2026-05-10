package eventHandling.dispatchers;


import eventHandling.events.Event;
import eventHandling.events.GameEventType;
import eventHandling.listeners.IEventListener;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;


class SimpleEventDispatcherTest
{
	@Test
	void dispatchEventNotifiesRegisteredListeners() throws Exception {
		SimpleEventDispatcher dispatcher = new SimpleEventDispatcher();
		RecordingListener listener = new RecordingListener();
		Event event = new Event(GameEventType.SCORE_UPDATED);
		dispatcher.addListener(GameEventType.SCORE_UPDATED, listener);

		dispatcher.dispatchEvent(event);

		assertEquals(List.of(event), listener.events);
	}

	@Test
	void removeListenerStopsFutureNotifications() throws Exception {
		SimpleEventDispatcher dispatcher = new SimpleEventDispatcher();
		RecordingListener listener = new RecordingListener();
		Event event = new Event(GameEventType.SCORE_UPDATED);
		dispatcher.addListener(GameEventType.SCORE_UPDATED, listener);
		dispatcher.removeListener(GameEventType.SCORE_UPDATED, listener);

		dispatcher.dispatchEvent(event);

		assertEquals(List.of(), listener.events);
	}

	@Test
	void dispatchEventWithoutListenersDoesNotThrow() {
		SimpleEventDispatcher dispatcher = new SimpleEventDispatcher();

		assertDoesNotThrow(() -> dispatcher.dispatchEvent(new Event(GameEventType.GAME_OVER)));
	}

	private static final class RecordingListener implements IEventListener
	{
		private final List<Event> events = new ArrayList<>();

		@Override
		public void handleEvent(Event event) {
			events.add(event);
		}
	}
}

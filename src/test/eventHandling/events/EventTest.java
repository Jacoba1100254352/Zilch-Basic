package eventHandling.events;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class EventTest
{
	@Test
	void storesAndReturnsTypedDataEntries() {
		Event event = new Event(GameEventType.SCORE_UPDATED);
		event.setData(EventDataKey.MESSAGE, "updated");

		assertEquals(GameEventType.SCORE_UPDATED, event.getType());
		assertEquals("updated", event.getData(EventDataKey.MESSAGE));
		assertNull(event.getData(EventDataKey.SCORE));
	}
}

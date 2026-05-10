package eventHandling.listeners;


import eventHandling.events.Event;
import eventHandling.events.GameEventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;


class RuleChangeListenerTest
{
	private PrintStream originalOut;
	private ByteArrayOutputStream outputStream;

	@BeforeEach
	void setUp() {
		originalOut = System.out;
		outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));
	}

	@AfterEach
	void tearDown() {
		System.setOut(originalOut);
	}

	@Test
	void handleEventPrintsNotification() {
		new RuleChangeListener().handleEvent(new Event(GameEventType.RULE_CHANGE));

		assertTrue(outputStream.toString().contains("A rule has changed!"));
	}
}

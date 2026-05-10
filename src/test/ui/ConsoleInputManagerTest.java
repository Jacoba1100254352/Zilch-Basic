package ui;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ConsoleInputManagerTest
{
	private InputStream originalIn;
	private PrintStream originalOut;
	private ByteArrayOutputStream outputStream;

	@BeforeEach
	void setUp() {
		originalIn = System.in;
		originalOut = System.out;
		outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));
	}

	@AfterEach
	void tearDown() {
		System.setIn(originalIn);
		System.setOut(originalOut);
	}

	@Test
	void getInputStringReadsTheNextLine() {
		System.setIn(new ByteArrayInputStream("hello\n".getBytes(StandardCharsets.UTF_8)));
		ConsoleInputManager inputManager = new ConsoleInputManager();

		assertEquals("hello", inputManager.getInputString());
	}

	@Test
	void getInputIntRepromptsUntilItReceivesAnInteger() {
		System.setIn(new ByteArrayInputStream("abc\n42\n".getBytes(StandardCharsets.UTF_8)));
		ConsoleInputManager inputManager = new ConsoleInputManager();

		assertEquals(42, inputManager.getInputInt());
		assertTrue(outputStream.toString().contains("Please enter a valid integer:"));
	}

	@Test
	void waitForEnterKeyPrintsThePausePrompt() {
		System.setIn(new ByteArrayInputStream("\n".getBytes(StandardCharsets.UTF_8)));
		ConsoleInputManager inputManager = new ConsoleInputManager();

		inputManager.waitForEnterKey().run();

		assertTrue(outputStream.toString().contains("Press enter to continue"));
	}
}

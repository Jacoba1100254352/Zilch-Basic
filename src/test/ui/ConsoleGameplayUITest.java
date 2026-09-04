package ui;


import model.entities.Dice;
import model.entities.GameOption;
import model.entities.Player;
import model.entities.Score;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rules.managers.RuleType;
import support.TestDoubles;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ConsoleGameplayUITest
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
	void displayWelcomeMessagePrintsRulesAndPausePrompt() throws IOException {
		ConsoleMessage consoleMessage = newConsoleMessage("\n");

		consoleMessage.displayWelcomeMessage();

		String output = outputStream.toString();
		assertTrue(output.contains("Welcome to Zilch!"));
		assertTrue(output.contains("Press enter to continue"));
	}

	@Test
	void displayGameOptionsShowsRoundScoreAndDescriptions() throws IOException {
		ConsoleMessage consoleMessage = newConsoleMessage("");
		Score score = new Score();
		score.setRoundScore(150);
		List<GameOption> options = List.of(
				new GameOption(RuleType.SINGLE, "Single", "Single one", 1, 100, Map.of(1, 1)),
				new GameOption(RuleType.SET, "Set", "Three pairs", null, 1000, Map.of(1, 2, 2, 2, 5, 2))
		);

		consoleMessage.displayGameOptions(score, options);

		String output = outputStream.toString();
		assertTrue(output.contains("Available Options:"));
		assertTrue(output.contains("Round score: 150"));
		assertTrue(output.contains("1. Single [1] - 100 points. Single one"));
		assertTrue(output.contains("2. Set - 1000 points. Three pairs"));
	}

	@Test
	void displayCurrentScoreAndDiceRenderReadableText() throws IOException {
		ConsoleMessage consoleMessage = newConsoleMessage("");
		Map<Integer, Integer> diceValues = new LinkedHashMap<>();
		diceValues.put(1, 2);
		diceValues.put(5, 1);
		Dice dice = new Dice(diceValues);
		dice.calculateNumDiceInPlay();

		consoleMessage.displayCurrentScore("Jacob", 250);
		consoleMessage.displayDice(dice);

		String output = outputStream.toString();
		assertTrue(output.contains("Jacob's current round score: 250"));
		assertTrue(output.contains("You have 3 dice left."));
		assertTrue(output.contains("1, 1, 5"));
	}

	@Test
	void displayHighScoreInfoShowsCurrentRoundWhenPlayerHasNotOpened() throws IOException {
		ConsoleMessage consoleMessage = newConsoleMessage("");
		Player player = TestDoubles.player("Jacob");
		player.score().setRoundScore(350);

		consoleMessage.displayHighScoreInfo(player, "Alice");

		assertTrue(outputStream.toString().contains("Jacob's current score: 350"));
	}

	@Test
	void legacyYouNameUsesNaturalConsolePossessives() throws IOException {
		ConsoleMessage consoleMessage = newConsoleMessage("");
		Player player = TestDoubles.player("You");
		player.score().setRoundScore(350);

		consoleMessage.displayCurrentScore("You", 350);
		consoleMessage.displayHighScoreInfo(player, "You");

		String output = outputStream.toString();
		assertTrue(output.contains("Your current round score: 350"));
		assertTrue(output.contains("Your current score: 350"));
		assertFalse(output.contains("You's"));
	}

	@Test
	void displayAndWaitLastRoundTieAndWinnerMessagesArePrinted() throws IOException {
		ConsoleMessage consoleMessage = newConsoleMessage("\n");
		Player alice = TestDoubles.player("Alice");
		Player bob = TestDoubles.player("Bob");

		consoleMessage.displayAndWait("Bust!\n");
		consoleMessage.displayLastRoundMessage(alice, () -> {
		});
		consoleMessage.announceTie(List.of(alice, bob), 5000);
		consoleMessage.announceWinner(alice, 5200);

		String output = outputStream.toString();
		assertTrue(output.contains("Bust!"));
		assertTrue(output.contains("Press enter to continue"));
		assertTrue(output.contains("Alice reached"));
		assertTrue(output.contains("Final Chase"));
		assertTrue(output.contains("Alice, Bob have tied with 5000 points!"));
		assertTrue(output.contains("Alice won with 5200 points!"));
	}

	@Test
	void lastRoundMessageUsesTheActiveGameWinningScore() {
		Scanner scanner = new Scanner(new ByteArrayInputStream("\n".getBytes(StandardCharsets.UTF_8)));
		ConsoleMessage consoleMessage = new ConsoleMessage(scanner, 7500);

		consoleMessage.displayLastRoundMessage(TestDoubles.player("Alice"), () -> {
		});

		assertTrue(outputStream.toString().contains("Alice reached 7500 points!"));
	}

	@Test
	void pauseAndContinueRunsTheProvidedCallback() throws IOException {
		ConsoleMessage consoleMessage = newConsoleMessage("");
		AtomicBoolean called = new AtomicBoolean(false);

		consoleMessage.pauseAndContinue(() -> called.set(true));

		assertTrue(called.get());
	}

	private ConsoleMessage newConsoleMessage(String input) throws IOException {
		Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
		return new ConsoleMessage(scanner);
	}
}

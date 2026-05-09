package ui;


import managers.GameCoordinator;
import modelManagers.PlayerManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ConsoleUserInputHandlerTest
{
	private final InputStream standardIn = System.in;
	private final PrintStream standardOut = System.out;
	private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
	
	@BeforeEach
	void setUp() {
		outputStreamCaptor.reset();
		System.setOut(new PrintStream(outputStreamCaptor));
	}
	
	@AfterEach
	void tearDown() {
		System.setIn(standardIn);
		System.setOut(standardOut);
	}
	
	@Test
	void endTurnUsesDisplayedIndexWithoutRollAgain() {
		System.setIn(new ByteArrayInputStream("1\n\n".getBytes(StandardCharsets.UTF_8)));
		
		GameCoordinator gameCoordinator = new GameCoordinator();
		gameCoordinator.setPlayerManager(new PlayerManager(List.of("TestPlayer"), 5000));
		gameCoordinator.getPlayerManager().getCurrentPlayer().score().increasePermanentScore(1500);
		gameCoordinator.setUserInputHandler(new ConsoleUserInputHandler(gameCoordinator));
		
		gameCoordinator.getUserInputHandler().inputGameOption();
		
		assertFalse(gameCoordinator.getGameStateManager().getContinueTurn(), "Choosing the displayed end-turn option should end the turn");
		assertTrue(outputStreamCaptor.toString().contains("1. End Turn"), "The displayed end-turn option should match the accepted menu index");
	}
	
	@Test
	void getValidScoreLimitRetriesUntilAValidValueIsEntered() {
		System.setIn(new ByteArrayInputStream("abc\n900\n1000\n".getBytes(StandardCharsets.UTF_8)));
		
		GameCoordinator gameCoordinator = new GameCoordinator();
		ConsoleUserInputHandler userInputHandler = new ConsoleUserInputHandler(gameCoordinator);
		gameCoordinator.setUserInputHandler(userInputHandler);
		
		assertEquals(1000, userInputHandler.getValidScoreLimit(), "Input handling should keep prompting until the minimum valid score limit is entered");
	}
	
	@Test
	void getPlayerNamesRetriesUntilAValidPlayerCountIsEntered() {
		System.setIn(new ByteArrayInputStream("0\n2\nAlice\nBob\n".getBytes(StandardCharsets.UTF_8)));
		
		GameCoordinator gameCoordinator = new GameCoordinator();
		ConsoleUserInputHandler userInputHandler = new ConsoleUserInputHandler(gameCoordinator);
		gameCoordinator.setUserInputHandler(userInputHandler);
		
		assertEquals(List.of("Alice", "Bob"), userInputHandler.getPlayerNames(), "Input handling should retry invalid player counts and then capture each name");
	}
	
	@Test
	void inputGameOptionSelectsAndProcessesAScoringMove() {
		System.setIn(new ByteArrayInputStream("1\n".getBytes(StandardCharsets.UTF_8)));
		
		GameCoordinator gameCoordinator = new GameCoordinator();
		gameCoordinator.setPlayerManager(new PlayerManager(List.of("TestPlayer"), 5000));
		gameCoordinator.setUserInputHandler(new ConsoleUserInputHandler(gameCoordinator));
		gameCoordinator.getPlayerManager().getCurrentPlayer().dice().setDiceSetMap(Map.of(5, 1));
		gameCoordinator.getPlayerManager().getCurrentPlayer().dice().calculateNumDiceInPlay();
		gameCoordinator.getGameOptionManager().evaluateGameOptions();
		
		gameCoordinator.getUserInputHandler().inputGameOption();
		
		assertEquals(50, gameCoordinator.getPlayerManager().getCurrentPlayer().score().getRoundScore(), "Selecting a single scoring die should add its value to the round score");
		assertEquals(0, gameCoordinator.getPlayerManager().getCurrentPlayer().dice().getNumDiceInPlay(), "Selecting a single scoring die should remove it from play");
	}
	
	@Test
	void inputGameOptionCanChooseRollAgain() {
		System.setIn(new ByteArrayInputStream("1\n".getBytes(StandardCharsets.UTF_8)));
		
		GameCoordinator gameCoordinator = new GameCoordinator();
		gameCoordinator.setPlayerManager(new PlayerManager(List.of("TestPlayer"), 5000));
		gameCoordinator.getPlayerManager().getCurrentPlayer().score().setRoundScore(1000);
		gameCoordinator.getGameOptionManager().setOptionSelectedForCurrentRoll(true);
		gameCoordinator.setUserInputHandler(new ConsoleUserInputHandler(gameCoordinator));
		
		gameCoordinator.getUserInputHandler().inputGameOption();
		
		assertTrue(gameCoordinator.getGameStateManager().getReroll(), "Choosing the roll-again action should mark the turn for another roll");
	}
	
	@Test
	void endTurnStaysAfterRollAgainWhenBothActionsAreVisible() {
		System.setIn(new ByteArrayInputStream("2\n\n".getBytes(StandardCharsets.UTF_8)));
		
		GameCoordinator gameCoordinator = new GameCoordinator();
		gameCoordinator.setPlayerManager(new PlayerManager(List.of("TestPlayer"), 5000));
		gameCoordinator.getPlayerManager().getCurrentPlayer().score().setRoundScore(1000);
		gameCoordinator.getGameOptionManager().setOptionSelectedForCurrentRoll(true);
		gameCoordinator.setUserInputHandler(new ConsoleUserInputHandler(gameCoordinator));
		
		gameCoordinator.getUserInputHandler().inputGameOption();
		
		assertFalse(gameCoordinator.getGameStateManager().getContinueTurn(), "The end-turn choice should still work when roll-again is also visible");
		assertTrue(outputStreamCaptor.toString().contains("1. Roll again"), "Roll-again should remain the first extra action when available");
		assertTrue(outputStreamCaptor.toString().contains("2. End Turn"), "End turn should remain the last visible action");
	}
}

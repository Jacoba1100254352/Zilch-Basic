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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ConsoleUserInputHandlerTest
{
	private final InputStream standardIn = System.in;
	private final PrintStream standardOut = System.out;
	private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
	
	@BeforeEach
	void setUp() {
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

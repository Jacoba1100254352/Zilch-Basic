package managers;


import modelManagers.PlayerManager;
import models.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class GameStateManagerTest
{
	
	private GameStateManager gameStateManager;
	private Player currentPlayer;
	
	@BeforeEach
	void setUp() {
		final int scoreLimit = 5000;
		GameCoordinator gameCoordinator = new GameCoordinator(); // Assuming GameCoordinator can be instantiated
		
		// Use the fake input handler that never blocks or prompts the console
		gameCoordinator.setUserInputHandler(new FakeUserInputHandler(gameCoordinator));
		
		gameStateManager = new GameStateManager(gameCoordinator);
		List<String> playerNames = List.of("TestPlayer");
		
		// Initialize player manager with players
		gameCoordinator.setPlayerManager(new PlayerManager(playerNames, scoreLimit));
		currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
	}
	
	@Test
	@DisplayName("Positive: Initialize Roll Cycle")
	void initializeRollCyclePass() {
		gameStateManager.initializeRollCycle();
		assertTrue(gameStateManager.getReroll(), "Continue turn should be true after initializing roll cycle");
		assertTrue(gameStateManager.getContinueTurn(), "Continue selecting should be true after initializing roll cycle");
	}
	
	@Test
	@DisplayName("Negative: Initialize Roll Cycle")
	void initializeRollCycleFail() {
		gameStateManager.initializeRollCycle();
		currentPlayer.score().setRoundScore(100);
		assertNotEquals(0, currentPlayer.score().getRoundScore(), "Round score should not be reset if already non-zero");
	}
	
	@Test
	@DisplayName("Positive: Handle First Roll Bust")
	void handleFirstRollBustPass() {
		gameStateManager.handleFirstRollBust();
		assertEquals(50, currentPlayer.score().getRoundScore(), "Round score should be increased by 50 on first roll bust");
		assertTrue(gameStateManager.getReroll(), "Continue turn should be true after first roll bust");
	}
	
	@Test
	@DisplayName("Negative: Handle First Roll Bust")
	void handleFirstRollBustFail() {
		int initialScore = 100;
		currentPlayer.score().increasePermanentScore(initialScore);
		gameStateManager.handleFirstRollBust();
		assertEquals(initialScore, currentPlayer.score().getPermanentScore(), "Round score should remain unchanged");
	}
	
	@Test
	@DisplayName("Positive: Handle Bust")
	void handleBustPass() {
		gameStateManager.handleBust();
		assertEquals(0, currentPlayer.score().getRoundScore(), "Round score should be reset to 0 on bust");
		assertFalse(gameStateManager.getContinueTurn(), "Continue turn should be false after bust");
		assertFalse(gameStateManager.getReroll(), "Reroll should be false after bust");
	}
	
	@Test
	@DisplayName("Negative: Handle Bust")
	void handleBustFail() {
		int initialScore = 100;
		currentPlayer.score().increasePermanentScore(initialScore);
		gameStateManager.handleBust();
		assertEquals(initialScore, currentPlayer.score().getPermanentScore(), "Permanent score should remain unchanged");
	}
	
	@Test
	@DisplayName("Positive: Set Continue Turn")
	void setContinueTurnPass() {
		currentPlayer.score().setRoundScore(1000);
		gameStateManager.setContinueTurn(false);
		assertFalse(gameStateManager.getContinueTurn(), "Players who have reached the entry threshold should be allowed to end the turn");
	}
	
	@Test
	@DisplayName("Negative: Set Continue Turn, Insufficient Score")
	void setContinueTurnFail() {
		currentPlayer.score().setRoundScore(500);
		gameStateManager.setContinueTurn(false);
		assertTrue(gameStateManager.getContinueTurn(), "Players below the threshold should be forced to keep playing");
	}
	
	@Test
	@DisplayName("Can Roll Again Requires Points And A Prior Selection")
		void canRollAgainRequiresPointsAndSelection() {
			currentPlayer.score().setRoundScore(50);
			assertFalse(gameStateManager.canRollAgain(), "Scoring alone should not unlock rerolls before a scoring option is selected");
			
			GameCoordinator coordinator = new GameCoordinator();
			coordinator.setUserInputHandler(new FakeUserInputHandler(coordinator));
			coordinator.setPlayerManager(new PlayerManager(List.of("TestPlayer"), 5000));
		coordinator.getPlayerManager().getCurrentPlayer().score().setRoundScore(50);
		coordinator.getGameOptionManager().setOptionSelectedForCurrentRoll(true);
		assertTrue(coordinator.getGameStateManager().canRollAgain(), "Once a scoring option is selected, a positive round score should allow rerolling");
	}
	
	@Test
	@DisplayName("Can End Turn Checks Round Or Permanent Score")
	void canEndTurnChecksRoundOrPermanentScore() {
		assertFalse(gameStateManager.canEndTurn(), "Players should not be able to bank before reaching the threshold");
		
		currentPlayer.score().setRoundScore(1000);
		assertTrue(gameStateManager.canEndTurn(), "A large enough round score should unlock banking");
		
		currentPlayer.score().setRoundScore(0);
		currentPlayer.score().increasePermanentScore(1200);
		assertTrue(gameStateManager.canEndTurn(), "Players already in the game should be able to bank smaller follow-up turns");
	}
}

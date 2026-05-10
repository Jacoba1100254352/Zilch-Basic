/*
package managers;


import modelManagers.PlayerManager;
import model.entities.Player;
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
		GameServer gameServer = new GameServer(); // Assuming GameServer can be instantiated
		gameStateManager = new GameStateManager(gameServer);
		List<String> playerNames = List.of("TestPlayer");
		
		// Initialize player manager with players
		gameServer.setPlayerManager(new PlayerManager(playerNames));
		currentPlayer = gameServer.getPlayerManager().getCurrentPlayer();
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
		assertFalse(gameStateManager.getReroll(), "Continue turn should be false after bust");
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
		currentPlayer.score().setRoundScore(1000); // Set the score to a value that allows turn to be ended
		
		gameStateManager.setReroll(true);
		assertTrue(gameStateManager.getReroll(), "Continue turn should be true when set to true");
		
		gameStateManager.setReroll(false);
		assertFalse(gameStateManager.getReroll(), "Continue turn should be false when set to false and round score is 1000 or more");
	}

    */
/*@Test
    @DisplayName("Negative: Set Continue Turn, Insufficient Score")
    void setContinueTurnFail() {
        currentPlayer.score().setRoundScore(500); // Set the score below the threshold that allows turn to be ended
        gameStateManager.setReroll(false);
        assertTrue(gameStateManager.getReroll(), "Continue turn should remain true if round score is below 1000");
    }*//*

}
*/

package managers;


import modelManagers.PlayerManager;
import models.GameOption;
import models.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class GameFlowManagerTest
{
	private final PrintStream standardOut = System.out;
	private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
	
	private GameCoordinator gameCoordinator;
	private GameFlowManager gameFlowManager;
	private Player player;
	
	@BeforeEach
	void setUp() {
		// Redirect all System.out to outputStreamCaptor
		outputStreamCaptor.reset();
		System.setOut(new PrintStream(outputStreamCaptor));
		
		// Set up game coordinator and player
		final int scoreLimit = 5000;
		gameCoordinator = new GameCoordinator();
		
		// Use the fake input handler that never blocks or prompts the console
		gameCoordinator.setUserInputHandler(new FakeUserInputHandler(gameCoordinator));
		
		// Initialize game flow manager
		gameFlowManager = new GameFlowManager(gameCoordinator);
		
		// Initialize player manager with players
		gameCoordinator.setPlayerManager(new PlayerManager(gameCoordinator.getUserInputHandler().getPlayerNames(), scoreLimit));
		player = gameCoordinator.getPlayerManager().getCurrentPlayer();
		
	}
	
	@AfterEach
	void tearDown() {
		System.setOut(standardOut);
	}
	
	@Test
	@DisplayName("Handle Bust Scenario")
	void handleBustScenario() {
		// Simulate a round with points
		player.score().setRoundScore(500);
		
		// Simulate a bust scenario
		gameCoordinator.getGameStateManager().handleBust();
		
		// Check if continueTurn is false after a bust
		assertFalse(gameCoordinator.getGameStateManager().getContinueTurn(), "Continue turn should be false after a bust");
		
		// Only call playTurn if continueTurn is false
		if (!gameCoordinator.getGameStateManager().getContinueTurn()) {
			gameFlowManager.playTurn(player, null);
		} else {
			fail("Continue turn should be false after a bust, but it was true");
		}
		
		// Check if the round score is reset
		assertEquals(0, player.score().getRoundScore(), "Round score should reset to 0 on bust");
	}
	
	@Test
	@DisplayName("Handle First Roll Bust Scenario")
	void handleFirstRollBustScenario() {
		gameCoordinator.setPlayerManager(new DeterministicPlayerManager(
				List.of("TestPlayer"),
				5000,
				Map.of(2, 2, 3, 1, 4, 2, 6, 1),
				Map.of(1, 3)
		));
		player = gameCoordinator.getPlayerManager().getCurrentPlayer();
		ScriptedUserInputHandler userInputHandler = new ScriptedUserInputHandler(
				gameCoordinator,
				5000,
				List.of("TestPlayer"),
				ScriptedUserInputHandler.select(GameOption.Type.MULTIPLE, 1),
				ScriptedUserInputHandler.endTurn()
		);
		gameCoordinator.setUserInputHandler(userInputHandler);
		
		gameCoordinator.getGameStateManager().initializeRollCycle();
		gameFlowManager.playTurn(player, null);
		
		assertEquals(1050, player.score().getPermanentScore(), "A first-roll bust bonus should carry into the eventual banked score");
		assertEquals(1, userInputHandler.getPauseCount(), "Recovering from a first-roll bust should pause exactly once");
	}
	
	@Test
	@DisplayName("Continue Turn with Reroll")
	void continueTurnWithReroll() {
		gameCoordinator.setPlayerManager(new DeterministicPlayerManager(
				List.of("TestPlayer"),
				5000,
				Map.of(5, 1, 2, 1, 3, 1, 4, 1, 6, 2),
				Map.of(1, 3, 2, 1, 3, 1)
		));
		player = gameCoordinator.getPlayerManager().getCurrentPlayer();
		gameCoordinator.setUserInputHandler(new ScriptedUserInputHandler(
				gameCoordinator,
				5000,
				List.of("TestPlayer"),
				ScriptedUserInputHandler.select(GameOption.Type.SINGLE, 5),
				ScriptedUserInputHandler.rollAgain(),
				ScriptedUserInputHandler.select(GameOption.Type.MULTIPLE, 1),
				ScriptedUserInputHandler.endTurn()
		));
		
		gameCoordinator.getGameStateManager().initializeRollCycle();
		gameFlowManager.playTurn(player, null);
		
		assertEquals(1050, player.score().getPermanentScore(), "A scored die should allow a reroll that keeps accumulating into the same banked turn");
	}
	
	@Test
	@DisplayName("Hot Dice Preserve Multiple Chain")
	void hotDicePreserveMultipleChain() throws Exception {
		player.dice().setNumDiceInPlay(0);
		player.score().setScoreFromMultiples(300);
		gameCoordinator.getGameOptionManager().setPreviouslySelectedMultipleValue(3);
		
		Method handleDiceRoll = GameFlowManager.class.getDeclaredMethod("handleDiceRoll", Player.class);
		handleDiceRoll.setAccessible(true);
		handleDiceRoll.invoke(gameFlowManager, player);
		
		assertEquals(300, player.score().getScoreFromMultiples(), "Hot dice should not reset the multiple chain");
		assertEquals(3, gameCoordinator.getGameOptionManager().getPreviouslySelectedMultipleValue(), "Hot dice should preserve the previously selected multiple");
	}
	
	@Test
	@DisplayName("Game Over Triggers At Score Limit")
	void gameOverTriggersAtScoreLimit() {
		player.score().increasePermanentScore(player.score().getScoreLimit() - 1);
		assertFalse(gameFlowManager.gameOver(player), "Scores below the limit should not end the game");
		
		player.score().increasePermanentScore(1);
		assertTrue(gameFlowManager.gameOver(player), "Reaching the score limit should end the game");
	}
	
	@Test
	@DisplayName("Handle Game End Announces Winner")
	void handleGameEndAnnouncesWinner() {
		gameCoordinator.setPlayerManager(new DeterministicPlayerManager(
				List.of("Alice", "Bob"),
				5000,
				Map.of(1, 3)
		));
		gameCoordinator.setUserInputHandler(new ScriptedUserInputHandler(
				gameCoordinator,
				5000,
				List.of("Alice", "Bob"),
				ScriptedUserInputHandler.select(GameOption.Type.MULTIPLE, 1),
				ScriptedUserInputHandler.endTurn()
		));
		gameCoordinator.getPlayerManager().getCurrentPlayer().score().increasePermanentScore(5000);
		gameCoordinator.getPlayerManager().switchToNextPlayer();
		gameCoordinator.getPlayerManager().getCurrentPlayer().score().increasePermanentScore(4500);
		gameCoordinator.getPlayerManager().switchToNextPlayer();
		
		gameFlowManager.handleGameEnd();
		
		assertTrue(outputStreamCaptor.toString().contains("Bob won with 5500 Points!"), "The last round should announce the new winner when another player overtakes the leader");
	}
	
	@Test
	@DisplayName("Handle Game End Announces Tie")
	void handleGameEndAnnouncesTie() {
		gameCoordinator.setPlayerManager(new DeterministicPlayerManager(
				List.of("Alice", "Bob"),
				5000,
				Map.of(5, 3)
		));
		gameCoordinator.setUserInputHandler(new ScriptedUserInputHandler(
				gameCoordinator,
				5000,
				List.of("Alice", "Bob"),
				ScriptedUserInputHandler.select(GameOption.Type.MULTIPLE, 5),
				ScriptedUserInputHandler.endTurn()
		));
		gameCoordinator.getPlayerManager().getCurrentPlayer().score().increasePermanentScore(5000);
		gameCoordinator.getPlayerManager().switchToNextPlayer();
		gameCoordinator.getPlayerManager().getCurrentPlayer().score().increasePermanentScore(4500);
		gameCoordinator.getPlayerManager().switchToNextPlayer();
		
		gameFlowManager.handleGameEnd();
		
		assertTrue(outputStreamCaptor.toString().contains("Alice, Bob have tied with 5000 Points!"), "The last round should announce a tie when players finish even");
	}
}

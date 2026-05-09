package managers;


import models.GameOption;
import models.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class GameCoordinatorTest
{
	private final PrintStream standardOut = System.out;
	private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
	
	@BeforeEach
	void setUp() {
		System.setOut(new PrintStream(outputStreamCaptor));
	}
	
	@AfterEach
	void tearDown() {
		System.setOut(standardOut);
	}
	
	@Test
	void setupGameInitializesPlayersFromUserInput() {
		GameCoordinator gameCoordinator = new GameCoordinator();
		ScriptedUserInputHandler userInputHandler = new ScriptedUserInputHandler(gameCoordinator, 2500, List.of("Alice", "Bob"));
		gameCoordinator.setUserInputHandler(userInputHandler);
		
		gameCoordinator.setupGame();
		
		assertNotNull(gameCoordinator.getPlayerManager(), "Setup should create a player manager");
		assertEquals(2, gameCoordinator.getPlayerManager().getPlayers().size(), "Setup should create every requested player");
		assertEquals("Alice", gameCoordinator.getPlayerManager().getCurrentPlayer().name(), "The first entered player should start");
		assertEquals(2500, gameCoordinator.getPlayerManager().getCurrentPlayer().score().getScoreLimit(), "Players should inherit the selected score limit");
		assertEquals(1, userInputHandler.getPauseCount(), "Setup should pause once after showing the welcome message");
	}
	
	@Test
	void playGameRunsSinglePlayerGameToCompletion() {
		GameCoordinator gameCoordinator = new GameCoordinator();
		ScriptedUserInputHandler userInputHandler = new ScriptedUserInputHandler(
				gameCoordinator,
				1000,
				List.of("Solo"),
				ScriptedUserInputHandler.select(GameOption.Type.MULTIPLE, 1),
				ScriptedUserInputHandler.endTurn()
		);
		gameCoordinator.setUserInputHandler(userInputHandler);
		gameCoordinator.setPlayerManager(new DeterministicPlayerManager(List.of("Solo"), 1000, Map.of(1, 3)));
		
		gameCoordinator.playGame();
		
		Player player = gameCoordinator.getPlayerManager().getCurrentPlayer();
		assertEquals(1000, player.score().getPermanentScore(), "The scripted player should bank the winning score");
		assertTrue(outputStreamCaptor.toString().contains("You are the only player, YOU WIN!"), "Single-player games should end with the solo win message");
	}
}

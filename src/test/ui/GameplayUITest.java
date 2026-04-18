package ui;


import managers.FakeUserInputHandler;
import managers.GameCoordinator;
import modelManagers.PlayerManager;
import models.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


class GameplayUITest
{
	private final PrintStream standardOut = System.out;
	private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
	
	private GameplayUI gameplayUI;
	private Player trailingPlayer;
	
	@BeforeEach
	void setUp() {
		System.setOut(new PrintStream(outputStreamCaptor));
		
		GameCoordinator gameCoordinator = new GameCoordinator();
		gameCoordinator.setUserInputHandler(new FakeUserInputHandler(gameCoordinator));
		gameCoordinator.setPlayerManager(new PlayerManager(List.of("Alice", "Bob"), 5000));
		
		Player leader = gameCoordinator.getPlayerManager().getCurrentPlayer();
		leader.score().increasePermanentScore(5000);
		
		gameCoordinator.getPlayerManager().switchToNextPlayer();
		trailingPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
		trailingPlayer.score().increasePermanentScore(3200);
		trailingPlayer.score().increaseRoundScore(400);
		
		gameplayUI = gameCoordinator.getGameplayUI();
	}
	
	@AfterEach
	void tearDown() {
		System.setOut(standardOut);
	}
	
	@Test
	void displayHighScoreInfoUsesActualLeaderScore() {
		gameplayUI.displayHighScoreInfo(trailingPlayer);
		
		assertTrue(outputStreamCaptor.toString().contains("Your current score of 3600 is 1400 less than Alice's High Score of 5000."),
				"Last-round messaging should compare against the actual leader's score");
	}
}

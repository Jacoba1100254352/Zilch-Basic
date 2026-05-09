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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class GameplayUITest
{
	private final PrintStream standardOut = System.out;
	private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
	
	private GameplayUI gameplayUI;
	private Player trailingPlayer;
	
	@BeforeEach
	void setUp() {
		outputStreamCaptor.reset();
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
	
	@Test
	void displayHighScoreInfoHandlesTieAndLeaderMessages() {
		GameCoordinator gameCoordinator = new GameCoordinator();
		gameCoordinator.setUserInputHandler(new FakeUserInputHandler(gameCoordinator));
		gameCoordinator.setPlayerManager(new PlayerManager(List.of("Alice", "Bob"), 5000));
		
		Player alice = gameCoordinator.getPlayerManager().getCurrentPlayer();
		alice.score().increasePermanentScore(5000);
		gameCoordinator.getPlayerManager().switchToNextPlayer();
		Player bob = gameCoordinator.getPlayerManager().getCurrentPlayer();
		bob.score().increasePermanentScore(4500);
		bob.score().increaseRoundScore(500);
		
		gameCoordinator.getGameplayUI().displayHighScoreInfo(bob);
		assertTrue(outputStreamCaptor.toString().contains("You are currently tied with the highest scoring player!"),
				"Matching the leader should use the tie message");
		
		outputStreamCaptor.reset();
		bob.score().increaseRoundScore(100);
		gameCoordinator.getGameplayUI().displayHighScoreInfo(bob);
		assertTrue(outputStreamCaptor.toString().contains("You are currently the highest scoring player."),
				"Exceeding the leader should use the leader message");
	}
	
	@Test
	void displayGameOptionsShowsExtraActionsInOrder() {
		GameCoordinator gameCoordinator = new GameCoordinator();
		gameCoordinator.setUserInputHandler(new FakeUserInputHandler(gameCoordinator));
		gameCoordinator.setPlayerManager(new PlayerManager(List.of("TestPlayer"), 5000));
		gameCoordinator.getPlayerManager().getCurrentPlayer().score().setRoundScore(1000);
		gameCoordinator.getGameOptionManager().setOptionSelectedForCurrentRoll(true);
		
		gameCoordinator.getGameplayUI().displayGameOptions(List.of(new models.GameOption(models.GameOption.Type.SINGLE, 5)));
		
		String output = outputStreamCaptor.toString();
		assertTrue(output.contains("1. Score Single 5"), "The scoring option should be listed first");
		assertTrue(output.contains("2. Roll again"), "Roll-again should follow the scoring options");
		assertTrue(output.contains("3. End Turn"), "End-turn should appear last when both extra actions are available");
	}
	
	@Test
	void displayGameOptionsOmitsUnavailableExtraActions() {
		GameCoordinator gameCoordinator = new GameCoordinator();
		gameCoordinator.setUserInputHandler(new FakeUserInputHandler(gameCoordinator));
		gameCoordinator.setPlayerManager(new PlayerManager(List.of("TestPlayer"), 5000));
		
		gameCoordinator.getGameplayUI().displayGameOptions(List.of());
		
		String output = outputStreamCaptor.toString();
		assertFalse(output.contains("Roll again"), "Roll-again should not appear before a scoring option has been selected");
		assertFalse(output.contains("End Turn"), "End-turn should not appear before the player qualifies to bank");
	}
}

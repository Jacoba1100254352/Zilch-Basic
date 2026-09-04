package model.managers;


import model.entities.ComputerDifficulty;
import model.entities.Player;
import model.entities.PlayerConfiguration;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PlayerManagerTest
{
	@Test
	void configuredComputerPlayerRetainsItsDifficulty() {
		PlayerManager playerManager = PlayerManager.fromConfigurations(List.of(
				PlayerConfiguration.human("Alice"),
				PlayerConfiguration.computer("Computer", ComputerDifficulty.HARD)
		));

		assertFalse(playerManager.getPlayers().get(0).isComputer());
		assertTrue(playerManager.getPlayers().get(1).isComputer());
		assertEquals(ComputerDifficulty.HARD, playerManager.getPlayers().get(1).difficulty());
	}

	@Test
	void initializesCurrentPlayerToFirstPlayer() {
		PlayerManager playerManager = new PlayerManager(List.of("Alice", "Bob", "Charlie"));

		assertEquals("Alice", playerManager.getCurrentPlayer().name());
		assertEquals(3, playerManager.getPlayers().size());
	}

	@Test
	void initializesCurrentPlayerToNullWhenNoPlayersExist() {
		PlayerManager playerManager = new PlayerManager(List.of());

		assertNull(playerManager.getCurrentPlayer());
		assertEquals(List.of(), playerManager.getPlayers());
	}

	@Test
	void switchToNextPlayerCyclesThroughPlayers() {
		PlayerManager playerManager = new PlayerManager(List.of("Alice", "Bob", "Charlie"));

		playerManager.switchToNextPlayer();
		assertEquals("Bob", playerManager.getCurrentPlayer().name());

		playerManager.switchToNextPlayer();
		assertEquals("Charlie", playerManager.getCurrentPlayer().name());

		playerManager.switchToNextPlayer();
		assertEquals("Alice", playerManager.getCurrentPlayer().name());
	}

	@Test
	void findHighestScoringPlayerReturnsPlayerWithLargestPermanentScore() {
		PlayerManager playerManager = new PlayerManager(List.of("Alice", "Bob", "Charlie"));
		playerManager.getPlayers().get(0).score().increasePermanentScore(2000);
		playerManager.getPlayers().get(1).score().increasePermanentScore(3500);
		playerManager.getPlayers().get(2).score().increasePermanentScore(3000);

		assertSame(playerManager.getPlayers().get(1), playerManager.findHighestScoringPlayer());
	}

	@Test
	void inheritedAccessorsAllowChangingTheCurrentPlayer() {
		PlayerManager playerManager = new PlayerManager(List.of("Alice", "Bob"));
		Player bob = playerManager.getPlayers().get(1);

		playerManager.setCurrentPlayer(bob);

		assertSame(bob, playerManager.getCurrentPlayer());
	}
}

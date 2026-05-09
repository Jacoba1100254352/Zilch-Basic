package modelManagers;


import models.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PlayerManagerTest
{
	private PlayerManager playerManager;
	private Player currentPlayer;
	
	@BeforeEach
	void setUp() {
		playerManager = new PlayerManager(List.of("Alice", "Bob"), 5000);
		currentPlayer = playerManager.getCurrentPlayer();
	}
	
	@Test
	void switchToNextPlayerWrapsAroundRoster() {
		playerManager.switchToNextPlayer();
		assertEquals("Bob", playerManager.getCurrentPlayer().name(), "The second player should play next");
		
		playerManager.switchToNextPlayer();
		assertEquals("Alice", playerManager.getCurrentPlayer().name(), "Switching again should wrap back to the first player");
	}
	
	@Test
	void findHighestScoringPlayerReturnsLeader() {
		currentPlayer.score().increasePermanentScore(1200);
		playerManager.switchToNextPlayer();
		playerManager.getCurrentPlayer().score().increasePermanentScore(1800);
		
		assertEquals("Bob", playerManager.findHighestScoringPlayer().name(), "The player with the largest permanent score should be returned");
	}
	
	@Test
	void replenishAllDiceRestoresFullPool() {
		currentPlayer.dice().setNumDiceInPlay(2);
		currentPlayer.dice().setDiceSetMap(Map.of(1, 1, 5, 1));
		
		playerManager.replenishAllDice();
		
		assertEquals(6, currentPlayer.dice().getNumDiceInPlay(), "Replenishing should restore all six dice");
		assertTrue(currentPlayer.dice().getDiceSetMap().isEmpty(), "Replenishing should clear the previous roll contents");
	}
	
	@Test
	void removeDiceDropsCountAndRemovesZeros() {
		currentPlayer.dice().setDiceSetMap(Map.of(1, 2, 5, 1));
		currentPlayer.dice().calculateNumDiceInPlay();
		
		playerManager.removeDice(1, 1);
		assertEquals(Map.of(1, 1, 5, 1), currentPlayer.dice().getDiceSetMap(), "Removing one die should decrement only that die value");
		assertEquals(2, currentPlayer.dice().getNumDiceInPlay(), "The in-play count should be recalculated after removing a die");
		
		playerManager.removeDice(1, 1);
		assertEquals(Map.of(5, 1), currentPlayer.dice().getDiceSetMap(), "Die values with zero count should be removed from the map");
		assertEquals(1, currentPlayer.dice().getNumDiceInPlay(), "Removing the last matching die should reduce the count again");
	}
	
	@Test
	void eliminateDiceRemovesEntireValueGroup() {
		currentPlayer.dice().setDiceSetMap(Map.of(3, 3, 5, 2));
		currentPlayer.dice().calculateNumDiceInPlay();
		
		playerManager.eliminateDice(3);
		
		assertEquals(Map.of(5, 2), currentPlayer.dice().getDiceSetMap(), "Eliminating a die value should remove the entire group");
		assertEquals(2, currentPlayer.dice().getNumDiceInPlay(), "Eliminating a group should recalculate the remaining dice");
	}
	
	@Test
	void removeAllDiceClearsRollState() {
		currentPlayer.dice().setDiceSetMap(Map.of(2, 2, 4, 1));
		currentPlayer.dice().calculateNumDiceInPlay();
		
		playerManager.removeAllDice();
		
		assertTrue(currentPlayer.dice().getDiceSetMap().isEmpty(), "Removing all dice should clear the roll");
		assertEquals(0, currentPlayer.dice().getNumDiceInPlay(), "Removing all dice should leave zero dice in play");
	}
	
	@Test
	void rollDicePopulatesExactlyTheDiceInPlay() {
		currentPlayer.dice().setNumDiceInPlay(4);
		
		playerManager.rollDice();
		
		int rolledDice = currentPlayer.dice().getDiceSetMap().values().stream().mapToInt(Integer::intValue).sum();
		assertEquals(4, rolledDice, "Rolling should populate exactly the number of dice currently in play");
		assertTrue(currentPlayer.dice().getDiceSetMap().keySet().stream().allMatch(value -> value >= 1 && value <= 6),
				"Every rolled die value should stay within the standard d6 range");
	}
}

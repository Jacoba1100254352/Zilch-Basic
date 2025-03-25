package ruleManagers;


import managers.GameCoordinator;
import modelManagers.PlayerManager;
import models.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class RuleManagerTest
{
	
	private RuleManager ruleManager;
	private Player currentPlayer;
	private Map<Integer, Integer> diceSetMap;
	
	@BeforeEach
	void setUp() {
		final int scoreLimit = 5000;
		GameCoordinator gameCoordinator = new GameCoordinator(); // Assuming this can be instantiated
		ruleManager = new RuleManager(gameCoordinator);
		List<String> playerNames = List.of("TestPlayer");
		
		// Initialize player manager with players
		gameCoordinator.setPlayerManager(new PlayerManager(playerNames, scoreLimit));
		currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
		
		diceSetMap = gameCoordinator.getPlayerManager().getCurrentPlayer().dice().getDiceSetMap();
	}
	
	@AfterEach
	void tearDown() {
		// Clean up after each test if necessary
	}
	
	@Test
	@DisplayName("Positive: Strait")
	void isStraitPass() {
		setCurrentPlayerDiceMap(1, 2, 3, 4, 5, 6);
		assertTrue(ruleManager.isRuleValid(new StraitRule(), diceSetMap), "isRuleValid should return true for a valid strait");
	}
	
	@Test
	@DisplayName("Negative: Strait")
	void isStraitFail() {
		setCurrentPlayerDiceMap(1, 2, 2, 3, 4, 5);
		assertFalse(ruleManager.isRuleValid(new StraitRule(), diceSetMap), "isRuleValid should return false when no strait exists");
	}
	
	@Test
	@DisplayName("Positive: Set")
	void isSetPass() {
		setCurrentPlayerDiceMap(2, 2, 3, 3, 5, 5);
		assertTrue(ruleManager.isRuleValid(new SetRule(), diceSetMap), "isRuleValid should return true for a valid set");
	}
	
	@Test
	@DisplayName("Negative: Set")
	void isSetFail() {
		setCurrentPlayerDiceMap(2, 2, 3, 4, 5, 6);
		assertFalse(ruleManager.isRuleValid(new SetRule(), diceSetMap), "isRuleValid should return false when no set exists");
	}
	
	@Test
	@DisplayName("Positive: Single")
	void isSinglePass() {
		setCurrentPlayerDiceMap(1, 2, 3, 4, 5, 6);
		assertTrue(ruleManager.isRuleValid(new SingleRule(1), diceSetMap), "isRuleValid should return true for a valid single");
	}
	
	@Test
	@DisplayName("Negative: Single")
	void isSingleFail() {
		setCurrentPlayerDiceMap(2, 2, 3, 4, 5, 6);
		assertFalse(ruleManager.isRuleValid(new SingleRule(1), diceSetMap), "isRuleValid should return false when no single exists");
	}
	
	@Test
	@DisplayName("Positive: Multiple")
	void isMultiplePass() {
		setCurrentPlayerDiceMap(2, 2, 2, 3, 4, 5);
		assertTrue(ruleManager.isRuleValid(new MultipleRule(), diceSetMap), "isRuleValid should return true for a valid multiple");
	}
	
	@Test
	@DisplayName("Negative: Multiple")
	void isMultipleFail() {
		setCurrentPlayerDiceMap(2, 2, 3, 4, 5, 6);
		assertFalse(ruleManager.isRuleValid(new MultipleRule(), diceSetMap), "isRuleValid should return false when no multiple exists");
	}
	
	@Test
	@DisplayName("Positive: Add Multiple")
	void canAddMultiplesPass() {
		setCurrentPlayerDiceMap(2, 4, 5);
		assertTrue(ruleManager.isRuleValid(new AddMultipleRule(2, 2), diceSetMap), "isRuleValid should return true when multiples can be added");
	}
	
	@Test
	@DisplayName("Negative: Add Multiple")
	void canAddMultiplesFail() {
		setCurrentPlayerDiceMap(3, 4, 5, 6);
		assertFalse(ruleManager.isRuleValid(new AddMultipleRule(2, 2), diceSetMap), "isRuleValid should return false when multiples cannot be added");
	}
	
	private void setCurrentPlayerDiceMap(Integer... values) {
		Map<Integer, Integer> diceMap = new HashMap<>();
		for (int value : values) {
			diceMap.merge(value, 1, Integer::sum);
		}
		
		currentPlayer.dice().setDiceSetMap(diceMap);
	}
}
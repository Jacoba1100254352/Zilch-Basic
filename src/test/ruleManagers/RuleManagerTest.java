package ruleManagers;


import dispatchers.SimpleEventDispatcher;
import interfaces.IDiceManager;
import interfaces.IScoreManager;
import managers.GameCoordinator;
import modelManagers.DiceManager;
import modelManagers.PlayerManager;
import modelManagers.ScoreManager;
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
	
	@Test
	void name() {
	}
	
	@BeforeEach
	void setUp() {
		final int scoreLimit = 5000;
		// TODO: We will probably need to fix
		SimpleEventDispatcher eventDispatcher = new SimpleEventDispatcher();
		//eventDispatcher.addListener(GameEventTypes.SCORE_UPDATED, new ScoreUpdateListener());
		//eventDispatcher.addListener(GameEventTypes.GAME_STATE_CHANGED, new GameStateChangeListener());
		
		GameCoordinator gameCoordinator = new GameCoordinator(eventDispatcher);
		ruleManager = new RuleManager();
		List<String> playerNames = List.of("TestPlayer");
		
		// Initialize player manager with players
		IDiceManager diceManager = new DiceManager();
		IScoreManager scoreManager = new ScoreManager(scoreLimit);
		
		gameCoordinator.setPlayerManager(new PlayerManager(playerNames, diceManager, scoreManager));
		currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
		
		diceSetMap = gameCoordinator.getPlayerManager().getCurrentPlayer().dice().diceSetMap();
	}
	
	@AfterEach
	void tearDown() {
		// Clean up after each test if necessary
	}
	
	@Test
	@DisplayName("Positive: Strait")
	void isStraitPass() {
		setCurrentPlayerDiceMap(1, 2, 3, 4, 5, 6);
		assertTrue(ruleManager.isRuleValid(RuleType.STRAIT, diceSetMap, null), "isRuleValid should return true for a valid strait");
	}
	
	@Test
	@DisplayName("Negative: Strait")
	void isStraitFail() {
		setCurrentPlayerDiceMap(1, 2, 2, 3, 4, 5);
		assertFalse(ruleManager.isRuleValid(RuleType.STRAIT, diceSetMap, null), "isRuleValid should return false when no strait exists");
	}
	
	@Test
	@DisplayName("Positive: Set")
	void isSetPass() {
		setCurrentPlayerDiceMap(2, 2, 3, 3, 5, 5);
		assertTrue(ruleManager.isRuleValid(RuleType.SET, diceSetMap, null), "isRuleValid should return true for a valid set");
	}
	
	@Test
	@DisplayName("Negative: Set")
	void isSetFail() {
		setCurrentPlayerDiceMap(2, 2, 3, 4, 5, 6);
		assertFalse(ruleManager.isRuleValid(RuleType.SET, diceSetMap, null), "isRuleValid should return false when no set exists");
	}
	
	@Test
	@DisplayName("Positive: Single")
	void isSinglePass() {
		setCurrentPlayerDiceMap(1, 2, 3, 4, 5, 6);
		assertTrue(ruleManager.isRuleValid(RuleType.SINGLE, diceSetMap, 1), "isRuleValid should return true for a valid single");
	}
	
	@Test
	@DisplayName("Negative: Single")
	void isSingleFail() {
		setCurrentPlayerDiceMap(2, 2, 3, 4, 5, 6);
		assertFalse(ruleManager.isRuleValid(RuleType.SINGLE, diceSetMap, 1), "isRuleValid should return false when no single exists");
	}
	
	@Test
	@DisplayName("Positive: Multiple")
	void isMultiplePass() {
		setCurrentPlayerDiceMap(2, 2, 2, 3, 4, 5);
		assertTrue(ruleManager.isRuleValid(RuleType.MULTIPLE, diceSetMap, null), "isRuleValid should return true for a valid multiple");
	}
	
	@Test
	@DisplayName("Negative: Multiple")
	void isMultipleFail() {
		setCurrentPlayerDiceMap(2, 2, 3, 4, 5, 6);
		assertFalse(ruleManager.isRuleValid(RuleType.MULTIPLE, diceSetMap, null), "isRuleValid should return false when no multiple exists");
	}
	
	@Test
	@DisplayName("Positive: Add Multiple")
	void canAddMultiplesPass() {
		setCurrentPlayerDiceMap(2, 2, 2, 3, 4, 5);
		assertTrue(ruleManager.isRuleValid(RuleType.ADD_MULTIPLE, diceSetMap, 2), "isRuleValid should return true when multiples can be added");
	}
	
	@Test
	@DisplayName("Negative: Add Multiple")
	void canAddMultiplesFail() {
		setCurrentPlayerDiceMap(2, 2, 3, 4, 5, 6);
		assertFalse(ruleManager.isRuleValid(RuleType.ADD_MULTIPLE, diceSetMap, 2), "isRuleValid should return false when multiples cannot be added");
	}
	
	private void setCurrentPlayerDiceMap(Integer... values) {
		Map<Integer, Integer> diceMap = new HashMap<>();
		for (int value : values) {
			diceMap.merge(value, 1, Integer::sum);
		}
		
		currentPlayer.dice().setDiceSetMap(diceMap);
	}
}
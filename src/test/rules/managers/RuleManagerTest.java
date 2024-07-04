package rules.managers;


import model.entities.Dice;
import model.entities.GameOption;
import model.entities.Player;
import model.entities.Score;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rules.config.RulesConfig;
import rules.config.RulesConfigBuilder;
import rules.models.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class RuleManagerTest
{
	
	@Mock
	private IRuleRegistry ruleRegistry;
	private RuleManager ruleManager;
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ruleManager = new RuleManager(ruleRegistry, "TestGameID");
	}
	
	@AfterEach
	void tearDown() {
		// Cleanup resources if necessary
	}
	
	@Test
	void initializeRules() {
		// Assume we're initializing with certain parameters
		Set<Integer> singleValues = Set.of(1, 5);
		RulesConfig config = new RulesConfigBuilder("TestGameID")
				.setAddMultipleMin(3)
				.setMultipleMin(3)
				.setSingleValues(singleValues)
				.setSetMin(3)
				.setNumStraitValues(6)
				.build();
		ruleManager.initializeRules("TestGameID", 3, 3, singleValues, 3, 6);
		
		// Verify that the rule registry is called with the correct parameters
		verify(ruleRegistry).initializeRulesForGame("TestGameID", 3, 3, singleValues, 3, 6);
	}
	
	@Test
	void evaluateRules_AllRulesValid() {
		// Setup player and dice
		Dice dice = new Dice(Map.of(1, 4, 2, 2, 3, 2)); // Dice counts that should trigger multiple rules
		final int permanentScore = 1000;
		final int roundScore = 300;
		Score score = new Score(roundScore, permanentScore, 1100); // Setup for roll again and end turn
		Player player = new Player("Jacob", dice, score);
		
		// Mock rules to simulate rule checks
		IRuleStrategy addMultipleRule = mock(AddMultipleRule.class);
		IRuleStrategy straitRule = mock(StraitRule.class);
		IRuleStrategy setRule = mock(SetRule.class);
		IRuleStrategy multipleRule = mock(MultipleRule.class);
		IRuleStrategy singleRule = mock(SingleRule.class);
		when(ruleRegistry.getRule("TestGameID", RuleType.ADD_MULTIPLE)).thenReturn(addMultipleRule);
		when(ruleRegistry.getRule("TestGameID", RuleType.STRAIT)).thenReturn(straitRule);
		when(ruleRegistry.getRule("TestGameID", RuleType.SET)).thenReturn(setRule);
		when(ruleRegistry.getRule("TestGameID", RuleType.MULTIPLE)).thenReturn(multipleRule);
		when(ruleRegistry.getRule("TestGameID", RuleType.SINGLE)).thenReturn(singleRule);
		
		when(addMultipleRule.isValid(any(), any())).thenReturn(true);
		when(straitRule.isValid(any(), any())).thenReturn(true);
		when(setRule.isValid(any(), any())).thenReturn(true);
		when(multipleRule.isValid(any(), any())).thenReturn(true);
		when(singleRule.isValid(any(), any())).thenReturn(true);
		
		// Execution setup
		int expected = 0;
		final boolean isOptionSelected = true;
		
		if (isOptionSelected) expected++; // ROLL_AGAIN
		// expected++; // ADD_MULTIPLE
		expected += 6; // 6 MULTIPLE
		expected += 6; // 6 SINGLE
		expected += 2; // STRAIT, SET
		if (permanentScore >= 1000 || roundScore >= 1000) expected++; // END_TURN
		
		// Execute and verify
		List<GameOption> gameOptions = ruleManager.evaluateRules(player, isOptionSelected);
		assertEquals(expected, gameOptions.size(), "Incorrect number of options displayed, " + gameOptions);
		if (isOptionSelected) {
			// assertTrue(gameOptions.contains(new GameOption(RuleType.ADD_MULTIPLE, 1, "")));
			assertTrue(gameOptions.contains(new GameOption(RuleType.ROLL_AGAIN, null)));
		} else {
			assertFalse(gameOptions.contains(new GameOption(RuleType.ADD_MULTIPLE, 1)));
			assertFalse(gameOptions.contains(new GameOption(RuleType.ROLL_AGAIN, null)));
		}
		assertTrue(gameOptions.contains(new GameOption(RuleType.STRAIT, null)));
		assertTrue(gameOptions.contains(new GameOption(RuleType.SET, null)));
		assertTrue(gameOptions.contains(new GameOption(RuleType.MULTIPLE, 1)));
		assertTrue(gameOptions.contains(new GameOption(RuleType.SINGLE, 2)));
		
		// Verify that the end turn option is present
		if (permanentScore >= 1000 || roundScore >= 1000) {
			assertTrue(gameOptions.contains(new GameOption(RuleType.END_TURN, null)));
		} else {
			assertFalse(gameOptions.contains(new GameOption(RuleType.END_TURN, null)));
		}
	}
	
	@Test
	void evaluateRules_NoRulesValid() {
		// Setup player and dice
		Dice dice = new Dice(Map.of(1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 1)); // Unlikely to trigger multiples or sets
		Score score = new Score(0, 0, 0); // No score
		Player player = new Player("Jacob", dice, score);
		
		// Mock rules to simulate rule checks
		IRuleStrategy addMultipleRule = mock(AddMultipleRule.class);
		IRuleStrategy multipleRule = mock(SingleRule.class);
		IRuleStrategy singleRule = mock(StraitRule.class);
		IRuleStrategy straitRule = mock(MultipleRule.class);
		IRuleStrategy setRule = mock(SetRule.class);
		when(ruleRegistry.getRule("TestGameID", RuleType.ADD_MULTIPLE)).thenReturn(addMultipleRule);
		when(ruleRegistry.getRule("TestGameID", RuleType.MULTIPLE)).thenReturn(multipleRule);
		when(ruleRegistry.getRule("TestGameID", RuleType.SINGLE)).thenReturn(singleRule);
		when(ruleRegistry.getRule("TestGameID", RuleType.STRAIT)).thenReturn(straitRule);
		when(ruleRegistry.getRule("TestGameID", RuleType.SET)).thenReturn(setRule);
		
		when(addMultipleRule.isValid(any(), any())).thenReturn(false);
		when(multipleRule.isValid(any(), any())).thenReturn(false);
		when(singleRule.isValid(any(), any())).thenReturn(false);
		when(straitRule.isValid(any(), any())).thenReturn(false);
		when(setRule.isValid(any(), any())).thenReturn(false);
		
		// Execute and verify
		List<GameOption> gameOptions = ruleManager.evaluateRules(player, false);
		assertTrue(gameOptions.isEmpty()); // No valid options should be present
	}
}

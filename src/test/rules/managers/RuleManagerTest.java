package rules.managers;


import model.entities.Dice;
import model.entities.GameOption;
import model.entities.Player;
import model.entities.Score;
import org.junit.jupiter.api.Test;
import rules.context.RuleContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class RuleManagerTest
{
	@Test
	void evaluateRulesOnlyUsesConfiguredRules() {
		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		ruleManager.initializeRules(Map.of(
				RuleType.SINGLE, Set.of(1, 5),
				RuleType.MULTIPLE, 3
		));

		Player player = new Player(
				"Jacob",
				new Dice(new HashMap<>(Map.of(1, 3, 5, 1, 2, 2))),
				new Score()
		);

		List<GameOption> gameOptions = ruleManager.evaluateRules(
				new RuleContext(player, player.dice().getDiceSetMap(), new HashMap<>())
		);

		assertTrue(gameOptions.stream().anyMatch(option -> option.type().equals(RuleType.SINGLE)));
		assertTrue(gameOptions.stream().anyMatch(option -> option.type().equals(RuleType.MULTIPLE)));
		assertTrue(gameOptions.stream().noneMatch(option -> option.type().equals(RuleType.SET)));
		assertTrue(gameOptions.stream().noneMatch(option -> option.type().equals(RuleType.STRAIT)));
	}

	@Test
	void applyRuleUpdatesScoreAndConsumesDice() {
		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		ruleManager.initializeRules(Map.of(RuleType.SINGLE, Set.of(1, 5)));

		Player player = new Player(
				"Jacob",
				new Dice(new HashMap<>(Map.of(1, 1, 2, 2, 3, 3))),
				new Score()
		);
		RuleContext context = new RuleContext(player, player.dice().getDiceSetMap(), new HashMap<>());

		GameOption option = ruleManager.evaluateRules(context).stream()
		                             .filter(gameOption -> gameOption.selectedValue() != null && gameOption.selectedValue() == 1)
		                             .findFirst()
		                             .orElseThrow();

		ruleManager.applyRule(context, option);

		assertEquals(100, player.score().getRoundScore());
		assertEquals(0, player.dice().getDiceSetMap().getOrDefault(1, 0));
		assertEquals(5, player.dice().getNumDiceInPlay());
	}

	@Test
	void applyRuleThrowsWhenNoMatchingRuleIsRegistered() {
		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		Player player = new Player("Jacob", new Dice(new HashMap<>()), new Score());
		RuleContext context = new RuleContext(player, player.dice().getDiceSetMap(), new HashMap<>());
		GameOption option = new GameOption(new RuleType("missing_rule"), "Missing", "desc", null, 0, Map.of());

		assertThrows(IllegalArgumentException.class, () -> ruleManager.applyRule(context, option));
	}

	@Test
	void getAvailableRulesExposesDiscoveredRules() {
		RuleManager ruleManager = new RuleManager(new RuleRegistry());

		assertTrue(ruleManager.getAvailableRules().stream().anyMatch(rule -> rule.getRuleType().equals(RuleType.SINGLE)));
		assertTrue(ruleManager.getAvailableRules().stream().anyMatch(rule -> rule.getRuleType().equals(new RuleType("test_auto_loaded"))));
	}

	@Test
	void isRuleActiveReflectsConfiguredRules() {
		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		ruleManager.initializeRules(Map.of(RuleType.FIRST_ROLL_BUST, 50));

		assertTrue(ruleManager.isRuleActive(RuleType.FIRST_ROLL_BUST));
		assertTrue(ruleManager.evaluateRules(new RuleContext(
				new Player("Jacob", new Dice(new HashMap<>()), new Score()),
				new HashMap<>(),
				new HashMap<>()
		)).isEmpty());
	}
}

package model.managers;


import model.entities.Dice;
import model.entities.GameOption;
import model.entities.Player;
import model.entities.Score;
import org.junit.jupiter.api.Test;
import rules.context.RuleContext;
import rules.managers.IRuleManager;
import rules.managers.RuleType;
import rules.variable.IRule;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class GameOptionManagerTest
{
	@Test
	void evaluateGameOptionsReplacesExistingOptions() {
		StubRuleManager ruleManager = new StubRuleManager();
		GameOptionManager gameOptionManager = new GameOptionManager(ruleManager);
		RuleContext context = testRuleContext();

		GameOption first = new GameOption(RuleType.SINGLE, "Single", "desc", 1, 100, Map.of(1, 1));
		GameOption second = new GameOption(RuleType.SET, "Set", "desc", null, 1000, Map.of(1, 2, 2, 2, 5, 2));

		ruleManager.queueEvaluation(List.of(first));
		ruleManager.queueEvaluation(List.of(second));

		gameOptionManager.evaluateGameOptions(context);
		assertEquals(List.of(first), gameOptionManager.getGameOptions());

		gameOptionManager.evaluateGameOptions(context);
		assertEquals(List.of(second), gameOptionManager.getGameOptions());
	}

	@Test
	void applyGameOptionUsesProvidedOptionOrSelectedFallback() {
		StubRuleManager ruleManager = new StubRuleManager();
		GameOptionManager gameOptionManager = new GameOptionManager(ruleManager);
		RuleContext context = testRuleContext();
		GameOption option = new GameOption(RuleType.SINGLE, "Single", "desc", 1, 100, Map.of(1, 1));

		gameOptionManager.applyGameOption(context, option);
		assertSame(context, ruleManager.lastApplyContext);
		assertSame(option, ruleManager.lastAppliedOption);

		gameOptionManager.setSelectedGameOption(option);
		gameOptionManager.applyGameOption(context, null);
		assertSame(context, ruleManager.lastApplyContext);
		assertSame(option, ruleManager.lastAppliedOption);
	}

	@Test
	void applyGameOptionWithoutSelectionThrows() {
		GameOptionManager gameOptionManager = new GameOptionManager(new StubRuleManager());

		assertThrows(IllegalStateException.class, () -> gameOptionManager.applyGameOption(testRuleContext(), null));
	}

	@Test
	void getGameOptionsReturnsDefensiveCopy() {
		StubRuleManager ruleManager = new StubRuleManager();
		GameOptionManager gameOptionManager = new GameOptionManager(ruleManager);
		RuleContext context = testRuleContext();
		GameOption option = new GameOption(RuleType.SINGLE, "Single", "desc", 1, 100, Map.of(1, 1));
		ruleManager.queueEvaluation(List.of(option));

		gameOptionManager.evaluateGameOptions(context);
		List<GameOption> returnedOptions = gameOptionManager.getGameOptions();
		returnedOptions.clear();

		assertEquals(1, gameOptionManager.getGameOptions().size());
		assertNotSame(returnedOptions, gameOptionManager.getGameOptions());
		assertNull(gameOptionManager.getSelectedGameOption());
	}

	@Test
	void isValidReflectsWhetherAnOptionHasBeenSelected() {
		GameOptionManager gameOptionManager = new GameOptionManager(new StubRuleManager());

		assertFalse(gameOptionManager.isValid());

		GameOption option = new GameOption(RuleType.SINGLE, "Single", "desc", 1, 100, Map.of(1, 1));
		gameOptionManager.setSelectedGameOption(option);

		assertTrue(gameOptionManager.isValid());
		assertSame(option, gameOptionManager.getSelectedGameOption());
	}

	private RuleContext testRuleContext() {
		Player player = new Player("Test", new Dice(new HashMap<>(Map.of(1, 1))), new Score());
		return new RuleContext(player, player.dice().getDiceSetMap(), new HashMap<>());
	}

	private static final class StubRuleManager implements IRuleManager
	{
		private final ArrayDeque<List<GameOption>> queuedEvaluations = new ArrayDeque<>();
		private RuleContext lastApplyContext;
		private GameOption lastAppliedOption;

		void queueEvaluation(List<GameOption> gameOptions) {
			queuedEvaluations.addLast(gameOptions);
		}

		@Override
		public void initializeRules(Map<RuleType, Object> config) {
		}

		@Override
		public List<GameOption> evaluateRules(RuleContext context) {
			return queuedEvaluations.removeFirst();
		}

		@Override
		public IRule getRule(RuleType ruleType) {
			return null;
		}

		@Override
		public void applyRule(RuleContext context, GameOption option) {
			lastApplyContext = context;
			lastAppliedOption = option;
		}

		@Override
		public List<IRule> getAvailableRules() {
			return List.of();
		}
	}
}

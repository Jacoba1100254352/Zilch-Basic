package rules.variable;


import model.entities.Dice;
import model.entities.GameOption;
import model.entities.Player;
import model.entities.Score;
import org.junit.jupiter.api.Test;
import rules.context.RuleContext;
import rules.managers.RuleType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class VariableRuleTest
{
	@Test
	void singleRuleReturnsOptionsForSingleOnesAndFives() {
		SingleRule rule = new SingleRule();
		rule.configure(Set.of(1, 5));
		RuleContext context = contextFor(Map.of(1, 2, 5, 1, 2, 3));

		List<GameOption> options = rule.evaluate(context);

		assertEquals(2, options.size());
		assertTrue(options.stream().anyMatch(option -> option.selectedValue() == 1 && option.pointsAwarded() == 100));
		assertTrue(options.stream().anyMatch(option -> option.selectedValue() == 5 && option.pointsAwarded() == 50));
	}

	@Test
	void singleRuleReturnsNoOptionsWhenNoAcceptedDiceArePresent() {
		SingleRule rule = new SingleRule();
		rule.configure(Set.of(1, 5));

		assertEquals(List.of(), rule.evaluate(contextFor(Map.of(2, 2, 3, 2, 4, 2))));
	}

	@Test
	void setRuleReturnsOptionForThreePairs() {
		SetRule rule = new SetRule();
		rule.configure(3);
		RuleContext context = contextFor(Map.of(1, 2, 2, 2, 5, 2));

		List<GameOption> options = rule.evaluate(context);

		assertEquals(1, options.size());
		assertEquals(RuleType.SET, options.get(0).type());
		assertEquals(1000, options.get(0).pointsAwarded());
	}

	@Test
	void setRuleRejectsRollsThatAreNotPairs() {
		SetRule rule = new SetRule();
		rule.configure(3);

		assertEquals(List.of(), rule.evaluate(contextFor(Map.of(1, 3, 2, 2, 5, 1))));
	}

	@Test
	void straitRuleReturnsOptionForFullStraight() {
		StraitRule rule = new StraitRule();
		rule.configure(Dice.FULL_SET_OF_DICE);
		RuleContext context = contextFor(Map.of(1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 1));

		List<GameOption> options = rule.evaluate(context);

		assertEquals(1, options.size());
		assertEquals(RuleType.STRAIT, options.get(0).type());
		assertEquals(1000, options.get(0).pointsAwarded());
		assertEquals(6, options.get(0).consumedDice().size());
	}

	@Test
	void straitRuleSupportsShorterConfiguredStraights() {
		StraitRule rule = new StraitRule();
		rule.configure(4);

		List<GameOption> options = rule.evaluate(contextFor(Map.of(1, 1, 2, 1, 3, 1, 4, 1, 6, 1)));

		assertEquals(1, options.size());
		assertEquals(Map.of(1, 1, 2, 1, 3, 1, 4, 1), options.get(0).consumedDice());
	}

	@Test
	void multipleRuleAppliesScoreAndTracksScoredMultiples() {
		MultipleRule rule = new MultipleRule();
		rule.configure(3);
		Player player = new Player("Jacob", new Dice(new HashMap<>(Map.of(3, 4))), new Score());
		RuleContext context = new RuleContext(player, player.dice().getDiceSetMap(), new HashMap<>());

		GameOption option = rule.evaluate(context).get(0);
		rule.apply(context, option);

		assertEquals(600, player.score().getRoundScore());
		assertTrue(player.dice().getDiceSetMap().isEmpty());
		assertEquals(4, context.scoredMultiples().get(3));
	}

	@Test
	void multipleRuleSkipsValuesAlreadyScoredAsMultiplesThisTurn() {
		MultipleRule rule = new MultipleRule();
		rule.configure(3);
		Player player = new Player("Jacob", new Dice(new HashMap<>(Map.of(3, 3))), new Score());
		RuleContext context = new RuleContext(player, player.dice().getDiceSetMap(), new HashMap<>(Map.of(3, 3)));

		assertEquals(List.of(), rule.evaluate(context));
	}

	@Test
	void addMultipleRuleScoresOnlyIncrementalValue() {
		AddMultipleRule rule = new AddMultipleRule();
		rule.configure(1);
		Player player = new Player("Jacob", new Dice(new HashMap<>(Map.of(3, 1))), new Score());
		Map<Integer, Integer> scoredMultiples = new HashMap<>(Map.of(3, 3));
		RuleContext context = new RuleContext(player, player.dice().getDiceSetMap(), scoredMultiples);

		GameOption option = rule.evaluate(context).get(0);
		rule.apply(context, option);

		assertEquals(300, player.score().getRoundScore());
		assertTrue(player.dice().getDiceSetMap().isEmpty());
		assertEquals(4, context.scoredMultiples().get(3));
	}

	@Test
	void addMultipleRuleRequiresAnExistingScoredMultiple() {
		AddMultipleRule rule = new AddMultipleRule();
		rule.configure(1);
		Player player = new Player("Jacob", new Dice(new HashMap<>(Map.of(3, 1))), new Score());
		RuleContext context = new RuleContext(player, player.dice().getDiceSetMap(), new HashMap<>());

		assertEquals(List.of(), rule.evaluate(context));
	}

	private RuleContext contextFor(Map<Integer, Integer> diceSetMap) {
		Player player = new Player("Jacob", new Dice(new HashMap<>(diceSetMap)), new Score());
		return new RuleContext(player, player.dice().getDiceSetMap(), new HashMap<>());
	}
}

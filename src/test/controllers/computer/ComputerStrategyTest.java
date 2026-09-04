package controllers.computer;


import model.entities.ComputerDifficulty;
import model.entities.Dice;
import model.entities.GameOption;
import model.entities.Player;
import model.entities.PlayerType;
import model.entities.Score;
import model.entities.TurnContinuation;
import model.managers.ActionManager;
import org.junit.jupiter.api.Test;
import rules.managers.RuleType;
import support.TestDoubles.SequencedDiceManager;
import support.TestDoubles.StubPlayerManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ComputerStrategyTest
{
	@Test
	void easyScoresEveryOptionAndBanksAtSixHundredOrToWin() {
		Player computer = computer("Computer", ComputerDifficulty.EASY, 0, 550, 3);
		ComputerStrategy strategy = strategy(computer, human("Alice", 0), 5000, true, true, false);
		List<GameOption> remaining = List.of(option(RuleType.SINGLE, 5, 50, Map.of(5, 1)));

		assertTrue(strategy.shouldScoreMore(computer, remaining));
		assertTrue(strategy.shouldRollAgain(computer, true));

		computer.score().setRoundScore(600);
		assertFalse(strategy.shouldRollAgain(computer, true));

		Player nearWin = computer("Computer", ComputerDifficulty.EASY, 4900, 100, 5);
		ComputerStrategy winStrategy = strategy(nearWin, human("Alice", 3000), 5000, true, true, false);
		assertFalse(winStrategy.shouldRollAgain(nearWin, true));
	}

	@Test
	void mediumStagesBelowTheTargetOnlyWhenTheOpponentMakesItWorthwhile() {
		Player computer = computer("Computer", ComputerDifficulty.MEDIUM, 4850, 100, 4);
		Player distantOpponent = human("Alice", 4000);
		ComputerStrategy safeStage = strategy(computer, distantOpponent, 5000, true, true, false);

		assertFalse(safeStage.shouldRollAgain(computer, true));

		Player closeOpponent = human("Bob", 4700);
		ComputerStrategy unsafeStage = strategy(computer, closeOpponent, 5000, true, true, false);
		assertTrue(unsafeStage.shouldRollAgain(computer, true));
	}

	@Test
	void mediumPressesForABufferWithSafeDiceButProtectsARiskierLead() {
		Player computer = computer("Computer", ComputerDifficulty.MEDIUM, 4800, 300, 4);
		Player opponent = human("Alice", 4700);
		ComputerStrategy strategy = strategy(computer, opponent, 5000, true, true, false);

		assertTrue(strategy.shouldRollAgain(computer, true));

		computer.dice().setNumDiceInPlay(3);
		assertFalse(strategy.shouldRollAgain(computer, true));
	}

	@Test
	void finalChaseBanksATieOnlyWhenTheRulesAllowIt() {
		Player computer = computer("Computer", ComputerDifficulty.MEDIUM, 4800, 200, 3);
		Player opponent = human("Alice", 5000);
		ActionManager actionManager = actionManager(computer, opponent, 5000);
		actionManager.setGameEndingPlayer(opponent);

		assertFalse(new ComputerStrategy(actionManager, true, true, false)
				.shouldRollAgain(computer, true));
		assertTrue(new ComputerStrategy(actionManager, true, false, false)
				.shouldRollAgain(computer, true));
	}

	@Test
	void mediumValuesHotDiceInsteadOfOnlyTheLargestImmediateScore() {
		Player computer = computer("Computer", ComputerDifficulty.MEDIUM, 0, 0, 6);
		ComputerStrategy strategy = strategy(computer, human("Alice", 0), 5000, true, true, false);
		GameOption immediate = option(RuleType.SINGLE, 1, 500, Map.of(1, 1));
		GameOption hotDice = option(
				RuleType.SET,
				null,
				300,
				Map.of(1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 1)
		);

		assertEquals(hotDice, strategy.chooseGameOption(computer, List.of(immediate, hotDice)));
	}

	@Test
	void hardSwitchesToTheSimulationDerivedStealingThresholds() {
		Player standardComputer = computer("Computer", ComputerDifficulty.HARD, 0, 1450, 4);
		Player opponent = human("Alice", 0);
		ComputerStrategy standard = strategy(standardComputer, opponent, 10000, false, true, false);
		assertTrue(standard.shouldRollAgain(standardComputer, true));

		Player stealingComputer = computer("Computer", ComputerDifficulty.HARD, 0, 1450, 4);
		ComputerStrategy stealing = strategy(stealingComputer, opponent, 10000, false, true, true);
		assertFalse(stealing.shouldRollAgain(stealingComputer, true));
	}

	@Test
	void policyAdjustmentsUseTheSimulatorsIntegerThresholdArithmetic() {
		Player computer = computer("Computer", ComputerDifficulty.HARD, 0, 1050, 1);
		ComputerStrategy strategy = strategy(computer, human("Alice", 2900), 5000, false, true, false);

		assertFalse(strategy.shouldRollAgain(computer, true));
		assertEquals(1050, strategy.policyBankThreshold(computer, 1050, 1));
	}

	@Test
	void hardStealingUsesTheTrainedContinuationCutoffs() {
		Player computer = computer("Computer", ComputerDifficulty.HARD, 1000, 0, 6);
		ComputerStrategy strategy = strategy(computer, human("Alice", 1000), 5000, true, true, true);

		Map<Integer, Integer> practicalCutoffs = Map.of(1, 550, 2, 450, 3, 350, 4, 250, 5, 150);
		for (Map.Entry<Integer, Integer> cutoff : practicalCutoffs.entrySet()) {
			assertFalse(strategy.shouldSteal(
					computer,
					new TurnContinuation("Alice", cutoff.getValue() - 50, cutoff.getKey(), Map.of())
			));
			assertTrue(strategy.shouldSteal(
					computer,
					new TurnContinuation("Alice", cutoff.getValue(), cutoff.getKey(), Map.of())
			));
		}
	}

	@Test
	void hardOptionUtilityCanPreferHotDiceOverAHigherImmediateScore() {
		Player computer = computer("Computer", ComputerDifficulty.HARD, 0, 0, 6);
		ComputerStrategy strategy = strategy(computer, human("Alice", 0), 5000, true, true, false);
		GameOption immediate = option(RuleType.SINGLE, 1, 500, Map.of(1, 1));
		GameOption hotDice = option(RuleType.SET, null, 300, Map.of(1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 1));

		assertEquals(hotDice, strategy.chooseGameOption(computer, List.of(immediate, hotDice)));
	}

	@Test
	void publishedDifficultyPoliciesRemainExact() {
		assertEquals(Map.of(1, 600, 2, 600, 3, 600, 4, 600, 5, 600, 6, 600),
				ComputerStrategy.EASY_POLICY.bankThresholdByDice());
		assertEquals(Map.of(1, 350, 2, 500, 3, 700, 4, 850, 5, 1000, 6, 1150),
				ComputerStrategy.MEDIUM_POLICY.bankThresholdByDice());
		assertEquals(Map.of(1, 200, 2, 1021, 3, 1128, 4, 1506, 5, 2130, 6, 2130),
				ComputerStrategy.HARD_STANDARD_POLICY.bankThresholdByDice());
		assertEquals(Map.of(1, 313, 2, 313, 3, 1106, 4, 1360, 5, 1360, 6, 1376),
				ComputerStrategy.HARD_STEALING_POLICY.bankThresholdByDice());
		assertPolicyWeights(
				ComputerStrategy.HARD_STANDARD_POLICY,
				1.0045, 36.0805, 354.561, 91.9329, 0, 0.293194, 0.193316, 136.066
		);
		assertPolicyWeights(
				ComputerStrategy.HARD_STEALING_POLICY,
				0.88553, 91.2663, 229.628, 94.2546, 0, 0.187935, 0.20764, -26.2974
		);
	}

	private void assertPolicyWeights(
			ComputerPolicy policy,
			double score,
			double remainingDice,
			double hotDice,
			double multiple,
			double lead,
			double trail,
			double closing,
			double rollBias
	) {
		assertEquals(score, policy.scoreWeight());
		assertEquals(remainingDice, policy.remainingDiceWeight());
		assertEquals(hotDice, policy.hotDiceWeight());
		assertEquals(multiple, policy.multipleWeight());
		assertEquals(lead, policy.leadFactor());
		assertEquals(trail, policy.trailFactor());
		assertEquals(closing, policy.closingFactor());
		assertEquals(rollBias, policy.rollBias());
	}

	private ComputerStrategy strategy(
			Player computer,
			Player opponent,
			int scoreLimit,
			boolean finalChase,
			boolean allowTies,
			boolean stealing
	) {
		return new ComputerStrategy(
				actionManager(computer, opponent, scoreLimit),
				finalChase,
				allowTies,
				stealing
		);
	}

	private ActionManager actionManager(Player computer, Player opponent, int scoreLimit) {
		return new ActionManager(
				new StubPlayerManager(List.of(computer, opponent)),
				new SequencedDiceManager(),
				scoreLimit,
				0
		);
	}

	private Player computer(
			String name,
			ComputerDifficulty difficulty,
			int permanentScore,
			int roundScore,
			int diceInPlay
	) {
		Dice dice = new Dice(new LinkedHashMap<>());
		dice.setNumDiceInPlay(diceInPlay);
		return new Player(
				name,
				dice,
				new Score(permanentScore, roundScore, 0),
				PlayerType.COMPUTER,
				difficulty
		);
	}

	private Player human(String name, int permanentScore) {
		return new Player(name, new Dice(new LinkedHashMap<>()), new Score(permanentScore, 0, 0));
	}

	private GameOption option(
			RuleType type,
			Integer selectedValue,
			int points,
			Map<Integer, Integer> consumedDice
	) {
		return new GameOption(type, type.toString(), "Test option", selectedValue, points, consumedDice);
	}
}

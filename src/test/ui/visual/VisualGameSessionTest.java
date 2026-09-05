package ui.visual;

import model.entities.ComputerDifficulty;
import model.entities.GameOption;
import org.junit.jupiter.api.Test;
import rules.managers.RuleType;
import support.TestDoubles.SequencedDiceManager;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


class VisualGameSessionTest
{
	@Test
	void hardVisualBankCommitmentSurvivesAHotDiceCollection() {
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(2, 2, 3, 2, 4, 1, 6, 1))
				.queueRoll(Map.of(1, 6));
		VisualGameSession session = sessionWithOnlySingles(diceManager);
		session.setComputerOpponentEnabled(true);
		session.setComputerDifficulty(ComputerDifficulty.HARD);
		session.startGame();
		session.roll();
		session.acknowledgeBust();
		session.getCurrentPlayer().score().setRoundScore(2700);

		for (int action = 0; action < 14 && session.isComputerTurn(); action++) {
			session.update(1);
		}

		assertFalse(session.isComputerTurn());
		assertEquals(3300, session.getPlayers().get(1).score().getPermanentScore());
		assertEquals(6, session.getPlayers().get(1).dice().getNumDiceInPlay());
		assertEquals(2, diceManager.rollCalls, "Collecting hot dice must not switch a bank plan back to rolling.");
	}

	@Test
	void hardVisualComputerCollectsItsWholeBankingRoll() {
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(2, 2, 3, 2, 4, 1, 6, 1))
				.queueRoll(Map.of(1, 2, 5, 1, 2, 1, 3, 1, 4, 1));
		VisualGameSession session = new VisualGameSession(diceManager);
		session.setRuleEnabled(RuleType.FIRST_ROLL_BUST, false);
		session.setComputerOpponentEnabled(true);
		session.setComputerDifficulty(ComputerDifficulty.HARD);
		session.startGame();
		session.roll();
		session.acknowledgeBust();
		assertTrue(session.isComputerTurn());
		session.getCurrentPlayer().score().setRoundScore(2700);

		for (int action = 0; action < 10 && session.isComputerTurn(); action++) {
			session.update(1);
		}

		assertFalse(session.isComputerTurn());
		assertEquals(2950, session.getPlayers().get(1).score().getPermanentScore());
		assertEquals(0, session.getPlayers().get(0).score().getPermanentScore());
		assertEquals(2, diceManager.rollCalls, "No additional roll is needed to collect already-scoring dice.");
	}

	@Test
	void enablingAComputerOpponentKeepsPlayerTwoAtTheTable() {
		VisualGameSession session = new VisualGameSession();
		session.setPlayerCount(1);

		session.setComputerOpponentEnabled(true);
		session.adjustPlayerCount(-1);

		assertEquals(2, session.getPlayerCount());
	}

	@Test
	void humanBustKeepsTheRollVisibleUntilItIsAcknowledged() {
		Map<Integer, Integer> bustRoll = Map.of(2, 2, 3, 2, 4, 1, 6, 1);
		VisualGameSession session = sessionWithOnlySingles(
				new SequencedDiceManager().queueRoll(bustRoll)
		);
		session.startGame();
		var bustedPlayer = session.getCurrentPlayer();

		session.roll();

		assertEquals(VisualGameSession.Phase.AWAITING_BUST_ACKNOWLEDGEMENT, session.getPhase());
		assertSame(bustedPlayer, session.getCurrentPlayer());
		assertEquals(bustRoll, bustedPlayer.dice().getDiceSetMap());
		assertEquals(java.util.List.of(2, 2, 3, 3, 4, 6), session.getCurrentDiceValues());
		assertEquals(0, bustedPlayer.score().getRoundScore());
		assertTrue(session.getNotice().startsWith("Bust!"));
		assertTrue(session.getNotice().contains("Review the roll, then continue."));

		session.update(10);
		assertEquals(VisualGameSession.Phase.AWAITING_BUST_ACKNOWLEDGEMENT, session.getPhase());
		assertSame(bustedPlayer, session.getCurrentPlayer());
		assertEquals(bustRoll, bustedPlayer.dice().getDiceSetMap());

		session.acknowledgeBust();
		assertEquals(VisualGameSession.Phase.AWAITING_ROLL, session.getPhase());
		assertEquals("Player 2", session.getCurrentPlayer().name());
	}

	@Test
	void visualComputerAcknowledgesItsVisibleBustAfterTheExistingDelay() {
		Map<Integer, Integer> bustRoll = Map.of(2, 2, 3, 2, 4, 1, 6, 1);
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(1, 1, 2, 5))
				.queueRoll(bustRoll);
		VisualGameSession session = sessionWithOnlySingles(diceManager);
		session.setOpeningScoreLimit(0);
		session.setComputerOpponentEnabled(true);
		session.startGame();

		session.roll();
		session.chooseOption(session.getCurrentOptions().get(0));
		session.bank();
		assertTrue(session.isComputerTurn());

		session.update(1);
		assertEquals(VisualGameSession.Phase.AWAITING_BUST_ACKNOWLEDGEMENT, session.getPhase());
		assertEquals(bustRoll, session.getCurrentPlayer().dice().getDiceSetMap());
		assertTrue(session.getNotice().startsWith("Bust!"));

		session.update(0.4f);
		assertEquals(VisualGameSession.Phase.AWAITING_BUST_ACKNOWLEDGEMENT, session.getPhase());
		session.update(0.1f);

		assertEquals(VisualGameSession.Phase.AWAITING_ROLL, session.getPhase());
		assertEquals("Player 1", session.getCurrentPlayer().name());
	}

	@Test
	void finalChaseBustWaitsForAcknowledgementBeforeShowingTheWinner() {
		Map<Integer, Integer> bustRoll = Map.of(2, 2, 3, 2, 4, 1, 6, 1);
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(1, 2, 2, 2, 5, 2))
				.queueRoll(bustRoll);
		VisualGameSession session = new VisualGameSession(diceManager);
		for (var rule : session.getSelectableRules()) {
			boolean enabled = rule.getRuleType().equals(RuleType.SET)
					|| rule.getRuleType().equals(RuleType.FINAL_CHASE);
			session.setRuleEnabled(rule.getRuleType(), enabled);
		}
		session.setScoreLimit(1000);
		session.startGame();

		session.roll();
		session.chooseOption(session.getCurrentOptions().get(0));
		session.bank();
		session.roll();

		assertEquals(VisualGameSession.Phase.AWAITING_BUST_ACKNOWLEDGEMENT, session.getPhase());
		assertEquals("Player 2", session.getCurrentPlayer().name());
		assertEquals(bustRoll, session.getCurrentPlayer().dice().getDiceSetMap());
		assertTrue(session.getNotice().startsWith("Bust!"));

		session.acknowledgeBust();

		assertEquals(VisualGameSession.Phase.GAME_OVER, session.getPhase());
		assertTrue(session.getNotice().contains("Player 1 wins"));
	}

	@Test
	void optionalComputerOpponentUsesTheSelectedDifficultyAndSharedTurnFlow() {
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(1, 1, 2, 5))
				.queueRoll(Map.of(2, 2, 3, 1, 6, 3));
		VisualGameSession session = new VisualGameSession(diceManager);
		for (var rule : session.getSelectableRules()) {
			boolean enabled = rule.getRuleType().equals(RuleType.SINGLE)
					|| rule.getRuleType().equals(RuleType.MULTIPLE);
			session.setRuleEnabled(rule.getRuleType(), enabled);
		}
		session.setOpeningScoreLimit(0);
		session.setComputerOpponentEnabled(true);
		session.setComputerDifficulty(ComputerDifficulty.EASY);
		session.startGame();

		assertTrue(session.getPlayers().get(1).isComputer());
		assertEquals(ComputerDifficulty.EASY, session.getPlayers().get(1).difficulty());

		session.roll();
		session.chooseOption(session.getCurrentOptions().get(0));
		session.bank();
		assertTrue(session.isComputerTurn());

		session.update(1);
		session.update(1);
		session.update(1);

		assertFalse(session.isComputerTurn());
		assertEquals("Player 1", session.getCurrentPlayer().name());
		assertEquals(600, session.getPlayers().get(1).score().getPermanentScore());
	}

	@Test
	void hardVisualComputerAcceptsAValuableStealingContinuation() {
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(1, 3, 2, 3));
		VisualGameSession session = new VisualGameSession(diceManager);
		for (var rule : session.getSelectableRules()) {
			boolean enabled = rule.getRuleType().equals(RuleType.MULTIPLE)
					|| rule.getRuleType().equals(RuleType.STEALING);
			session.setRuleEnabled(rule.getRuleType(), enabled);
		}
		session.setOpeningScoreLimit(0);
		session.setComputerOpponentEnabled(true);
		session.setComputerDifficulty(ComputerDifficulty.HARD);
		session.startGame();

		session.roll();
		session.chooseOption(session.getCurrentOptions().stream()
		                                  .filter(option -> option.selectedValue() == 1)
		                                  .findFirst()
		                                  .orElseThrow());
		session.bank();
		assertEquals(VisualGameSession.Phase.AWAITING_STEAL_DECISION, session.getPhase());

		session.update(1);

		assertEquals(VisualGameSession.Phase.AWAITING_ROLL, session.getPhase());
		assertEquals(1000, session.getCurrentPlayer().score().getRoundScore());
		assertEquals(3, session.getDiceInPlay());
	}

	@Test
	void firstRollBustCanBeToggledAtSetup() {
		VisualGameSession session = new VisualGameSession();

		assertTrue(
				session.getSelectableRules()
				       .stream()
				       .filter(rule -> rule.getRuleType().equals(RuleType.FIRST_ROLL_BUST))
				       .findFirst()
				       .map(session::isRuleEnabled)
				       .orElse(false)
		);

		session.setRuleEnabled(RuleType.FIRST_ROLL_BUST, false);

		assertFalse(
				session.getSelectableRules()
				       .stream()
				       .filter(rule -> rule.getRuleType().equals(RuleType.FIRST_ROLL_BUST))
				       .findFirst()
				       .map(session::isRuleEnabled)
				       .orElse(true)
		);
	}

	@Test
	void setupRequiresAtLeastOneScoringRule() {
		VisualGameSession session = new VisualGameSession();
		for (var rule : session.getSelectableRules()) {
			if (rule.isScoringRule()) {
				session.setRuleEnabled(rule.getRuleType(), false);
			}
		}

		assertFalse(session.canStart());
	}

	@Test
	void canonicalDefaultsEnableCoreRulesAndLeaveStealingOff() {
		VisualGameSession session = new VisualGameSession();

		assertFalse(session.isComputerOpponentEnabled());
		assertEquals(ComputerDifficulty.MEDIUM, session.getComputerDifficulty());
		assertFalse(
				session.getSelectableRules().stream()
				       .filter(rule -> rule.getRuleType().equals(RuleType.STEALING))
				       .findFirst()
				       .map(session::isRuleEnabled)
					       .orElse(true)
		);

		session.setRuleEnabled(RuleType.STEALING, true);

		assertTrue(
				session.getSelectableRules().stream()
				       .filter(rule -> rule.getRuleType().equals(RuleType.STEALING))
				       .findFirst()
				       .map(session::isRuleEnabled)
					       .orElse(false)
		);
		assertTrue(ruleEnabled(session, RuleType.SINGLE));
		assertTrue(ruleEnabled(session, RuleType.MULTIPLE));
		assertTrue(ruleEnabled(session, RuleType.SET));
		assertTrue(ruleEnabled(session, RuleType.STRAIT));
		assertTrue(ruleEnabled(session, RuleType.FIRST_ROLL_BUST));
		assertTrue(ruleEnabled(session, RuleType.FINAL_CHASE));
		assertTrue(ruleEnabled(session, RuleType.ALLOW_TIES));
		assertTrue(session.getSelectableRules().stream().noneMatch(rule -> rule.getRuleType().equals(RuleType.ADD_MULTIPLE)));
	}

	@Test
	void openingScoreLimitCanBeConfiguredWithinTheWinningScore() {
		VisualGameSession session = new VisualGameSession();

		session.setOpeningScoreLimit(1750);
		assertTrue(session.getOpeningScoreLimit() == 1750);

		session.setScoreLimit(1000);
		assertTrue(session.getOpeningScoreLimit() == 1000);

		session.setOpeningScoreLimit(-250);
		assertTrue(session.getOpeningScoreLimit() == VisualGameSession.MIN_OPENING_SCORE_LIMIT);
	}

	@Test
	void visualGameCanAcceptAndBankAStolenContinuation() {
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(1, 1, 2, 5))
				.queueRoll(Map.of(5, 1, 2, 4));
		VisualGameSession session = new VisualGameSession(diceManager);
		for (var rule : session.getSelectableRules()) {
			boolean enabled = rule.getRuleType().equals(RuleType.SINGLE) ||
					rule.getRuleType().equals(RuleType.STEALING);
			session.setRuleEnabled(rule.getRuleType(), enabled);
		}

		session.startGame();
		session.getPlayers().forEach(player -> player.score().increasePermanentScore(1000));
		session.roll();
		GameOption aliceOption = session.getCurrentOptions().stream()
		                                .filter(option -> option.type().equals(RuleType.SINGLE))
		                                .findFirst()
		                                .orElseThrow();
		session.chooseOption(aliceOption);
		session.bank();

		assertTrue(session.getPhase() == VisualGameSession.Phase.AWAITING_STEAL_DECISION);
		session.steal();
		assertTrue(session.getCurrentPlayer().score().getRoundScore() == 100);
		assertTrue(session.getDiceInPlay() == 5);

		session.roll();
		GameOption bobOption = session.getCurrentOptions().stream()
		                              .filter(option -> option.type().equals(RuleType.SINGLE))
		                              .findFirst()
		                              .orElseThrow();
		session.chooseOption(bobOption);
		session.bank();

		assertTrue(session.getPlayers().get(0).score().getPermanentScore() == 1100);
		assertTrue(session.getPlayers().get(1).score().getPermanentScore() == 1150);
	}

	@Test
	void visualGameCanScoreAOneAndFiveFromTheSameRoll() {
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(1, 1, 2, 4, 5, 1));
		VisualGameSession session = new VisualGameSession(diceManager);
		for (var rule : session.getSelectableRules()) {
			session.setRuleEnabled(rule.getRuleType(), rule.getRuleType().equals(RuleType.SINGLE));
		}
		session.setOpeningScoreLimit(0);
		session.startGame();

		session.roll();
		session.chooseOption(session.getCurrentOptions().stream()
		                            .filter(option -> option.selectedValue() == 1)
		                            .findFirst()
		                            .orElseThrow());
		assertTrue(session.canScoreMore());
		session.scoreMore();
		session.chooseOption(session.getCurrentOptions().stream()
		                            .filter(option -> option.selectedValue() == 5)
		                            .findFirst()
		                            .orElseThrow());
		session.bank();

		assertTrue(session.getPlayers().get(0).score().getPermanentScore() == 150);
	}

	@Test
	void visualHotDiceEndsTheMultipleExtensionChain() {
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(3, 6))
				.queueRoll(Map.of(3, 1, 1, 1, 2, 2, 4, 2));
		VisualGameSession session = new VisualGameSession(diceManager);
		for (var rule : session.getSelectableRules()) {
			boolean enabled = rule.getRuleType().equals(RuleType.MULTIPLE) ||
					rule.getRuleType().equals(RuleType.SINGLE);
			session.setRuleEnabled(rule.getRuleType(), enabled);
		}
		session.setOpeningScoreLimit(0);
		session.startGame();

		session.roll();
		session.chooseOption(session.getCurrentOptions().stream()
				.filter(option -> option.type().equals(RuleType.MULTIPLE))
				.findFirst()
				.orElseThrow());
		session.rollAgain();
		session.roll();

		assertFalse(session.getCurrentOptions().stream()
				.anyMatch(option -> option.type().equals(RuleType.ADD_MULTIPLE)));
		assertTrue(session.getCurrentOptions().stream()
				.anyMatch(option -> option.type().equals(RuleType.SINGLE) && option.selectedValue() == 1));
	}

	@Test
	void visualGameCanDisableTheFinalChase() {
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(1, 2, 2, 2, 5, 2));
		VisualGameSession session = new VisualGameSession(diceManager);
		for (var rule : session.getSelectableRules()) {
			session.setRuleEnabled(rule.getRuleType(), rule.getRuleType().equals(RuleType.SET));
		}
		session.setScoreLimit(1000);
		session.startGame();

		session.roll();
		session.chooseOption(session.getCurrentOptions().get(0));
		session.bank();

		assertTrue(session.getPhase() == VisualGameSession.Phase.GAME_OVER);
		assertFalse(session.isFinalRound());
		assertTrue(session.getNotice().contains("Player 1 wins"));
	}

	@Test
	void visualGameCanDisableTiesAndKeepTheIncumbentWinner() {
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(1, 2, 2, 2, 5, 2))
				.queueRoll(Map.of(1, 2, 2, 2, 5, 2));
		VisualGameSession session = new VisualGameSession(diceManager);
		for (var rule : session.getSelectableRules()) {
			boolean enabled = rule.getRuleType().equals(RuleType.SET) ||
					rule.getRuleType().equals(RuleType.FINAL_CHASE);
			session.setRuleEnabled(rule.getRuleType(), enabled);
		}
		session.setScoreLimit(1000);
		session.startGame();

		session.roll();
		session.chooseOption(session.getCurrentOptions().get(0));
		session.bank();
		session.roll();
		session.chooseOption(session.getCurrentOptions().get(0));
		session.bank();

		assertTrue(session.getPhase() == VisualGameSession.Phase.GAME_OVER);
		assertTrue(session.getNotice().contains("Player 1 wins"));
		assertFalse(session.getNotice().contains("tied"));
	}

	private boolean ruleEnabled(VisualGameSession session, RuleType ruleType) {
		return session.getSelectableRules().stream()
		              .filter(rule -> rule.getRuleType().equals(ruleType))
		              .findFirst()
		              .map(session::isRuleEnabled)
		              .orElse(false);
	}

	private VisualGameSession sessionWithOnlySingles(SequencedDiceManager diceManager) {
		VisualGameSession session = new VisualGameSession(diceManager);
		for (var rule : session.getSelectableRules()) {
			session.setRuleEnabled(rule.getRuleType(), rule.getRuleType().equals(RuleType.SINGLE));
		}
		return session;
	}
}

package ui.visual;

import model.entities.GameOption;
import org.junit.jupiter.api.Test;
import rules.managers.RuleType;
import support.TestDoubles.SequencedDiceManager;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class VisualGameSessionTest
{
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
}

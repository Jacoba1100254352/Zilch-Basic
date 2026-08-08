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
	void stealingCanBeToggledAtSetup() {
		VisualGameSession session = new VisualGameSession();

		assertTrue(
				session.getSelectableRules().stream()
				       .filter(rule -> rule.getRuleType().equals(RuleType.STEALING))
				       .findFirst()
				       .map(session::isRuleEnabled)
				       .orElse(false)
		);

		session.setRuleEnabled(RuleType.STEALING, false);

		assertFalse(
				session.getSelectableRules().stream()
				       .filter(rule -> rule.getRuleType().equals(RuleType.STEALING))
				       .findFirst()
				       .map(session::isRuleEnabled)
				       .orElse(true)
		);
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
}

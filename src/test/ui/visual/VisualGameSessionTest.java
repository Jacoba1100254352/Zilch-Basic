package ui.visual;

import org.junit.jupiter.api.Test;
import rules.managers.RuleType;

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
}

package rules.variable;


import model.entities.GameOption;
import rules.context.RuleContext;
import rules.managers.RuleType;

import java.util.List;


/**
 * Setup-selectable game variant that awards points and rerolls when a turn's
 * first roll produces no scoring options.
 */
public class FirstRollBustRule implements IRule
{
	private int pointsAwarded = 50;

	@Override
	public RuleType getRuleType() {
		return RuleType.FIRST_ROLL_BUST;
	}

	@Override
	public String getDisplayName() {
		return "First-Roll Bust";
	}

	@Override
	public String getDescription() {
		return "Award 50 points and roll again when the first roll has no scoring options.";
	}

	@Override
	public boolean isScoringRule() {
		return false;
	}

	@Override
	public void configure(Object configValue) {
		Object value = configValue == null ? getDefaultConfig() : configValue;
		if (!(value instanceof Number number)) {
			throw new IllegalArgumentException("First-roll bust points must be numeric.");
		}
		pointsAwarded = number.intValue();
	}

	@Override
	public Object getDefaultConfig() {
		return 50;
	}

	@Override
	public List<GameOption> evaluate(RuleContext context) {
		return List.of();
	}

	@Override
	public void apply(RuleContext context, GameOption option) {
		throw new UnsupportedOperationException("First-roll bust does not produce selectable scoring options.");
	}

	public int getPointsAwarded() {
		return pointsAwarded;
	}
}

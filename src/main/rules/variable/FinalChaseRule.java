package rules.variable;


import model.entities.GameOption;
import rules.context.RuleContext;
import rules.managers.RuleType;

import java.util.List;


/**
 * Setup-selectable metadata rule that gives every remaining player one final
 * turn after a player reaches the winning score.
 */
public class FinalChaseRule implements IRule
{
	@Override
	public RuleType getRuleType() {
		return RuleType.FINAL_CHASE;
	}

	@Override
	public String getDisplayName() {
		return "Final Chase";
	}

	@Override
	public String getDescription() {
		return "Give every other player one final turn after the target score is reached.";
	}

	@Override
	public boolean isScoringRule() {
		return false;
	}

	@Override
	public void configure(Object configValue) {
		validateBoolean(configValue);
	}

	@Override
	public Object getDefaultConfig() {
		return true;
	}

	@Override
	public List<GameOption> evaluate(RuleContext context) {
		return List.of();
	}

	@Override
	public void apply(RuleContext context, GameOption option) {
		throw new UnsupportedOperationException("Final Chase does not produce scoring options.");
	}

	private void validateBoolean(Object configValue) {
		Object value = configValue == null ? getDefaultConfig() : configValue;
		if (!(value instanceof Boolean)) {
			throw new IllegalArgumentException("Final Chase configuration must be boolean.");
		}
	}
}

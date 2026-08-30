package rules.variable;


import model.entities.GameOption;
import rules.context.RuleContext;
import rules.managers.RuleType;

import java.util.List;


/**
 * Setup-selectable metadata rule for the cross-player Stealing variant.
 * Runtime continuation behavior is coordinated by {@code StealingManager}.
 */
public class StealingRule implements IRule
{
	@Override
	public RuleType getRuleType() {
		return RuleType.STEALING;
	}

	@Override
	public String getDisplayName() {
		return "Stealing";
	}

	@Override
	public String getDescription() {
		return "Continue the previous banked turn with its remaining dice and score.";
	}

	@Override
	public boolean isScoringRule() {
		return false;
	}

	@Override
	public boolean isEnabledByDefault() {
		return false;
	}

	@Override
	public void configure(Object configValue) {
		Object value = configValue == null ? getDefaultConfig() : configValue;
		if (!(value instanceof Boolean)) {
			throw new IllegalArgumentException("Stealing configuration must be boolean.");
		}
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
		throw new UnsupportedOperationException("Stealing does not produce scoring options.");
	}
}

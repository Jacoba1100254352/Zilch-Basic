package rules.variable;


import model.entities.GameOption;
import rules.context.RuleContext;
import rules.managers.RuleType;

import java.util.List;


/**
 * Setup-selectable metadata rule that preserves tied winners. When disabled,
 * the first player to attain the final high score keeps the win.
 */
public class AllowTiesRule implements IRule
{
	@Override
	public RuleType getRuleType() {
		return RuleType.ALLOW_TIES;
	}

	@Override
	public String getDisplayName() {
		return "Allow Ties";
	}

	@Override
	public String getDescription() {
		return "Declare every player tied at the final high score a winner.";
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
		throw new UnsupportedOperationException("Allow Ties does not produce scoring options.");
	}

	private void validateBoolean(Object configValue) {
		Object value = configValue == null ? getDefaultConfig() : configValue;
		if (!(value instanceof Boolean)) {
			throw new IllegalArgumentException("Allow Ties configuration must be boolean.");
		}
	}
}

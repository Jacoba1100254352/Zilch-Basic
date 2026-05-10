package rules.constant;


import rules.managers.RuleType;


public class RollAgainRule extends AbstractConstantRule
{
	public RollAgainRule() {
		super(RuleType.ROLL_AGAIN, "Roll Again", "Represents the turn action that continues the current turn.");
	}

	@Override
	protected void setConfigValue(Object value) {
	}
}

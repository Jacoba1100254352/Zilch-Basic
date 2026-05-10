package rules.constant;


import rules.managers.RuleType;


public class EndTurnRule extends AbstractConstantRule
{
	public EndTurnRule() {
		super(RuleType.END_TURN, "End Turn", "Represents the turn action that banks the current score.");
	}

	@Override
	protected void setConfigValue(Object value) {
	}
}

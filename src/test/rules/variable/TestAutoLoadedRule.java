package rules.variable;


import model.entities.GameOption;
import rules.context.RuleContext;
import rules.managers.RuleType;

import java.util.List;


public class TestAutoLoadedRule extends AbstractVariableRule
{
	public TestAutoLoadedRule() {
		super(new RuleType("test_auto_loaded"), "Test Auto Loaded", "A test rule used to verify classpath discovery.");
	}

	@Override
	protected void setConfigValue(Object value) {
	}

	@Override
	public Object getDefaultConfig() {
		return true;
	}

	@Override
	public List<GameOption> evaluate(RuleContext context) {
		return List.of();
	}
}

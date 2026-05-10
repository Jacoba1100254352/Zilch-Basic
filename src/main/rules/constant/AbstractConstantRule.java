package rules.constant;


import model.entities.GameOption;
import rules.context.RuleContext;
import rules.managers.RuleType;

import java.util.List;


public abstract class AbstractConstantRule implements IConstantRule
{
	private final RuleType ruleType;
	private final String displayName;
	private final String description;

	protected AbstractConstantRule(RuleType ruleType, String displayName, String description) {
		this.ruleType = ruleType;
		this.displayName = displayName;
		this.description = description;
	}

	@Override
	public RuleType getRuleType() {
		return ruleType;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void configure(Object configValue) {
		setConfigValue(configValue);
	}

	protected abstract void setConfigValue(Object value);

	@Override
	public Object getDefaultConfig() {
		return null;
	}

	@Override
	public List<GameOption> evaluate(RuleContext context) {
		return List.of();
	}

	@Override
	public void apply(RuleContext context, GameOption option) {
	}
}

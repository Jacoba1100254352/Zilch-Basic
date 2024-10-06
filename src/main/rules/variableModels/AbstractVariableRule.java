package rules.variableModels;


import rules.context.ScoreContext;
import rules.managers.RuleType;

import java.util.Map;


public abstract class AbstractVariableRule implements IVariableRule
{
	protected RuleType ruleType;
	
	protected abstract void setConfigValue(Object value);
	
	@Override
	public void configure(Map<RuleType, Object> config) {
		if (!config.containsKey(ruleType)) {
			config.put(ruleType, getDefaultConfig().get(ruleType));
		}
		setConfigValue(config.get(ruleType));
	}
	
	@Override
	public RuleType getRuleType() {
		return ruleType;
	}
	
	@Override
	public abstract void score(ScoreContext scoreContext);
}

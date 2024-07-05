package rules.models;


import rules.managers.RuleType;

import java.util.Map;


public abstract class AbstractRule implements IRule
{
	protected RuleType ruleType;
	
	@Override
	public void configure(Map<RuleType, Object> config) {
		if (!config.containsKey(ruleType)) {
			config.put(ruleType, getDefaultConfig().get(ruleType));
		}
		setConfigValue(config.get(ruleType));
	}
	
	protected abstract void setConfigValue(Object value);
	
	@Override
	public RuleType getRuleType() {
		return ruleType;
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		throw new UnsupportedOperationException("This rule requires an additional value for validation.");
	}
}


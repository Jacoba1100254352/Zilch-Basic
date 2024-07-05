package rules.models;


import rules.context.IRuleContext;
import rules.managers.RuleType;

import java.util.Map;


public abstract class AbstractRule implements IRule
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
	public abstract boolean isValid(IRuleContext validationContext);
}


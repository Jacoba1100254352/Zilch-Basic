package rules.constant;


import rules.managers.RuleType;

import java.util.HashMap;
import java.util.Map;


public abstract class AbstractConstantRule implements IConstantRule
{
	protected RuleType ruleType;
	
	@Override
	public RuleType getRuleType() {
		return ruleType;
	}
	
	@Override
	public void configure(Map<RuleType, Object> config) {
		if (!config.containsKey(ruleType)) {
			config.put(ruleType, getDefaultConfig().get(ruleType));
		}
		setConfigValue(config.get(ruleType));
	}
	
	protected abstract void setConfigValue(Object value);
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		defaultConfig.put(ruleType, 1000); // Example default value
		return defaultConfig;
	}
}

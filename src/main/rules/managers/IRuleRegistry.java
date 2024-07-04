package rules.managers;


import rules.models.IRuleStrategy;

import java.util.Map;


public interface IRuleRegistry
{
	
	void configureRules(Map<RuleType, Object> config);
	
	IRuleStrategy getRule(RuleType ruleType);
	
	Map<RuleType, Object> getDefaultConfig();
}
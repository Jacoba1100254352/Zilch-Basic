package rules.managers;


import rules.models.IRule;

import java.util.Map;


public interface IRuleRegistry
{
	
	void configureRules(Map<RuleType, Object> config);
	
	IRule getRule(RuleType ruleType);
	
	Map<RuleType, Object> getDefaultConfig();
}
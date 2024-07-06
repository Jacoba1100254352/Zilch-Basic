package rules.managers;


import java.util.Map;


public interface IRuleRegistry
{
	
	void configureRules(Map<RuleType, Object> config);
	
	Object getRule(RuleType ruleType);
	
	Map<RuleType, Object> getDefaultConfig();
}
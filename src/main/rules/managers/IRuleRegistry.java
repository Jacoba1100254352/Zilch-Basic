package rules.managers;


import rules.variable.IRule;

import java.util.List;
import java.util.Map;


public interface IRuleRegistry
{
	void configureRules(Map<RuleType, Object> config);

	IRule getRule(RuleType ruleType);

	Map<RuleType, Object> getDefaultConfig();

	List<IRule> getAvailableRules();

	List<IRule> getActiveRules();
}

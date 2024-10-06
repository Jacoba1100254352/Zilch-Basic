package rules.variable;

import rules.managers.RuleType;

import java.util.Map;


public interface IRule
{
	RuleType getRuleType();
	String getDescription();
	void configure(Map<RuleType, Object> config);
	Map<RuleType, Object> getDefaultConfig();
}

package rules.models;


import rules.context.RuleContext;
import rules.context.ScoreContext;
import rules.managers.RuleType;

import java.util.Map;


public interface IRule
{
	RuleType getRuleType();
	
	String getDescription();
	
	void configure(Map<RuleType, Object> config);
	
	boolean isValid(RuleContext validationContext);
	
	Map<RuleType, Object> getDefaultConfig();
	
	void score(ScoreContext scoreContext);
}


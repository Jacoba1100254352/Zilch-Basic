package rules.models;


import rules.context.IRuleContext;
import rules.context.IScoreContext;
import rules.managers.RuleType;

import java.util.Map;


public interface IRule
{
	String getDescription();
	
	void configure(Map<RuleType, Object> config);
	
	boolean isValid(IRuleContext validationContext);
	
	Map<RuleType, Object> getDefaultConfig();
	
	RuleType getRuleType();
	
	void score(IScoreContext scoreContext);
}


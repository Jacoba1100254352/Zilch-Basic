package rules.models;


import model.entities.Score;
import rules.managers.RuleType;

import java.util.Map;


public interface IRule extends IRuleValidation
{
	String getDescription();
	
	void configure(Map<RuleType, Object> config);
	
	Map<RuleType, Object> getDefaultConfig();
	
	RuleType getRuleType();
	
	void score(Score score);
}


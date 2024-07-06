package rules.constantModels;


import rules.managers.RuleType;


public interface IConstantRule
{
	RuleType getRuleType();
	
	String getDescription();
	
	boolean isValid(Integer value1, Integer value2);
	
	void applyAction();
}

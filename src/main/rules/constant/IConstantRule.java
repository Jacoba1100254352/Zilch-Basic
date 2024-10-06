package rules.constant;


import rules.variable.IRule;


public interface IConstantRule extends IRule
{
	boolean isValid(Integer value1, Integer value2);
	void applyAction();
}

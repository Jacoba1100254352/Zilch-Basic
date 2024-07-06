package rules.constantModels;


import rules.variableModels.IRule;


public interface IConstantRule extends IRule
{
	boolean isValid(Integer value1, Integer value2);
	void applyAction();
}

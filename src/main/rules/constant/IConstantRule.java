package rules.constant;


import rules.variable.IRule;


public interface IConstantRule extends IRule
{
	@Override
	default boolean isSelectableAtSetup() {
		return false;
	}
}

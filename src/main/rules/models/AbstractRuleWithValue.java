package rules.models;


import java.util.Map;


public abstract class AbstractRuleWithValue extends AbstractRule implements IRuleWithValue
{
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		throw new UnsupportedOperationException("This rule requires an additional value for validation.");
	}
}

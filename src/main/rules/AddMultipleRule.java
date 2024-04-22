package rules;

import abstracts.AbstractRule;

import java.util.Map;

public class AddMultipleRule extends AbstractRule
{
	
	public AddMultipleRule(Integer value) {
		super(value);
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		return diceSetMap.getOrDefault(value, 0) >= 3;
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		return diceSetMap.getOrDefault(this.value, 0) >= 3;
	}
}

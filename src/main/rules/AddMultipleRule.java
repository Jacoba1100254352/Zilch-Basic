package rules;


import interfaces.IRule;

import java.util.Map;


public class AddMultipleRule implements IRule
{
	private final Integer value;
	
	public AddMultipleRule(Integer value) {
		this.value = value;
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		return diceSetMap.getOrDefault(value, 0) >= 3;
	}
}

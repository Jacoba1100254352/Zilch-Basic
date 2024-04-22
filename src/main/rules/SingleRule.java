package rules;


import interfaces.IRule;
import types.Singles;

import java.util.Map;


public class SingleRule implements IRule
{
	private int value;
	
	public SingleRule(int value) {
		this.value = value;
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		Singles single = new Singles(value);
		return single.isValidSingle() && diceSetMap.getOrDefault(value, 0) > 0;
	}
}

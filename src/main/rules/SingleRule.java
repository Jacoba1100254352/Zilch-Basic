package rules;


import abstracts.AbstractRule;
import types.Singles;

import java.util.Map;


public class SingleRule extends AbstractRule
{
	public SingleRule(Integer value) {
		super(value);
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		Singles single = new Singles(value);
		return single.isValidSingle() && diceSetMap.getOrDefault(value, 0) > 0;
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		Singles single = new Singles(value);
		return single.isValidSingle() && diceSetMap.getOrDefault(value, 0) > 0;
	}
}

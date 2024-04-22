package rules;

import abstracts.AbstractRule;

import java.util.Map;

public class MultipleRule extends AbstractRule
{
	
	public MultipleRule() {
		super(null);
	}

	public MultipleRule(Integer value) {
		super(value);
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		return diceSetMap.values().stream().anyMatch(count -> count >= 3);
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		return diceSetMap.getOrDefault(value, 0) >= 3;
	}
}

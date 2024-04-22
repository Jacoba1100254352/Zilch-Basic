package rules;


import interfaces.IRule;

import java.util.Map;


public class MultipleRule implements IRule
{
	private Integer value;
	
	public MultipleRule() {
	}
	
	public MultipleRule(Integer value) {
		this.value = value;
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		return diceSetMap.values().stream().anyMatch(count -> count >= 3);
	}
	
	public boolean isDesiredMultipleAvailable(Map<Integer, Integer> diceSetMap) {
		return diceSetMap.getOrDefault(value, 0) >= 3;
	}
}

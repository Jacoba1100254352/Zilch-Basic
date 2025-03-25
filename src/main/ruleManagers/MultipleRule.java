package ruleManagers;


import java.util.Map;


public class MultipleRule implements Rule
{
	private final Integer value;
	
	public MultipleRule() {
		this.value = null;
	}
	
	public MultipleRule(int value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	
	public int getCount(Map<Integer, Integer> diceSetMap) {
		return diceSetMap.getOrDefault(value, 0);
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		if (value != null) {
			return getCount(diceSetMap) >= 3;
		} else {
			return existsMultiple(diceSetMap);
		}
	}
	
	// NOTE: May want to make this necessary through interface or abstract class
	private boolean existsMultiple(Map<Integer, Integer> diceSetMap) {
		return diceSetMap.values().stream().anyMatch(count -> count >= 3);
	}
}

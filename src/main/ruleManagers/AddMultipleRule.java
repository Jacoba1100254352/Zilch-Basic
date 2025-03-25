package ruleManagers;


import java.util.Map;


public class AddMultipleRule implements Rule
{
	private final Integer value;
	private final Integer previouslySelectedMultipleValue;
	
	public AddMultipleRule(Integer value) {
		this.value = value;
		this.previouslySelectedMultipleValue = null;
	}
	
	public AddMultipleRule(Integer value, Integer previouslySelectedMultipleValue) {
		this.value = value;
		this.previouslySelectedMultipleValue = previouslySelectedMultipleValue;
	}
	
	public Integer getValue() {
		return value;
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		int numDiceInPlay = diceSetMap.values().stream().mapToInt(Integer::intValue).sum();// .stream().reduce(0, Integer::sum);
		if (value == null && previouslySelectedMultipleValue == null) {
			return false;
		} else if (value != null) {
			return value.equals(previouslySelectedMultipleValue) && numDiceInPlay <= 3 && diceSetMap.getOrDefault(value, 0) > 0;
		} else {
			return diceSetMap.getOrDefault(previouslySelectedMultipleValue, 0) > 0;
		}
	}
}
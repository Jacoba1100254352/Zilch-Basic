package ruleManagers;


import java.util.Map;


public class SingleRule implements Rule
{
	private final int value;
	
	public SingleRule(int value) {
		this.value = value;
	}
	
	public boolean isValidSingle() {
		return value == 1 || value == 5;
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		return isValidSingle() && diceSetMap.getOrDefault(value, 0) > 0;
	}
}

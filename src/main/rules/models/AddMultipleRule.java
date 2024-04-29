package rules.models;


import java.util.Map;


// FIXME: This Rule needs to be adjusted
public class AddMultipleRule implements IRuleStrategy
{
	Integer value;
	
	public AddMultipleRule(Integer value) {
		this.value = value;
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		return diceSetMap.getOrDefault(value, 0) >= 3;
	}
}

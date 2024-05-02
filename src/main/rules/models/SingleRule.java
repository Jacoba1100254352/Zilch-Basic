package rules.models;


import java.util.Map;
import java.util.Set;


public class SingleRule implements IRuleStrategy
{
	Set<Integer> acceptedValues;
	
	public SingleRule(Set<Integer> acceptedValues) {
		this.acceptedValues = acceptedValues;
	}
	
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		return this.acceptedValues.contains(value) && diceSetMap.getOrDefault(value, 0) > 0;
	}
}

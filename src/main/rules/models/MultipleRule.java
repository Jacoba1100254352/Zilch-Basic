package rules.models;


import java.util.Map;


public class MultipleRule implements IRuleStrategy
{
	Integer minimumMultiples;
	
	public MultipleRule(Integer minimumMultiples) {
		this.minimumMultiples = minimumMultiples;
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		// return diceSetMap.getOrDefault(value, 0) >= 3 && diceSetMap.values().stream().anyMatch(count -> count >= 3);
		return diceSetMap.getOrDefault(value, 0) >= minimumMultiples;
	}
}

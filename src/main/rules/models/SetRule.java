package rules.models;


import java.util.Map;


// FIXME: This Rule needs to be adjusted (what to do with value)
public class SetRule implements IRuleStrategy
{
	Integer value;
	
	public SetRule(Integer value) {
		this.value = value;
	}
	
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		return diceSetMap.size() == 3 && diceSetMap.values().stream().allMatch(count -> count == 2);
	}
}

package rules.implementations;


import rules.managers.RuleType;
import rules.models.IRule;

import java.util.HashMap;
import java.util.Map;


// FIXME: This Rule needs to be adjusted (what to do with value)
public class SetRule implements IRule
{
	private final RuleType key = RuleType.SET;
	private Integer setMin;
	
	public SetRule() {}
	
	@Override
	public void configure(Map<RuleType, Object> config) {
		if (!config.containsKey(key)) {
			config.put(key, getDefaultConfig().get(key));
		}
		this.setMin = (Integer) config.get(key);
	}
	
	@Override
	public String getDescription() {
		return "Set Rule";
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		return diceSetMap.size() == setMin && diceSetMap.values().stream().allMatch(count -> count == 2);
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		defaultConfig.put(key, 3); // Default value for setMin
		return defaultConfig;
	}
	
	@Override
	public RuleType getRuleType() {
		return key;
	}
}

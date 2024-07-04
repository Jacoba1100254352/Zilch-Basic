package rules.models;


import rules.managers.RuleType;

import java.util.HashMap;
import java.util.Map;


public class AddMultipleRule implements IRuleStrategy
{
	private final RuleType key = RuleType.ADD_MULTIPLE;
	private Integer value;
	
	public AddMultipleRule() {}
	
	@Override
	public void configure(Map<RuleType, Object> config) {
		if (!config.containsKey(key)) {
			config.put(key, getDefaultConfig().get(key));
		}
		this.value = (Integer) config.get(key);
	}
	
	@Override
	public String getDescription() {
		return "Add Multiple Rule";
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		return diceSetMap.getOrDefault(this.value, 0) >= this.value;
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		defaultConfig.put(key, 3); // Default value for addMultipleMin
		return defaultConfig;
	}
	
	@Override
	public RuleType getRuleType() {
		return key;
	}
}

package rules.models;


import rules.managers.RuleType;

import java.util.HashMap;
import java.util.Map;


public class MultipleRule implements IRuleStrategy
{
	private final RuleType key = RuleType.MULTIPLE;
	private Integer minimumMultiples;
	
	public MultipleRule() {}
	
	@Override
	public void configure(Map<RuleType, Object> config) {
		if (!config.containsKey(key)) {
			config.put(key, getDefaultConfig().get(key));
		}
		this.minimumMultiples = (Integer) config.get(key);
	}
	
	@Override
	public String getDescription() {
		return "Multiple Rule";
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		return diceSetMap.getOrDefault(value, 0) >= minimumMultiples;
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		defaultConfig.put(key, 3); // Default value for multipleMin
		return defaultConfig;
	}
	
	@Override
	public RuleType getRuleType() {
		return key;
	}
}

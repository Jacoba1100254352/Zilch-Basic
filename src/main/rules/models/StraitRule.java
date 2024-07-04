package rules.models;


import rules.managers.RuleType;

import java.util.HashMap;
import java.util.Map;

import static model.entities.Dice.FULL_SET_OF_DICE;


public class StraitRule implements IRuleStrategy
{
	private final RuleType key = RuleType.STRAIT;
	private Integer numStraitValues;
	
	public StraitRule() {}
	
	@Override
	public void configure(Map<RuleType, Object> config) {
		if (!config.containsKey(key)) {
			config.put(key, getDefaultConfig().get(key));
		}
		this.numStraitValues = (Integer) config.get(key);
	}
	
	@Override
	public String getDescription() {
		return "Strait Rule";
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		if (diceSetMap.size() != FULL_SET_OF_DICE) {
			return false;
		}
		
		for (int i = 1; i <= FULL_SET_OF_DICE; i++) {
			if (!diceSetMap.containsKey(i) || diceSetMap.get(i) != 1) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		defaultConfig.put(key, 6); // Default value for numStraitValues
		return defaultConfig;
	}
	
	@Override
	public RuleType getRuleType() {
		return key;
	}
}

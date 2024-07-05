package rules.implementations;


import model.entities.Player;
import model.managers.ActionManager;
import rules.managers.RuleType;
import rules.models.IRule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SingleRule implements IRule
{
	private final RuleType key = RuleType.SINGLE;
	private Set<Integer> acceptedValues;
	
	public SingleRule() {}
	
	@Override
	public void configure(Map<RuleType, Object> config) {
		if (!config.containsKey(key)) {
			config.put(key, getDefaultConfig().get(key));
		}
		this.acceptedValues = (Set<Integer>) config.get(key);
	}
	
	@Override
	public String getDescription() {
		return "Single Rule";
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		return this.acceptedValues.contains(value) && diceSetMap.getOrDefault(value, 0) > 0;
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		defaultConfig.put(key, Set.of(1, 5)); // Default values for singleValues
		return defaultConfig;
	}
	
	@Override
	public RuleType getRuleType() {
		return key;
	}
	
	@Override
	public void apply(ActionManager actionManager, Player player) {
	
	}
	
	@Override
	public void score() {
		int singleScore = (dieValue == 1) ? 100 : 50;
		score.increaseRoundScore(singleScore);
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		return false;
	}
}

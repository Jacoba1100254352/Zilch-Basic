package rules.variableModels;


import rules.context.RuleContext;
import rules.context.ScoreContext;
import rules.managers.RuleType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SingleRule extends AbstractVariableRule
{
	private Set<Integer> acceptedValues;
	
	@SuppressWarnings("unused") // This is automatically called by the ServiceLoader
	public SingleRule() {
		this.ruleType = RuleType.SINGLE;
	}
	
	@Override
	protected void setConfigValue(Object value) {
		this.acceptedValues = (Set<Integer>) value;
	}
	
	@Override
	public void configure(Map<RuleType, Object> config) {
		if (!config.containsKey(this.ruleType)) {
			config.put(this.ruleType, getDefaultConfig().get(this.ruleType));
		}
		this.acceptedValues = (Set<Integer>) config.get(this.ruleType);
	}
	
	@Override
	public String getDescription() {
		return "Single Rule";
	}
	
	@Override
	public boolean isValid(RuleContext validationContext) {
		if (validationContext.value() == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		
		return this.acceptedValues.contains(validationContext.value()) && validationContext.diceSetMap().getOrDefault(validationContext.value(), 0) > 0;
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		defaultConfig.put(this.ruleType, Set.of(1, 5)); // Default values for singleValues
		return defaultConfig;
	}
	
	@Override
	public void score(ScoreContext scoreContext) {
		if (scoreContext.dieValue() == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		
		int singleScore = (scoreContext.dieValue() == 1) ? 100 : 50;
		scoreContext.score().increaseRoundScore(singleScore);
	}
}

package rules.models;


import rules.context.IRuleContext;
import rules.context.IScoreContext;
import rules.managers.RuleType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SingleRule extends AbstractRule
{
	private Set<Integer> acceptedValues;
	
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
	public boolean isValid(IRuleContext validationContext) {
		if (validationContext.getValue() == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		
		return this.acceptedValues.contains(validationContext.getValue()) && validationContext.getDiceSetMap().getOrDefault(validationContext.getValue(), 0) > 0;
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		defaultConfig.put(this.ruleType, Set.of(1, 5)); // Default values for singleValues
		return defaultConfig;
	}
	
	@Override
	public void score(IScoreContext scoreContext) {
		if (scoreContext.getDieValue() == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		
		int singleScore = (scoreContext.getDieValue() == 1) ? 100 : 50;
		scoreContext.getScore().increaseRoundScore(singleScore);
	}
}

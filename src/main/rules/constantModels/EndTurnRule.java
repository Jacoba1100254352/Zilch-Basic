package rules.constantModels;


import rules.managers.RuleType;
import rules.variableModels.AbstractRule;

import java.util.HashMap;
import java.util.Map;


public class EndTurnRule extends AbstractRule implements IConstantRule
{
	private Integer startingScoreLimit;
	
	@SuppressWarnings("unused") // This is automatically called by the ServiceLoader
	public EndTurnRule() {
		this.ruleType = RuleType.END_TURN;
	}
	
	@Override
	public String getDescription() {
		return "End Turn Rule";
	}
	
	@Override
	public boolean isValid(Integer permanentScore, Integer roundScore) {
		return permanentScore >= this.startingScoreLimit || roundScore >= this.startingScoreLimit;
	}
	
	@Override
	public void applyAction() {
	
	}
	
	@Override
	protected void setConfigValue(Object value) {
		this.startingScoreLimit = (Integer) value;
	}
	
	@Override
	public void configure(Map<RuleType, Object> config) {
		if (!config.containsKey(ruleType)) {
			config.put(ruleType, getDefaultConfig().get(ruleType));
		}
		this.startingScoreLimit = (Integer) config.get(ruleType);
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		defaultConfig.put(ruleType, 1000); // Default value for startingScoreLimit
		return defaultConfig;
	}
}
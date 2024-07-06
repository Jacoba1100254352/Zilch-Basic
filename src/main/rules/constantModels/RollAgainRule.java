package rules.constantModels;


import rules.managers.RuleType;
import rules.variableModels.AbstractRule;

import java.util.HashMap;
import java.util.Map;


public class RollAgainRule extends AbstractRule implements IConstantRule
{
	@SuppressWarnings("unused") // This is automatically called by the ServiceLoader
	public RollAgainRule() {
		this.ruleType = RuleType.ROLL_AGAIN;
	}
	
	@Override
	public String getDescription() {
		return "Roll Again";
	}
	
	@Override
	protected void setConfigValue(Object value) {
		//this.startingScoreLimit = (Integer) value;
	}
	
	@Override
	public void configure(Map<RuleType, Object> config) {
		// This might throw an error upon analysis, if it does, comment out the code below
		if (!config.containsKey(ruleType)) {
			config.put(ruleType, getDefaultConfig().get(ruleType));
		}

//		this.startingScoreLimit = (Integer) config.get(ruleType);
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
//		defaultConfig.put(ruleType, 1000); // Default value for startingScoreLimit
		return defaultConfig;
	}
	
	@Override
	public boolean isValid(Integer roundScore, Integer numOptionsSelected) {
		return roundScore > 0 && numOptionsSelected > 0;
	}
	
	@Override
	public void applyAction() {
		//diceManager.rollDice(getDice());
	}
}

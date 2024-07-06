package rules.variableModels;


import rules.context.RuleContext;
import rules.context.ScoreContext;
import rules.managers.RuleType;

import java.util.HashMap;
import java.util.Map;

import static model.entities.Dice.FULL_SET_OF_DICE;


public class StraitRule extends AbstractRule implements IVariableRule
{
	private Integer minNumStraitValues;
	
	@SuppressWarnings("unused") // This is automatically called by the ServiceLoader
	public StraitRule() {
		this.ruleType = RuleType.STRAIT;
	}
	
	@Override
	protected void setConfigValue(Object value) {
		this.minNumStraitValues = (Integer) value;
	}
	
	@Override
	public void configure(Map<RuleType, Object> config) {
		if (!config.containsKey(this.ruleType)) {
			config.put(this.ruleType, getDefaultConfig().get(this.ruleType));
		}
		this.minNumStraitValues = (Integer) config.get(this.ruleType);
	}
	
	@Override
	public String getDescription() {
		return "Strait Rule";
	}
	
	@Override
	public boolean isValid(RuleContext validationContext) {
		// If you are required to have all dice in a strait, but not all dice are present, the rule is invalid
		if (this.minNumStraitValues == FULL_SET_OF_DICE && validationContext.getDiceSetMap().size() != FULL_SET_OF_DICE) {
			return false;
		}
		
		// The number of strait values found so far
		int numStraitValues = 0;
		
		for (int i = 1; i <= FULL_SET_OF_DICE; i++) {
			// Zero is used instead of != 1, because if the min is lower than the number of dice it might be permitted.
			// This could change in the future if a rule is added requiring strict strait rules.
			if (!validationContext.getDiceSetMap().containsKey(i) || validationContext.getDiceSetMap().get(i) == 0) {
				numStraitValues = 0;
			} else {
				numStraitValues++;
			}
			
			// If the number of strait values has reached the minimum required, the rule is valid
			if (numStraitValues >= this.minNumStraitValues) {
				return true;
			}
		}
		
		// If the loop finishes without returning true, the rule is invalid
		return false;
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		defaultConfig.put(this.ruleType, 6); // Default value for numStraitValues
		return defaultConfig;
	}
	
	@Override
	public void score(ScoreContext scoreContext) {
		scoreContext.getScore().increaseRoundScore(1000);
	}
}

package rules.variable;


import rules.context.RuleContext;
import rules.context.ScoreContext;
import rules.managers.RuleType;

import java.util.HashMap;
import java.util.Map;


public class SetRule extends AbstractVariableRule
{
	private Integer setMin;
	
	@SuppressWarnings("unused") // This is automatically called by the ServiceLoader
	public SetRule() {
		this.ruleType = RuleType.SET;
	}
	
	@Override
	public String getDescription() {
		return "Set Rule";
	}
	
	@Override
	public boolean isValid(RuleContext validationContext) {
		return validationContext.diceSetMap().size() == this.setMin && validationContext.diceSetMap().values().stream().allMatch(count -> count == 2);
	}
	
	@Override
	protected void setConfigValue(Object value) {
		this.setMin = (Integer) value;
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		defaultConfig.put(this.ruleType, 3); // Default value for setMin
		return defaultConfig;
	}
	
	// TODO: Might want to make 1000 configurable
	@Override
	public void score(ScoreContext scoreContext) {
		scoreContext.score().increaseRoundScore(1000);
	}
}

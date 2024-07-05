package rules.models;


import rules.context.IRuleContext;
import rules.context.IScoreContext;
import rules.managers.RuleType;

import java.util.HashMap;
import java.util.Map;


public class SetRule extends AbstractRule
{
	private Integer setMin;
	
	public SetRule() {
		this.ruleType = RuleType.SET;
	}
	
	@Override
	public String getDescription() {
		return "Set Rule";
	}
	
	@Override
	public boolean isValid(IRuleContext validationContext) {
		return validationContext.getDiceSetMap().size() == this.setMin && validationContext.getDiceSetMap().values().stream().allMatch(count -> count == 2);
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
	public void score(IScoreContext scoreContext) {
		scoreContext.getScore().increaseRoundScore(1000);
	}
}

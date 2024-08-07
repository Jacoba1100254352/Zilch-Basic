package rules.variableModels;


import rules.context.RuleContext;
import rules.context.ScoreContext;
import rules.managers.RuleType;

import java.util.HashMap;
import java.util.Map;


public class MultipleRule extends AbstractRule implements IVariableRule
{
	private Integer minimumMultiples;
	
	@SuppressWarnings("unused") // This is automatically called by the ServiceLoader
	public MultipleRule() {
		this.ruleType = RuleType.MULTIPLE;
	}
	
	@Override
	public String getDescription() {
		return "Multiple Rule";
	}
	
	@Override
	public boolean isValid(RuleContext validationContext) {
		if (validationContext.getValue() == null) {
			throw new IllegalArgumentException("Value cannot be null.");
		}
		
		return validationContext.getDiceSetMap().getOrDefault(validationContext.getValue(), 0) >= this.minimumMultiples;
	}
	
	@Override
	protected void setConfigValue(Object value) {
		this.minimumMultiples = (Integer) value;
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		defaultConfig.put(this.ruleType, 3); // Default value for multipleMin
		return defaultConfig;
	}
	
	@Override
	public void score(ScoreContext scoreContext) {
		int mScore = calculateMultipleScore(scoreContext.getNumGivenDice(), scoreContext.getDieValue());
		
		if (scoreContext.getScore().getScoreFromMultiples() == 0) {
			scoreContext.getScore().increaseRoundScore(mScore);
		} else { // Increase the round score by the difference between the new multiple score and the previous multiple score
			scoreContext.getScore().increaseRoundScore(mScore - scoreContext.getScore().getScoreFromMultiples());
		}
		
		scoreContext.getScore().setScoreFromMultiples(mScore);
	}
	
	private int calculateMultipleScore(int numMultiples, int dieValue) {
		int baseScore = (dieValue == 1) ? 1000 : dieValue * 100;
		numMultiples -= -3;
		return baseScore * (int) Math.pow(2, numMultiples);
	}
}

package rules.variable;


import rules.context.RuleContext;
import rules.context.ScoreContext;
import rules.managers.RuleType;

import java.util.HashMap;
import java.util.Map;


public class MultipleRule extends AbstractVariableRule
{
	private Integer minimumMultiples;
	
	@SuppressWarnings("unused")
	public MultipleRule() {
		this.ruleType = RuleType.MULTIPLE;
	}
	
	@Override
	public String getDescription() {
		return "Multiple Rule";
	}
	
	@Override
	public boolean isValid(RuleContext validationContext) {
		if (validationContext.value() == null) {
			throw new IllegalArgumentException("Value cannot be null.");
		}
		return validationContext.diceSetMap().getOrDefault(validationContext.value(), 0) >= this.minimumMultiples;
	}
	
	@Override
	protected void setConfigValue(Object value) {
		this.minimumMultiples = (Integer) value;
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		defaultConfig.put(this.ruleType, 3);
		return defaultConfig;
	}
	
	@Override
	public void score(ScoreContext scoreContext) {
		int mScore = calculateMultipleScore(scoreContext.numGivenDice(), scoreContext.dieValue());
		
		if (scoreContext.score().getScoreFromMultiples() == 0) {
			scoreContext.score().increaseRoundScore(mScore);
		} else {
			scoreContext.score().increaseRoundScore(mScore - scoreContext.score().getScoreFromMultiples());
		}
		
		scoreContext.score().setScoreFromMultiples(mScore);
	}
	
	private int calculateMultipleScore(int numMultiples, int dieValue) {
		int baseScore = (dieValue == 1) ? 1000 : dieValue * 100;
		numMultiples -= 3; // Corrected subtraction
		return baseScore * (int) Math.pow(2, numMultiples);
	}
}

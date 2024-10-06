package rules.variable;


import rules.context.RuleContext;
import rules.context.ScoreContext;
import rules.managers.RuleType;

import java.util.HashMap;
import java.util.Map;


// FIXME: This class will need to be checked as it needs to be differentiated from MultipleRule
// FIXME: There needs to be a check that verifies that the MultipleRule is enabled before this class can be enabled
public class AddMultipleRule extends AbstractVariableRule
{
	private final RuleType ruleType;
	private Integer value;
	
	@SuppressWarnings("unused") // This is automatically called by the ServiceLoader
	public AddMultipleRule() {
		this.ruleType = RuleType.ADD_MULTIPLE;
	}
	
	@Override
	public String getDescription() {
		return "Add Multiple Rule";
	}
	
	@Override
	public boolean isValid(RuleContext validationContext) {
		if (validationContext.value() == null) {
			throw new IllegalArgumentException("Value cannot be null.");
		}
		
		return validationContext.diceSetMap().getOrDefault(this.value, 0) >= validationContext.value(); // >= this.value
	}
	
	@Override
	protected void setConfigValue(Object value) {
		this.value = (Integer) value;
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		defaultConfig.put(ruleType, 3); // Default value for addMultipleMin
		return defaultConfig;
	}
	
	@Override
	public void score(ScoreContext scoreContext) {
		int mScore = calculateMultipleScore(scoreContext.numGivenDice(), scoreContext.dieValue());
		
		if (scoreContext.score().getScoreFromMultiples() == 0) {
			scoreContext.score().increaseRoundScore(mScore);
		} else { // Increase the round score by the difference between the new multiple score and the previous multiple score
			scoreContext.score().increaseRoundScore(mScore - scoreContext.score().getScoreFromMultiples());
		}
		
		scoreContext.score().setScoreFromMultiples(mScore);
	}
	
	private int calculateMultipleScore(int numMultiples, int dieValue) {
		int baseScore = (dieValue == 1) ? 1000 : dieValue * 100;
		numMultiples -= -3;
		return baseScore * (int) Math.pow(2, numMultiples);
	}
}

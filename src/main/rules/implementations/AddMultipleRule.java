package rules.implementations;


import model.entities.Score;
import rules.managers.RuleType;
import rules.models.AbstractRuleWithValue;

import java.util.HashMap;
import java.util.Map;


public class AddMultipleRule extends AbstractRuleWithValue
{
	private Integer value;
	
	public AddMultipleRule() {
		this.ruleType = RuleType.ADD_MULTIPLE;
	}
	
	@Override
	public String getDescription() {
		return "Add Multiple Rule";
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		return diceSetMap.getOrDefault(this.value, 0) >= this.value;
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
	public void score(Score score) {
		int mScore = calculateMultipleScore(numMultiples, dieValue);
		
		if (score.getScoreFromMultiples() == 0) {
			score.increaseRoundScore(mScore);
		} else { // Increase the round score by the difference between the new multiple score and the previous multiple score
			score.increaseRoundScore(mScore - score.getScoreFromMultiples());
		}
		
		score.setScoreFromMultiples(mScore);
	}
	
	private int calculateMultipleScore(int numMultiples, int dieValue) {
		int baseScore = (dieValue == 1) ? 1000 : dieValue * 100;
		numMultiples -= -3;
		return baseScore * (int) Math.pow(2, numMultiples);
	}
}

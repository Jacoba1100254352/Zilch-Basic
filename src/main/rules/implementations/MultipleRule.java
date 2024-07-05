package rules.implementations;


import model.entities.Score;
import rules.managers.RuleType;
import rules.models.AbstractRuleWithValue;

import java.util.HashMap;
import java.util.Map;


public class MultipleRule extends AbstractRuleWithValue
{
	private Integer minimumMultiples;
	
	public MultipleRule() {
		this.ruleType = RuleType.MULTIPLE;
	}
	
	@Override
	public String getDescription() {
		return "Multiple Rule";
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		return diceSetMap.getOrDefault(value, 0) >= minimumMultiples;
	}
	
	@Override
	protected void setConfigValue(Object value) {
		this.minimumMultiples = (Integer) value;
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		defaultConfig.put(ruleType, 3); // Default value for multipleMin
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
}

package rules.constantModels;


public class EndTurnRule implements IConstantRule
{
	Integer startingScoreLimit;
	
	EndTurnRule() {
		this.startingScoreLimit = 1000;
	}
	
	EndTurnRule(Integer startingScoreLimit) {
		this.startingScoreLimit = startingScoreLimit;
	}
	
	@Override
	public String getDescription() {
		return "End Turn Rule";
	}
	
	@Override
	public boolean isValid(Integer permanentScore, Integer roundScore) {
		return permanentScore >= this.startingScoreLimit || roundScore >= this.startingScoreLimit;
	}
}

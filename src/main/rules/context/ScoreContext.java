package rules.context;

import model.entities.Score;


public class ScoreContext
{
	private final Score score;
	private final Integer dieValue;
	private final Integer numGivenDice;
	
	public ScoreContext(Score score, Integer dieValue, Integer numGivenDice) {
		if (score == null) {
			throw new IllegalArgumentException("Score cannot be null.");
		}
		this.score = score;
		this.dieValue = dieValue;
		this.numGivenDice = numGivenDice;
	}
	
	public Score getScore() {
		return score;
	}
	
	public Integer getDieValue() {
		return dieValue;
	}
	
	public Integer getNumGivenDice() {
		return numGivenDice;
	}
}

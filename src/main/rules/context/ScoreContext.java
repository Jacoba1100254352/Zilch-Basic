package rules.context;


import model.entities.Score;


public class ScoreContext implements IScoreContext
{
	private final Score score;
	private final Integer dieValue;
	private final Integer numGivenDice;
	
	public ScoreContext(Score score) {
		this(score, null, null);
	}
	
	public ScoreContext(Score score, Integer dieValue) {
		this(score, dieValue, null);
	}
	
	public ScoreContext(Score score, Integer dieValue, Integer numGivenDice) {
		if (score == null) {
			throw new IllegalArgumentException("Score cannot be null.");
		}
		
		this.score = score;
		this.dieValue = dieValue;
		this.numGivenDice = numGivenDice;
	}
	
	@Override
	public Score getScore() {
		return this.score;
	}
	
	@Override
	public Integer getDieValue() {
		return this.dieValue;
	}
	
	@Override
	public Integer getNumGivenDice() {
		return this.numGivenDice;
	}
}

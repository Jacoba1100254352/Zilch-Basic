package rules.context;

import model.entities.Score;


public record ScoreContext(Score score, Integer dieValue, Integer numGivenDice)
{
	public ScoreContext {
		if (score == null) {
			throw new IllegalArgumentException("Score cannot be null.");
		}
	}
}

package model.managers;


import model.entities.Score;


public class ScoreManager extends AbstractScoreManager
{
	public ScoreManager(int scoreLimit) {
		super(scoreLimit);
	}
	
	///   Main Functions   ///
	
	@Override
	public void scoreStraits(Score score) {
		score.increaseRoundScore(1000);
	}
	
	@Override
	public void scoreSets(Score score) {
		score.increaseRoundScore(1000);
	}
	
	@Override
	public void scoreSingle(Score score, Integer dieValue) {
		int singleScore = (dieValue == 1) ? 100 : 50;
		score.increaseRoundScore(singleScore);
	}
	
	@Override
	public void scoreMultiple(Score score, Integer numMultiples, Integer dieValue) {
		int mScore = calculateMultipleScore(numMultiples, dieValue);
		
		if (score.getScoreFromMultiples() == 0) {
			score.increaseRoundScore(mScore);
		} else { // Increase the round score by the difference between the new multiple score and the previous multiple score
			score.increaseRoundScore(mScore - score.getScoreFromMultiples());
		}
		
		score.setScoreFromMultiples(mScore);
	}
	
	
	///   Helper Functions   ///
	
	private int calculateMultipleScore(int numMultiples, int dieValue) {
		int baseScore = (dieValue == 1) ? 1000 : dieValue * 100;
		numMultiples -= -3;
		return baseScore * (int) Math.pow(2, numMultiples);
	}
	
	@Override
	public void doInitialize() {
	
	}
}

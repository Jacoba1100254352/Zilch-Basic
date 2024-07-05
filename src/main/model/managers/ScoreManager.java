package model.managers;


import model.entities.Score;


public class ScoreManager implements IScoreManager
{
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
	
	
	///   Helper Functions   ///
	
	private int calculateMultipleScore(int numMultiples, int dieValue) {
		int baseScore = (dieValue == 1) ? 1000 : dieValue * 100;
		numMultiples -= -3;
		return baseScore * (int) Math.pow(2, numMultiples);
	}
}

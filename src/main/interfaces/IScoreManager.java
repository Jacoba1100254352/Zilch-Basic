package interfaces;


import models.Score;


public interface IScoreManager
{
	int scoreLimit = 2000;
	
	void scoreStraits(Score score);
	
	void scoreSets(Score score);
	
	void scoreSingle(Score score, int dieValue);
	
	void scoreMultiple(Score score, int numMultiples, int dieValue);
	
	default int getScoreLimit() {
		return scoreLimit;
	}
}

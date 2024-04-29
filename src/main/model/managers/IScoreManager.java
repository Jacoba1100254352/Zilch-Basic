package model.managers;


import model.entities.Score;


public interface IScoreManager
{
	int scoreLimit = 2000;
	
	void scoreStraits(Score score);
	
	void scoreSets(Score score);
	
	void scoreSingle(Score score, Integer dieValue);
	
	void scoreMultiple(Score score, Integer numMultiples, Integer dieValue);
	
	default int getScoreLimit() {
		return scoreLimit;
	}
}

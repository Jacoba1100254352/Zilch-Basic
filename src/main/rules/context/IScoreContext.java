package rules.context;


import model.entities.Score;


public interface IScoreContext
{
	Score getScore();
	
	Integer getDieValue();
	
	Integer getNumGivenDice();
}

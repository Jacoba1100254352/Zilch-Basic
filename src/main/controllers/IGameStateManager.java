package controllers;


public interface IGameStateManager
{
	void initializeRollCycle();
	
	void handleFirstRollBust();
	
	void handleBust();
	
	boolean isBust();
	
	void setBust(boolean bust);
	
	boolean getReroll();
	
	void setReroll(boolean reroll);
	
	boolean getContinueTurn();
	
	void setContinueTurn(boolean continueTurn);
}

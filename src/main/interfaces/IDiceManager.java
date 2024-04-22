package interfaces;


import models.Dice;


public interface IDiceManager
{
	void rollDice(Dice dice);
	
	void replenishAllDice(Dice dice);
	
	void removeAllDice(Dice dice);
	
	void eliminateDice(Dice dice, int dieValue);
	
	void removeDice(Dice dice, int dieValue, int numToRemove);
}

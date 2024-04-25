package modelManagers;


import abstracts.AbstractManager;
import interfaces.IDiceManager;
import models.Dice;

import java.security.SecureRandom;

import static models.Dice.FULL_SET_OF_DICE;


public class DiceManager extends AbstractManager implements IDiceManager
{
	
	private static final SecureRandom secureRandom = new SecureRandom();
	
	private static int roll() {
		return secureRandom.nextInt(6) + 1;
	}
	
	
	///   Main Functions   ///
	
	@Override
	public void rollDice(Dice dice) {
		dice.diceSetMap().clear();
		for (int i = 0; i < dice.getNumDiceInPlay(); i++) {
			int dieValue = roll();
			dice.diceSetMap().merge(dieValue, 1, Integer::sum);
		}
	}
	
	@Override
	public void replenishAllDice(Dice dice) {
		dice.diceSetMap().clear();
		dice.setNumDiceInPlay(FULL_SET_OF_DICE);
	}
	
	@Override
	public void removeAllDice(Dice dice) {
		dice.diceSetMap().clear();
		dice.setNumDiceInPlay(0);
	}
	
	@Override
	public void eliminateDice(Dice dice, int dieValue) {
		dice.diceSetMap().remove(dieValue);
		dice.calculateNumDiceInPlay();
	}
	
	@Override
	public void removeDice(Dice dice, int dieValue, int numToRemove) {
		dice.diceSetMap().put(dieValue, dice.diceSetMap().get(dieValue) - numToRemove);
		removeZeros(dice);
		dice.calculateNumDiceInPlay();
	}
	
	
	///   Helper Functions   ///
	
	private void removeZeros(Dice dice) {
		dice.diceSetMap().values().removeIf(value -> value == 0);
	}
	
	@Override
	protected void initialize() {
	
	}
}

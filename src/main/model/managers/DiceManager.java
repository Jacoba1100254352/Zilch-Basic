package model.managers;


import model.entities.Dice;

import java.security.SecureRandom;

import static model.entities.Dice.FULL_SET_OF_DICE;


public class DiceManager implements IDiceManager
{
	private static final SecureRandom secureRandom = new SecureRandom();
	
	private static int roll() {
		return secureRandom.nextInt(6) + 1;
	}
	
	@Override
	public void rollDice(Dice dice) {
		dice.getDiceSetMap().clear();
		for (int i = 0; i < dice.getNumDiceInPlay(); i++) {
			int dieValue = roll();
			dice.getDiceSetMap().merge(dieValue, 1, Integer::sum);
		}
	}
	
	@Override
	public void replenishAllDice(Dice dice) {
		dice.getDiceSetMap().clear();
		dice.setNumDiceInPlay(FULL_SET_OF_DICE);
	}
	
	@Override
	public void removeAllDice(Dice dice) {
		dice.getDiceSetMap().clear();
		dice.setNumDiceInPlay(0);
	}
	
	@Override
	public void removeDice(Dice dice, int dieValue) {
		dice.getDiceSetMap().remove(dieValue);
		dice.calculateNumDiceInPlay();
	}
	
	@Override
	public void removeDice(Dice dice, int dieValue, int numToRemove) {
		dice.getDiceSetMap().put(dieValue, dice.getDiceSetMap().get(dieValue) - numToRemove);
		removeZeros(dice);
		dice.calculateNumDiceInPlay();
	}
	
	private void removeZeros(Dice dice) {
		dice.getDiceSetMap().values().removeIf(value -> value == 0);
	}
}

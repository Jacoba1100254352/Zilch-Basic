package model.managers;


import model.entities.Dice;

import java.security.SecureRandom;

import static model.entities.Dice.FULL_SET_OF_DICE;


/**
 * Encapsulates dice rolling and mutation operations on a player's dice pool.
 */
public class DiceManager implements IDiceManager
{
	private static final SecureRandom secureRandom = new SecureRandom();
	
	/**
	 * Rolls a single six-sided die.
	 */
	private static int roll() {
		return secureRandom.nextInt(6) + 1;
	}
	
	@Override
	/**
	 * Rolls all dice currently in play and rebuilds the counted value map.
	 */
	public void rollDice(Dice dice) {
		dice.getDiceSetMap().clear();
		for (int i = 0; i < dice.getNumDiceInPlay(); i++) {
			int dieValue = roll();
			dice.getDiceSetMap().merge(dieValue, 1, Integer::sum);
		}
	}
	
	@Override
	/**
	 * Restores the full six-dice pool for a new turn or hot-dice reroll.
	 */
	public void replenishAllDice(Dice dice) {
		dice.getDiceSetMap().clear();
		dice.setNumDiceInPlay(FULL_SET_OF_DICE);
	}
	
	@Override
	/**
	 * Removes all dice from play.
	 */
	public void removeAllDice(Dice dice) {
		dice.getDiceSetMap().clear();
		dice.setNumDiceInPlay(0);
	}
	
	@Override
	/**
	 * Removes all instances of a die value from play.
	 */
	public void removeDice(Dice dice, int dieValue) {
		dice.getDiceSetMap().remove(dieValue);
		dice.calculateNumDiceInPlay();
	}
	
	@Override
	/**
	 * Removes a specific number of dice of the given value from play.
	 */
	public void removeDice(Dice dice, int dieValue, int numToRemove) {
		dice.getDiceSetMap().put(dieValue, dice.getDiceSetMap().get(dieValue) - numToRemove);
		removeZeros(dice);
		dice.calculateNumDiceInPlay();
	}
	
	/**
	 * Prunes any die values whose count has dropped to zero.
	 */
	private void removeZeros(Dice dice) {
		dice.getDiceSetMap().values().removeIf(value -> value == 0);
	}
}

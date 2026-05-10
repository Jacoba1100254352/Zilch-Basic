package model.entities;

import java.util.Map;


/**
 * Represents the dice currently owned by a player during a turn, including
 * both the rolled value counts and the number of dice still in play.
 */
public class Dice
{
	public static final int FULL_SET_OF_DICE = 6;
	private final Map<Integer, Integer> diceSetMap;
	private int numDiceInPlay;
	
	/**
	 * Creates a dice container backed by the supplied value-count map.
	 */
	public Dice(Map<Integer, Integer> diceSetMap) {
		this.diceSetMap = diceSetMap;
		this.numDiceInPlay = FULL_SET_OF_DICE;
	}
	
	/**
	 * Returns the mutable map of die value to count for the current roll.
	 */
	public Map<Integer, Integer> getDiceSetMap() {
		return diceSetMap;
	}
	
	/**
	 * Replaces the current value-count map contents with the supplied counts.
	 */
	public void setDiceSetMap(Map<Integer, Integer> diceSetMap) {
		this.diceSetMap.clear();
		this.diceSetMap.putAll(diceSetMap);
	}
	
	/**
	 * Returns the number of dice still available to roll this turn.
	 */
	public int getNumDiceInPlay() {
		return numDiceInPlay;
	}
	
	/**
	 * Sets the number of dice currently available for rolling.
	 */
	public void setNumDiceInPlay(int numOfDice) {
		this.numDiceInPlay = numOfDice;
	}
	
	/**
	 * Recalculates the number of dice in play based on the current diceSetMap values.
	 */
	public void calculateNumDiceInPlay() {
		// Recalculate the number of dice in play based on the current diceSetMap values.
		numDiceInPlay = diceSetMap.values().stream().mapToInt(Integer::intValue).sum();
	}
}

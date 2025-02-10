package model.entities;

import java.util.Map;


public class Dice
{
	public static final int FULL_SET_OF_DICE = 6;
	private final Map<Integer, Integer> diceSetMap;
	private int numDiceInPlay;
	
	public Dice(Map<Integer, Integer> diceSetMap) {
		this.diceSetMap = diceSetMap;
		this.numDiceInPlay = FULL_SET_OF_DICE;
	}
	
	public Map<Integer, Integer> getDiceSetMap() {
		return diceSetMap;
	}
	
	public void setDiceSetMap(Map<Integer, Integer> diceSetMap) {
		this.diceSetMap.clear();
		this.diceSetMap.putAll(diceSetMap);
	}
	
	public int getNumDiceInPlay() {
		return numDiceInPlay;
	}
	
	public void setNumDiceInPlay(int numOfDice) {
		this.numDiceInPlay = numOfDice;
	}
	
	public void calculateNumDiceInPlay() {
		// Recalculate the number of dice in play based on the current diceSetMap values.
		numDiceInPlay = diceSetMap.values().stream().mapToInt(Integer::intValue).sum();
	}
}

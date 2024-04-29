package model.entities;


import java.util.Map;


public record Dice(Map<Integer, Integer> diceSetMap)
{
	public static final int FULL_SET_OF_DICE = 6;
	private static int numDiceInPlay;
	
	public void setDiceSetMap(Map<Integer, Integer> diceSetMap) {
		this.diceSetMap.clear();
		this.diceSetMap.putAll(diceSetMap);
	}
	
	public int getNumDiceInPlay() {
		return numDiceInPlay;
	}
	
	public void setNumDiceInPlay(int numOfDice) {
		numDiceInPlay = numOfDice;
	}
	
	public void calculateNumDiceInPlay() {
		numDiceInPlay = diceSetMap.values().stream().mapToInt(Integer::intValue).sum();
	}
}

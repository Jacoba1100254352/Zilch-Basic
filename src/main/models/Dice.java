package models;

import java.util.Map;

public record Dice(Map<Integer, Integer> diceSetMap) {
    public void setDiceSetMap(Map<Integer, Integer> diceSetMap) {
        this.diceSetMap.clear();

        this.diceSetMap.putAll(diceSetMap);
    }

    public static final int FULL_SET_OF_DICE = 6;
    private static int numDiceInPlay;

    public int getNumDiceInPlay() {
        return numDiceInPlay;
    }

    public void setNumDiceInPlay(int numOfDice) {
        numDiceInPlay = numOfDice;
    }
}

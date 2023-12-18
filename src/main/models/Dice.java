package models;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Dice {
    public static final int FULL_SET_OF_DICE = 6;
    private static final SecureRandom secureRandom = new SecureRandom();
    private final Map<Integer, Integer> diceSetMap;
    private int numDiceInPlay;
    private boolean multipleExists;

    public Dice() {
        this.diceSetMap = new HashMap<>();
        this.numDiceInPlay = FULL_SET_OF_DICE;
        this.multipleExists = false;
    }

    private static int roll() {
        return secureRandom.nextInt(6) + 1;
    }

    private void removeZeros() {
        diceSetMap.values().removeIf(value -> value == 0);
    }

    public void rollDice() {
        diceSetMap.clear();
        multipleExists = false;

        for (int i = 0; i < this.numDiceInPlay; i++) {
            int dieValue = roll();
            diceSetMap.merge(dieValue, 1, Integer::sum);

            if (diceSetMap.get(dieValue) >= 3) {
                multipleExists = true;
            }
        }
    }

    public void calculateMultipleAvailability() {
        multipleExists = diceSetMap.values().stream().anyMatch(count -> count >= 3);
    }

    public boolean isMultipleAvailable() {
        return multipleExists;
    }

    public void eliminateDice(int dieValue) {
        diceSetMap.put(dieValue, 0);
        removeZeros();
    }

    public void displayDice() {
        System.out.println("\nYou have " + numDiceInPlay + " dice left.");

        ///   Build and Print Dice List String   ///
        StringBuilder diceList = new StringBuilder();
        diceSetMap.forEach((key, value) -> {
            for (int i = 0; i < value; i++) {
                diceList.append(key).append(", ");
            }
        });

        // Remove the trailing comma and space
        if (!diceList.isEmpty()) {
            diceList.setLength(diceList.length() - 2);
        }

        System.out.println(diceList);
    }

    public int getNumDiceInPlay() {
        return numDiceInPlay;
    }

    public void setNumDiceInPlay(int numOfDice) {
        removeZeros();

        numDiceInPlay = Math.min(numOfDice, 6);
    }

    public Map<Integer, Integer> getDiceSetMap() {
        return diceSetMap;
    }
}

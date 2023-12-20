package modelManagers;

import models.Dice;

import java.security.SecureRandom;

import static models.Dice.FULL_SET_OF_DICE;

public record DiceManager(Dice dice) {
    private static final SecureRandom secureRandom = new SecureRandom();

    private static int roll() {
        return secureRandom.nextInt(6) + 1;
    }

    public void rollDice(Dice dice) {
        dice.diceSetMap().clear();
        for (int i = 0; i < dice.getNumDiceInPlay(); i++) {
            int dieValue = roll();
            dice.diceSetMap().merge(dieValue, 1, Integer::sum);
        }
    }

    public void replenishAllDice() {
        removeZeros();
        dice.setNumDiceInPlay(FULL_SET_OF_DICE);
    }

    public void removeAllDice() {
        dice.diceSetMap().clear();
        dice.setNumDiceInPlay(0);
    }

    public void eliminateDice(int dieValue) {
        dice.diceSetMap().put(dieValue, 0);
        removeZeros();
    }

    private void removeZeros() {
        dice.diceSetMap().values().removeIf(value -> value == 0);
    }

    public void removeDice(int dieValue, int numToRemove) {
        dice.diceSetMap().put(dieValue, dice.diceSetMap().get(dieValue) - numToRemove);
        removeZeros();
    }
}

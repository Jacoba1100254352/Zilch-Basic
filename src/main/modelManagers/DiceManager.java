package modelManagers;


import java.security.SecureRandom;

import static models.Dice.FULL_SET_OF_DICE;


public class DiceManager extends PlayerActionManager
{
	private static final SecureRandom secureRandom = new SecureRandom();
	
	public DiceManager(PlayerManager playerManager) {
		super(playerManager);
	}
	
	private static int roll() {
		return secureRandom.nextInt(6) + 1;
	}
	
	
	///   Main Functions   ///
	
	public void rollDice() {
		getDice().getDiceSetMap().clear();
		for (int i = 0; i < getDice().getNumDiceInPlay(); i++) {
			int dieValue = roll();
			getDice().getDiceSetMap().merge(dieValue, 1, Integer::sum);
		}
	}
	
	public void replenishAllDice() {
		getDice().getDiceSetMap().clear();
		getDice().setNumDiceInPlay(FULL_SET_OF_DICE);
	}
	
	public void removeAllDice() {
		getDice().getDiceSetMap().clear();
		getDice().setNumDiceInPlay(0);
	}
	
	public void eliminateDice(int dieValue) {
		getDice().getDiceSetMap().remove(dieValue);
		getDice().calculateNumDiceInPlay();
	}
	
	public void removeDice(int dieValue, int numToRemove) {
		getDice().getDiceSetMap().put(dieValue, getDice().getDiceSetMap().get(dieValue) - numToRemove);
		removeZeros();
		getDice().calculateNumDiceInPlay();
	}
	
	
	///   Helper Functions   ///
	
	private void removeZeros() {
		getDice().getDiceSetMap().values().removeIf(value -> value == 0);
	}
}

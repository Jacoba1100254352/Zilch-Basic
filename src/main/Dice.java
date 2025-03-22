// Dice.java
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Dice {
	// Public map to store the dice values and their counts.
	public Map<Integer, Integer> diceSetMap;
	private int numDiceInPlay;
	private boolean multipleExists;
	private final Random random;
	
	public Dice() {
		this.numDiceInPlay = Constants.FULL_SET_OF_DICE;
		this.multipleExists = false;
		this.diceSetMap = new HashMap<>();
		this.random = new Random();
	}
	
	// Remove keys with zero count.
	private void removeZeros() {
		diceSetMap.entrySet().removeIf(entry -> entry.getValue() == 0);
	}
	
	// Roll a single die (1–6).
	private int roll() {
		return random.nextInt(6) + 1;
	}
	
	public void rollDice() {
		diceSetMap.clear();
		multipleExists = false;
		for (int i = 0; i < getNumDiceInPlay(); i++) {
			int dieValue = roll();
			diceSetMap.put(dieValue, diceSetMap.getOrDefault(dieValue, 0) + 1);
			if (diceSetMap.get(dieValue) >= 3) {
				multipleExists = true;
			}
		}
	}
	
	public int getNumDiceInPlay() {
		return numDiceInPlay;
	}
	
	public void setNumDiceInPlay(int numOfDice) {
		removeZeros();
		numDiceInPlay = Math.min(numOfDice, Constants.FULL_SET_OF_DICE);
	}
	
	// Recount the dice in play by summing the counts.
	public void recountNumDiceInPlay() {
		removeZeros();
		numDiceInPlay = 0;
		for (int count : diceSetMap.values()) {
			numDiceInPlay += count;
		}
	}
	
	public void eliminateDice(int dieValue) {
		diceSetMap.put(dieValue, 0);
		removeZeros();
	}
	
	public void calculateMultipleAvailability() {
		multipleExists = false;
		for (int count : diceSetMap.values()) {
			if (count >= 3) {
				multipleExists = true;
				break;
			}
		}
	}
	
	public boolean isMultipleAvailable() {
		return multipleExists;
	}
	
	public void displayDice() {
		System.out.println("\nYou have " + numDiceInPlay + " dice left");
		StringBuilder diceList = new StringBuilder();
		for (Map.Entry<Integer, Integer> entry : diceSetMap.entrySet()) {
			for (int i = 0; i < entry.getValue(); i++) {
				diceList.append(entry.getKey()).append(", ");
			}
		}
		if (!diceList.isEmpty()) {
			diceList.setLength(diceList.length() - 2); // Remove trailing comma and space.
		}
		System.out.println(diceList.toString());
	}
}

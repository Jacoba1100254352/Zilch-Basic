package model.managers;


import model.entities.Dice;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class DiceManagerTest
{
	private final DiceManager diceManager = new DiceManager();

	@Test
	void rollDicePopulatesCountsForDiceInPlay() {
		Dice dice = new Dice(new HashMap<>());
		dice.setNumDiceInPlay(4);

		diceManager.rollDice(dice);

		int totalRolledDice = dice.getDiceSetMap().values().stream().mapToInt(Integer::intValue).sum();
		assertEquals(4, totalRolledDice);
		assertTrue(dice.getDiceSetMap().keySet().stream().allMatch(value -> value >= 1 && value <= 6));
	}

	@Test
	void replenishAllDiceRestoresFullPoolAndClearsMap() {
		Dice dice = new Dice(new HashMap<>(Map.of(1, 2, 5, 1)));
		dice.setNumDiceInPlay(3);

		diceManager.replenishAllDice(dice);

		assertTrue(dice.getDiceSetMap().isEmpty());
		assertEquals(Dice.FULL_SET_OF_DICE, dice.getNumDiceInPlay());
	}

	@Test
	void removeAllDiceClearsEverything() {
		Dice dice = new Dice(new HashMap<>(Map.of(1, 2, 5, 1)));

		diceManager.removeAllDice(dice);

		assertTrue(dice.getDiceSetMap().isEmpty());
		assertEquals(0, dice.getNumDiceInPlay());
	}

	@Test
	void removeDiceByValueRemovesAllMatchingDiceAndRecalculates() {
		Dice dice = new Dice(new HashMap<>(Map.of(1, 2, 5, 1, 6, 3)));

		diceManager.removeDice(dice, 6);

		assertEquals(Map.of(1, 2, 5, 1), dice.getDiceSetMap());
		assertEquals(3, dice.getNumDiceInPlay());
	}

	@Test
	void removeDiceByCountReducesCountAndPrunesZeroEntries() {
		Dice dice = new Dice(new HashMap<>(Map.of(1, 2, 5, 1, 6, 3)));

		diceManager.removeDice(dice, 6, 2);
		assertEquals(Map.of(1, 2, 5, 1, 6, 1), dice.getDiceSetMap());
		assertEquals(4, dice.getNumDiceInPlay());

		diceManager.removeDice(dice, 6, 1);
		assertEquals(Map.of(1, 2, 5, 1), dice.getDiceSetMap());
		assertEquals(3, dice.getNumDiceInPlay());
	}
}

package model.entities;


import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


class DiceTest
{
	@Test
	void setDiceSetMapReplacesTheExistingCounts() {
		Dice dice = new Dice(new LinkedHashMap<>(Map.of(1, 2, 5, 1)));

		dice.setDiceSetMap(Map.of(2, 3, 6, 1));

		assertEquals(Map.of(2, 3, 6, 1), dice.getDiceSetMap());
	}

	@Test
	void calculateNumDiceInPlaySumsTheCurrentCounts() {
		Dice dice = new Dice(new LinkedHashMap<>(Map.of(1, 2, 5, 1, 6, 3)));

		dice.calculateNumDiceInPlay();

		assertEquals(6, dice.getNumDiceInPlay());
	}
}

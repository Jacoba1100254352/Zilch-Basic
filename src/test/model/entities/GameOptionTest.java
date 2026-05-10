package model.entities;


import org.junit.jupiter.api.Test;
import rules.managers.RuleType;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class GameOptionTest
{
	@Test
	void constructorDefensivelyCopiesConsumedDice() {
		Map<Integer, Integer> consumedDice = new HashMap<>(Map.of(1, 1));
		GameOption option = new GameOption(RuleType.SINGLE, "Single", "desc", 1, 100, consumedDice);

		consumedDice.put(5, 1);

		assertEquals(Map.of(1, 1), option.consumedDice());
		assertThrows(UnsupportedOperationException.class, () -> option.consumedDice().put(2, 1));
	}

	@Test
	void constructorRejectsNullFields() {
		assertThrows(NullPointerException.class, () -> new GameOption(null, "Single", "desc", 1, 100, Map.of(1, 1)));
		assertThrows(NullPointerException.class, () -> new GameOption(RuleType.SINGLE, null, "desc", 1, 100, Map.of(1, 1)));
		assertThrows(NullPointerException.class, () -> new GameOption(RuleType.SINGLE, "Single", null, 1, 100, Map.of(1, 1)));
		assertThrows(NullPointerException.class, () -> new GameOption(RuleType.SINGLE, "Single", "desc", 1, 100, null));
	}
}

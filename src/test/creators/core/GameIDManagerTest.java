package creators.core;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class GameIDManagerTest
{
	@Test
	void generateGameIDReturnsUniqueTrackedIds() {
		GameIDManager gameIDManager = new GameIDManager();

		String first = gameIDManager.generateGameID();
		String second = gameIDManager.generateGameID();

		assertNotEquals(first, second);
		assertFalse(gameIDManager.isGameIDUnique(first));
		assertFalse(gameIDManager.isGameIDUnique(second));
	}

	@Test
	void addGameIDMarksItAsNonUnique() {
		GameIDManager gameIDManager = new GameIDManager();

		gameIDManager.addGameID("custom-id");

		assertFalse(gameIDManager.isGameIDUnique("custom-id"));
		assertTrue(gameIDManager.isGameIDUnique("another-id"));
	}
}

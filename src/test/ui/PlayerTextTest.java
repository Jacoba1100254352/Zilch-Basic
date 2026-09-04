package ui;


import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PlayerTextTest
{
	@Test
	void legacyYouNameUsesSecondPersonPossessivesVerbsAndTurnHeading() {
		assertTrue(PlayerText.isSecondPerson("  yOu "));
		assertEquals("your", PlayerText.possessive("You"));
		assertEquals("Your", PlayerText.possessiveSubject("You"));
		assertEquals("You are", PlayerText.withPresentVerb("You", "are", "is"));
		assertEquals("You win", PlayerText.withPresentVerb("You", "win", "wins"));
		assertEquals("Your Turn", PlayerText.turnHeading("You"));
	}

	@Test
	void ordinaryNamesKeepThirdPersonFormatting() {
		assertEquals("Alice's", PlayerText.possessive("Alice"));
		assertEquals("Alice's", PlayerText.possessiveSubject("Alice"));
		assertEquals("Alice is", PlayerText.withPresentVerb("Alice", "are", "is"));
		assertEquals("Alice wins", PlayerText.withPresentVerb("Alice", "win", "wins"));
		assertEquals("Alice's Turn", PlayerText.turnHeading("Alice"));
	}
}

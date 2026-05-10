package rules.managers;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class RuleTypeTest
{
	@Test
	void constructorNormalizesIdsAndSupportsValueEquality() {
		RuleType first = new RuleType(" Single_Rule ");
		RuleType second = new RuleType("single_rule");

		assertEquals("single_rule", first.id());
		assertEquals(first, second);
		assertEquals(0, first.compareTo(second));
	}

	@Test
	void constructorRejectsBlankIds() {
		assertThrows(IllegalArgumentException.class, () -> new RuleType(" "));
	}

	@Test
	void compareToOrdersByNormalizedId() {
		assertTrue(new RuleType("alpha").compareTo(new RuleType("beta")) < 0);
	}
}

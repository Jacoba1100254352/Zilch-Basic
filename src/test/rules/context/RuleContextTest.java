package rules.context;


import org.junit.jupiter.api.Test;
import support.TestDoubles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


class RuleContextTest
{
	@Test
	void constructorRejectsNullValues() {
		assertThrows(IllegalArgumentException.class, () -> new RuleContext(null, Map.of(), new HashMap<>()));
		assertThrows(IllegalArgumentException.class, () -> new RuleContext(TestDoubles.player("Jacob"), null, new HashMap<>()));
		assertThrows(IllegalArgumentException.class, () -> new RuleContext(TestDoubles.player("Jacob"), Map.of(), null));
	}

	@Test
	void constructorAcceptsValidContext() {
		assertDoesNotThrow(() -> new RuleContext(TestDoubles.player("Jacob"), Map.of(1, 1), new HashMap<>()));
	}
}

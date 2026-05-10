package rules.managers;


import org.junit.jupiter.api.Test;
import rules.variable.IRule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class RuleRegistryTest
{
	@Test
	void discoversConcreteRuleClassesOnClasspath() {
		RuleRegistry ruleRegistry = new RuleRegistry();

		assertTrue(
				ruleRegistry.getAvailableRules().stream()
				            .anyMatch(rule -> rule.getRuleType().equals(new RuleType("test_auto_loaded"))),
				"Expected a compatible rule class on the classpath to be discovered automatically."
		);
	}

	@Test
	void availableRulesAreSortedByDisplayName() {
		RuleRegistry ruleRegistry = new RuleRegistry();
		List<String> displayNames = ruleRegistry.getAvailableRules().stream().map(IRule::getDisplayName).toList();
		List<String> sortedDisplayNames = new ArrayList<>(displayNames);
		sortedDisplayNames.sort(String::compareTo);

		assertEquals(sortedDisplayNames, displayNames);
	}

	@Test
	void getDefaultConfigReturnsSelectableRuleDefaults() {
		RuleRegistry ruleRegistry = new RuleRegistry();

		Map<RuleType, Object> defaults = ruleRegistry.getDefaultConfig();

		assertEquals(Set.of(1, 5), defaults.get(RuleType.SINGLE));
		assertEquals(3, defaults.get(RuleType.SET));
		assertEquals(50, defaults.get(RuleType.FIRST_ROLL_BUST));
		assertTrue(defaults.containsKey(new RuleType("test_auto_loaded")));
	}

	@Test
	void configureRulesActivatesConfiguredRulesAndRejectsUnknownRules() {
		RuleRegistry ruleRegistry = new RuleRegistry();
		Map<RuleType, Object> config = new LinkedHashMap<>();
		config.put(RuleType.SINGLE, Set.of(1));
		config.put(RuleType.SET, 3);

		ruleRegistry.configureRules(config);

		assertEquals(List.of(RuleType.SINGLE, RuleType.SET), ruleRegistry.getActiveRules().stream().map(IRule::getRuleType).toList());
		assertThrows(IllegalArgumentException.class, () -> ruleRegistry.configureRules(Map.of(new RuleType("missing_rule"), true)));
	}
}

package rules.managers;


import rules.variable.IRule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Registry for all discovered rules and the subset currently enabled for a game.
 * Available rules are loaded once from the classpath; active rules are rebuilt
 * from the user's selected configuration.
 */
public class RuleRegistry implements IRuleRegistry
{
	private final Map<RuleType, IRule> availableRules = new LinkedHashMap<>();
	private final Map<RuleType, IRule> activeRules = new LinkedHashMap<>();

	/**
	 * Creates the registry and discovers all compatible rule classes.
	 */
	public RuleRegistry() {
		loadRules();
	}

	/**
	 * Loads every compatible rule from the variable-rule package and stores it
	 * by rule id for later configuration and lookup.
	 */
	private void loadRules() {
		for (IRule rule : new RuleScanner().discoverRules("rules.variable")) {
			IRule previous = availableRules.put(rule.getRuleType(), rule);
			if (previous != null) {
				throw new IllegalStateException("Duplicate rule type discovered: " + rule.getRuleType());
			}
		}
	}

	@Override
	/**
	 * Activates the selected rules and applies their configuration values.
	 */
	public void configureRules(Map<RuleType, Object> config) {
		activeRules.clear();
		for (Map.Entry<RuleType, Object> entry : config.entrySet()) {
			IRule rule = availableRules.get(entry.getKey());
			if (rule == null) {
				throw new IllegalArgumentException("No rule is registered for " + entry.getKey());
			}
			rule.configure(entry.getValue());
			activeRules.put(entry.getKey(), rule);
		}
	}

	@Override
	/**
	 * Returns the discovered rule associated with the supplied id.
	 */
	public IRule getRule(RuleType ruleType) {
		return availableRules.get(ruleType);
	}

	@Override
	/**
	 * Builds the default setup config shown to the user at game creation time.
	 */
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new LinkedHashMap<>();
		for (IRule rule : availableRules.values()) {
			if (rule.isSelectableAtSetup()) {
				defaultConfig.put(rule.getRuleType(), rule.getDefaultConfig());
			}
		}
		return defaultConfig;
	}

	@Override
	/**
	 * Returns every discovered rule, regardless of whether it is active.
	 */
	public List<IRule> getAvailableRules() {
		return new ArrayList<>(availableRules.values());
	}

	@Override
	/**
	 * Returns the subset of rules currently enabled for this game.
	 */
	public List<IRule> getActiveRules() {
		return new ArrayList<>(activeRules.values());
	}
}

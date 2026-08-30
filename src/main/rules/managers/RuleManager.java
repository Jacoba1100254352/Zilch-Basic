package rules.managers;


import model.entities.GameOption;
import rules.context.RuleContext;
import rules.variable.IRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Facade over the rule registry that evaluates active rules for a turn and
 * delegates option application back to the owning rule strategy.
 */
public class RuleManager implements IRuleManager
{
	private final IRuleRegistry ruleRegistry;

	/**
	 * Creates the rule manager facade over the supplied registry.
	 */
	public RuleManager(IRuleRegistry ruleRegistry) {
		this.ruleRegistry = ruleRegistry;
	}

	@Override
	/**
	 * Activates the selected rules for the current game instance.
	 */
	public void initializeRules(Map<RuleType, Object> config) {
		ruleRegistry.configureRules(config);
	}

	@Override
	/**
	 * Evaluates the current roll against every active rule and returns the
	 * combined list of scoring options.
	 */
	public List<GameOption> evaluateRules(RuleContext context) {
		List<GameOption> gameOptions = new ArrayList<>();
		for (IRule rule : ruleRegistry.getActiveRules()) {
			gameOptions.addAll(rule.evaluate(context));
		}

		Set<Integer> multipleOwnedValues = new HashSet<>();
		for (GameOption option : gameOptions) {
			if ((option.type().equals(RuleType.MULTIPLE) || option.type().equals(RuleType.ADD_MULTIPLE)) &&
					option.selectedValue() != null) {
				multipleOwnedValues.add(option.selectedValue());
			}
		}
		gameOptions.removeIf(option -> option.type().equals(RuleType.SINGLE) &&
				option.selectedValue() != null && multipleOwnedValues.contains(option.selectedValue()));
		return gameOptions;
	}

	@Override
	/**
	 * Returns the discovered rule associated with the supplied id.
	 */
	public IRule getRule(RuleType ruleType) {
		return ruleRegistry.getRule(ruleType);
	}

	@Override
	/**
	 * Applies the selected option by delegating to the rule that created it.
	 */
	public void applyRule(RuleContext context, GameOption option) {
		IRule rule = getRule(option.type());
		if (rule == null) {
			throw new IllegalArgumentException("No rule found for option type " + option.type());
		}
		rule.apply(context, option);
	}

	@Override
	/**
	 * Returns all rules discovered by the underlying registry.
	 */
	public List<IRule> getAvailableRules() {
		return ruleRegistry.getAvailableRules();
	}

	@Override
	/**
	 * Returns whether the supplied rule is active for the current game.
	 */
	public boolean isRuleActive(RuleType ruleType) {
		return ruleRegistry.getActiveRules()
		                   .stream()
		                   .anyMatch(rule -> rule.getRuleType().equals(ruleType));
	}
}

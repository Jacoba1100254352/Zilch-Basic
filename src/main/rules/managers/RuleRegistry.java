package rules.managers;


import rules.models.*;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static rules.managers.RuleType.*;


public class RuleRegistry implements IRuleRegistry
{
	private final Map<String, Map<RuleType, IRuleStrategy>> gameRules = new ConcurrentHashMap<>();
	
	public RuleRegistry() {
		// Initialize rules for the default game
		initializeRulesForGame("default", 3, 3, Set.of(1, 5), 3, 5);
	}
	
	@Override
	public void initializeRulesForGame(
			String gameId, Integer addMultipleMin, Integer multipleMin, Set<Integer> singleValues, Integer setMin, Integer numStraitValues
	) {
		Map<RuleType, IRuleStrategy> rules = new EnumMap<>(RuleType.class);
		rules.put(ADD_MULTIPLE, new AddMultipleRule(addMultipleMin));
		rules.put(MULTIPLE, new MultipleRule(multipleMin));
		rules.put(SINGLE, new SingleRule(singleValues));
		rules.put(SET, new SetRule(setMin));
		rules.put(STRAIT, new StraitRule(numStraitValues));
		gameRules.put(gameId, rules);
	}
	
	@Override
	public IRuleStrategy getRule(String gameId, RuleType ruleType) {
		Map<RuleType, IRuleStrategy> rules = gameRules.get(gameId);
		if (rules != null) {
			return rules.get(ruleType);
		}
		return null;  // Or throw an exception if the game ID is not found or rule does not exist
	}
}

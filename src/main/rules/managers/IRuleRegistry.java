package rules.managers;


import rules.models.IRuleStrategy;

import java.util.Set;


public interface IRuleRegistry
{
	// TODO: Add initialization enforcement somewhere
	void initializeRulesForGame(
			String gameId, Integer addMultipleMin, Integer multipleMin, Set<Integer> singleValues, Integer setMin, Integer numStraitValues
	);
	
	IRuleStrategy getRule(String gameId, RuleType ruleType);
}
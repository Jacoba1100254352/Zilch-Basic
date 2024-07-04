package rules.models;


import rules.managers.RuleType;

import java.util.Map;


public interface IRuleStrategy
{
	/**
	 * This gives the rule message to be sent or printed
	 *
	 * @return The rule description
	 */
	String getDescription();
	
	boolean isValid(Map<Integer, Integer> diceSetMap, Integer value);
	
	void configure(Map<RuleType, Object> config);
	
	Map<RuleType, Object> getDefaultConfig(); // Method to provide default config values
	
	RuleType getRuleType(); // Method to get the rule type
}

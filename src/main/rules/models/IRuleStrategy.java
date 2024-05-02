package rules.models;


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
}

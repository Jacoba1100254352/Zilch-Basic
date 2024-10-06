package rules.variable;


import java.util.Map;


public interface IRuleStrategy
{
	/**
	 * Choose to apply or de-apply the rule
	 */
	void apply(boolean applyRule);
	
	/**
	 * Has the user selected to apply this rule.
	 * Returns `true` by default.
	 *
	 * @return if the user chose to apply this rule
	 */
	boolean isApplied(); // isSelected
	
	/**
	 * This gives the rule message to be sent or printed
	 *
	 * @return The rule description
	 */
	String getDescription();
	
	boolean isValid(Map<Integer, Integer> diceSetMap, Integer value);
}

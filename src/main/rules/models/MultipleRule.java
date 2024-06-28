package rules.models;


import java.util.Map;


public class MultipleRule implements IRuleStrategy
{
	Integer minimumMultiples;
	
	/**
	 * @note Applies the rule by default and assigns value a default of 3
	 */
	@SuppressWarnings("unused")
	public MultipleRule() {
		this(3);
	}
	
	/**
	 * @param minimumMultiples Minimum number of multiples before this rule becomes an option
	 *
	 * @note Applies the rule by default
	 */
	public MultipleRule(Integer minimumMultiples) {
		this(minimumMultiples, true);
	}
	
	/**
	 * @param minimumMultiples Minimum number before this rule becomes an option
	 * @param applyRule        Determines whether this rule may be applied
	 */
	public MultipleRule(Integer minimumMultiples, Boolean applyRule) {
		this.minimumMultiples = minimumMultiples;
	}
	
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		// return diceSetMap.getOrDefault(value, 0) >= 3 && diceSetMap.values().stream().anyMatch(count -> count >= 3);
		return diceSetMap.getOrDefault(value, 0) >= minimumMultiples;
	}
}

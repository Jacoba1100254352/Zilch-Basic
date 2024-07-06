package rules.variableModels;


import java.util.Map;


// FIXME: This Rule needs to be adjusted
public class AddMultipleRule implements IRuleStrategy
{
	Integer value;
	Boolean isApplied;
	
	/**
	 * @note Applies the rule and assigns value a default of 3
	 */
	@SuppressWarnings("unused")
	public AddMultipleRule() {
		this(3);
	}
	
	/**
	 * @param value Minimum number before this rule becomes an option
	 *
	 * @note Applies the rule by default
	 */
	public AddMultipleRule(Integer value) {
		this(value, true);
	}
	
	
	/**
	 * @param value Minimum number before this rule becomes an option
	 * @param applyRule Determines whether this rule may be applied
	 */
	public AddMultipleRule(Integer value, Boolean applyRule) {
		this.value = value;
		this.isApplied = applyRule;
	}
	
	@Override
	public void apply(boolean applyRule) {
		this.isApplied = applyRule;
	}
	
	@Override
	public boolean isApplied() {
		return this.isApplied;
	}
	
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		return diceSetMap.getOrDefault(value, 0) >= value;
	}
}

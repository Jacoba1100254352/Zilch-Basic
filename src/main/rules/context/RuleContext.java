package rules.context;

import java.util.Map;


public class RuleContext
{
	private final Map<Integer, Integer> diceSetMap;
	private final Integer value;
	
	public RuleContext(Map<Integer, Integer> diceSetMap, Integer value) {
		if (diceSetMap == null) {
			throw new IllegalArgumentException("diceSetMap cannot be null.");
		}
		this.diceSetMap = diceSetMap;
		this.value = value;
	}
	
	public RuleContext(Map<Integer, Integer> diceSetMap) {
		this(diceSetMap, null);
	}
	
	public Map<Integer, Integer> getDiceSetMap() {
		return diceSetMap;
	}
	
	public Integer getValue() {
		return value;
	}
}

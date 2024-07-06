package rules.context;


import java.util.Map;


public record RuleContext(Map<Integer, Integer> diceSetMap, Integer value)
{
	public RuleContext {
		if (diceSetMap == null) {
			throw new IllegalArgumentException("diceSetMap cannot be null.");
		}
	}
	
	public RuleContext(Map<Integer, Integer> diceSetMap) {
		this(diceSetMap, null);
	}
}

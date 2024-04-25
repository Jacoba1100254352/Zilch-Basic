package rules;


import abstracts.AbstractRule;

import java.util.Map;


public class SetRule extends AbstractRule
{
	public SetRule(Integer value) {
		super(value);
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		return (diceSetMap.size() == 3 && !new StraitRule().isValid(diceSetMap) && !new MultipleRule().isValid(diceSetMap)) &&
				diceSetMap.values().stream().allMatch(count -> count == 2);
	}
}

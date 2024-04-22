package rules;


import interfaces.IRule;

import java.util.Map;


public class SetRule implements IRule
{
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		return (diceSetMap.size() == 3 && !new StraitRule().isValid(diceSetMap) && !new MultipleRule().isValid(diceSetMap)) &&
				diceSetMap.values().stream().allMatch(count -> count == 2);
	}
}

package rules;


import abstracts.AbstractRule;

import java.util.Map;

import static models.Dice.FULL_SET_OF_DICE;


public class StraitRule extends AbstractRule
{
	public StraitRule(Integer value) {
		super(value);
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		if (diceSetMap.size() != FULL_SET_OF_DICE) {
			return false;
		}
		
		for (int i = 1; i <= FULL_SET_OF_DICE; i++) {
			if (!diceSetMap.containsKey(i) || diceSetMap.get(i) != 1) {
				return false;
			}
		}
		
		return true;
	}
}

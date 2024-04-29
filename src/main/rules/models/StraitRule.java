package rules.models;


import java.util.Map;

import static model.entities.Dice.FULL_SET_OF_DICE;


public class StraitRule implements IRuleStrategy
{
	Integer value;
	
	public StraitRule(Integer value) {
		this.value = value;
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
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

package types;


import abstracts.AbstractDieType;

import java.util.Map;


public class Multiples extends AbstractDieType
{
	public Multiples(int value) {
		super(value);
	}
	
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		return diceSetMap.getOrDefault(value, 0) >= 3;
	}
	
	public int getCount(Map<Integer, Integer> diceSetMap) {
		return diceSetMap.getOrDefault(value, 0);
	}
}

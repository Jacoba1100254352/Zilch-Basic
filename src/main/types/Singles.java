package types;


import abstracts.AbstractDieType;

import java.util.Map;


public class Singles extends AbstractDieType
{
	public Singles(int value) {
		super(value);
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		return value == 1 || value == 5;
	}
}

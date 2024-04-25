package abstracts;


import interfaces.IRuleStrategy;

import java.util.Map;


public abstract class AbstractRule extends AbstractDieType implements IRuleStrategy
{
	public AbstractRule(Integer value) {
		super(value);
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		return false;
	}
}
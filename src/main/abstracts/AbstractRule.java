package abstracts;

import interfaces.IRuleStrategy;
import java.util.Map;

public abstract class AbstractRule implements IRuleStrategy {
	protected Integer value;
	
	public AbstractRule() {
		this.value = null;
	}
	
	public AbstractRule(Integer value) {
		this.value = value;
	}
	
	@Override
	public void setValue(Integer value) {
		this.value = value;
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap) {
		return false;
	}
	
	@Override
	public boolean isValid(Map<Integer, Integer> diceSetMap, Integer value) {
		return false;
	}
}
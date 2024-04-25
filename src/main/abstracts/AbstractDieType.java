package abstracts;


import java.util.Map;


public abstract class AbstractDieType
{
	protected static Map<Integer, Integer> diceSetMap = null;
	protected Integer value;
	
	public AbstractDieType(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	
	public void setValue(Integer value) {
		if (diceSetMap == null) {
			throw new RuntimeException("Dice set map not set.");
		}
		
		this.value = value;
		
		if (isValid(diceSetMap)) {
			throw new RuntimeException("Invalid value passed.");
		}
	}
	
	abstract public boolean isValid(Map<Integer, Integer> diceSetMap);
}

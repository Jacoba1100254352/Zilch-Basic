package interfaces;


import java.util.Map;


public interface IRuleStrategy
{
	boolean isValid(Map<Integer, Integer> diceSetMap, Integer value);
}

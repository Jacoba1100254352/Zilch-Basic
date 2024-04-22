package interfaces;


import java.util.Map;


public interface IRule
{
	boolean isValid(Map<Integer, Integer> diceSetMap);
}

package ruleManagers;


import java.util.Map;


public interface Rule
{
	boolean isValid(Map<Integer, Integer> diceSetMap);
}

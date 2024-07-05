package rules.models;


import java.util.Map;


public interface IRuleValidation
{
	boolean isValid(Map<Integer, Integer> diceSetMap);
}


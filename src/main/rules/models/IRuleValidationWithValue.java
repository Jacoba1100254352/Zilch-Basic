package rules.models;


import java.util.Map;


public interface IRuleValidationWithValue extends IRuleValidation
{
	boolean isValid(Map<Integer, Integer> diceSetMap, Integer value);
}

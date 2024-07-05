package rules.context;


import java.util.Map;


public interface IRuleContext
{
	Map<Integer, Integer> getDiceSetMap();
	
	Integer getValue();
}

package rules.managers;


import model.entities.GameOption;
import model.entities.Player;

import java.util.List;
import java.util.Set;


public interface IRuleManager
{
	// FIXME: Check to see if this is truly the best way
	void initializeRules(
			String gameId, Integer addMultipleMin, Integer multipleMin, Set<Integer> singleValues, Integer setMin, Integer numStraitValues
	);
	
	List<GameOption> evaluateRules(Player player, int numOptionsSelected);
}

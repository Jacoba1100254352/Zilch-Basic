package rules.managers;


import model.entities.GameOption;
import model.entities.Player;

import java.util.List;
import java.util.Map;


public interface IRuleManager
{
	void initializeRules(Map<RuleType, Object> config);
	
	List<GameOption> evaluateRules(Player player, int numOptionsSelected);
}

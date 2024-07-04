package rules.managers;


import model.entities.GameOption;
import model.entities.Player;
import rules.config.RulesConfig;

import java.util.List;


public interface IRuleManager
{
	void initializeRules(RulesConfig config);
	
	List<GameOption> evaluateRules(Player player, int numOptionsSelected);
}

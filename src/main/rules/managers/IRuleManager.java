package rules.managers;


import model.entities.GameOption;
import model.entities.Player;
import model.managers.ActionManager;
import rules.models.IRule;

import java.util.List;
import java.util.Map;


public interface IRuleManager
{
	void initializeRules(Map<RuleType, Object> config);
	
	List<GameOption> evaluateRules(Player player, int numOptionsSelected);
	
	IRule getRule(RuleType ruleType);
	
	void applyRule(ActionManager actionManager, GameOption option, Player player);
}

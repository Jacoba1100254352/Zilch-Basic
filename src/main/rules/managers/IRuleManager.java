package rules.managers;


import model.entities.GameOption;
import model.entities.Player;
import rules.variableModels.IRule;

import java.util.List;
import java.util.Map;


public interface IRuleManager
{
	void initializeRules(Map<RuleType, Object> config);
	
	List<GameOption> evaluateRules(Map<Integer, Integer> diceSetMap, Integer value);
	
	IRule getRule(RuleType ruleType);
	
	void applyRule(Player player, GameOption option);
}

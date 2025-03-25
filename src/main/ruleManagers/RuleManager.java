package ruleManagers;


import managers.GameCoordinator;
import modelManagers.PlayerManager;

import java.util.Map;


public class RuleManager
{
	private final GameCoordinator gameCoordinator;
	
	public RuleManager(GameCoordinator gameCoordinator) {
		this.gameCoordinator = gameCoordinator;
	}
	
	public boolean isOptionAvailable() {
		PlayerManager playerManager = gameCoordinator.getPlayerManager();
		Map<Integer, Integer> diceSetMap = playerManager.getDice(playerManager.getCurrentPlayer());
		
		return (isRuleValid(new StraitRule(), diceSetMap) || isRuleValid(new SetRule(), diceSetMap) || isRuleValid(new AddMultipleRule(null, gameCoordinator.getGameOptionManager().getPreviouslySelectedMultipleValue()), diceSetMap) || isRuleValid(new MultipleRule(), diceSetMap) || isRuleValid(new SingleRule(1), diceSetMap) || isRuleValid(new SingleRule(5), diceSetMap));
	}
	
	public boolean isRuleValid(Rule rule, Map<Integer, Integer> diceSetMap) {
		return rule.isValid(diceSetMap);
	}
}

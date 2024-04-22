package ruleManagers;


import interfaces.IRule;
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
		
		return (
				isRuleValid(RuleType.STRAIT, diceSetMap, null) ||
						isRuleValid(RuleType.SET, diceSetMap, null) ||
						isRuleValid(RuleType.MULTIPLE, diceSetMap, null) ||
						isRuleValid(RuleType.SINGLE, diceSetMap, 1) ||
						isRuleValid(RuleType.SINGLE, diceSetMap, 5)
		);
	}
	
	public boolean isRuleValid(RuleType ruleType, Map<Integer, Integer> diceSetMap, Integer value) {
		IRule rule = RuleFactory.getRule(ruleType, value);
		return rule.isValid(diceSetMap);
	}
}

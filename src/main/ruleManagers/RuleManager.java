package ruleManagers;

import interfaces.IRuleStrategy;
import managers.GameCoordinator;
import modelManagers.PlayerManager;
import rules.*;

import java.util.Map;

public class RuleManager {
	private final GameCoordinator gameCoordinator;
	
	public RuleManager(GameCoordinator gameCoordinator) {
		this.gameCoordinator = gameCoordinator;
	}
	
	public boolean isOptionAvailable() {
		PlayerManager playerManager = gameCoordinator.getPlayerManager();
		Map<Integer, Integer> diceSetMap = playerManager.getDice(playerManager.getCurrentPlayer());
		
		return (
				isRuleValid(new StraitRule(), diceSetMap, null) ||
						isRuleValid(new SetRule(), diceSetMap, null) ||
						isRuleValid(new MultipleRule(), diceSetMap, null) ||
						isRuleValid(new SingleRule(1), diceSetMap, 1) ||
						isRuleValid(new SingleRule(5), diceSetMap, 5)
		);
	}
	
	public boolean isRuleValid(IRuleStrategy rule, Map<Integer, Integer> diceSetMap) {
		return rule.isValid(diceSetMap);
	}
	
	public boolean isRuleValid(IRuleStrategy rule, Map<Integer, Integer> diceSetMap, Integer value) {
		return rule.isValid(diceSetMap, value);
	}
}

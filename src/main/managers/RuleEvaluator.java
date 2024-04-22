package managers;


import models.GameOption;
import models.Player;
import ruleManagers.RuleManager;
import ruleManagers.RuleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static models.Dice.FULL_SET_OF_DICE;


public class RuleEvaluator
{
	private final GameCoordinator gameCoordinator;
	
	public RuleEvaluator(GameCoordinator gameCoordinator) {
		this.gameCoordinator = gameCoordinator;
	}
	
	public List<GameOption> evaluateRules(Player player) {
		List<GameOption> gameOptions = new ArrayList<>();
		Map<Integer, Integer> diceSetMap = gameCoordinator.getPlayerManager().getDice(player);
		RuleManager ruleManager = gameCoordinator.getRuleManager();
		
		if (ruleManager.isRuleValid(RuleType.STRAIT, diceSetMap, null)) {
			gameOptions.add(new GameOption(GameOption.Type.STRAIT, null));
		}
		if (ruleManager.isRuleValid(RuleType.SET, diceSetMap, null)) {
			gameOptions.add(new GameOption(GameOption.Type.SET, null));
		}
		
		for (int dieValue = 1; dieValue <= FULL_SET_OF_DICE; dieValue++) {
			if (ruleManager.isRuleValid(RuleType.MULTIPLE, diceSetMap, dieValue) ||
					ruleManager.isRuleValid(RuleType.ADD_MULTIPLE, diceSetMap, dieValue)) {
				gameOptions.add(new GameOption(GameOption.Type.MULTIPLE, dieValue));
			}
			if (dieValue == 1 || dieValue == 5) {
				if (ruleManager.isRuleValid(RuleType.SINGLE, diceSetMap, dieValue)) {
					gameOptions.add(new GameOption(GameOption.Type.SINGLE, dieValue));
				}
			}
		}
		
		return gameOptions;
	}
}

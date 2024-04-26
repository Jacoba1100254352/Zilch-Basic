package ruleManagers;


import models.GameOption;
import models.Player;
import rules.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Unified manager for rule evaluation and management.
 */
public class RuleManager
{
	
	/**
	 * Evaluates the available game options based on the current dice state of a player.
	 *
	 * @param player The player whose dice are to be evaluated.
	 *
	 * @return A list of valid game options for the player.
	 */
	public List<GameOption> evaluateRules(Player player) {
		List<GameOption> gameOptions = new ArrayList<>();
		Map<Integer, Integer> diceSetMap = player.dice().diceSetMap();
		
		if (isRuleValid(RuleType.STRAIT, diceSetMap, null)) {
			gameOptions.add(new GameOption(GameOption.Type.STRAIT, null));
		}
		if (isRuleValid(RuleType.SET, diceSetMap, null)) {
			gameOptions.add(new GameOption(GameOption.Type.SET, null));
		}
		
		for (int dieValue = 1; dieValue <= 6; dieValue++) { // Assuming a standard set of 6-sided dice
			if (isRuleValid(RuleType.MULTIPLE, diceSetMap, dieValue) ||
					isRuleValid(RuleType.ADD_MULTIPLE, diceSetMap, dieValue)) {
				gameOptions.add(new GameOption(GameOption.Type.MULTIPLE, dieValue));
			}
			if (dieValue == 1 || dieValue == 5) {
				if (isRuleValid(RuleType.SINGLE, diceSetMap, dieValue)) {
					gameOptions.add(new GameOption(GameOption.Type.SINGLE, dieValue));
				}
			}
		}
		
		// Option to roll again if a score has been made and an option selected in the current roll.
		if (player.score().getRoundScore() > 0 && isOptionSelectedForCurrentRoll) {
			gameOptions.add(new GameOption(GameOption.Type.ROLL_AGAIN, null));
		}
		
		// Option to end turn if the score exceeds 1000.
		if (player.score().getPermanentScore() >= 1000 || player.score().getRoundScore() >= 1000) {
			gameOptions.add(new GameOption(GameOption.Type.END_TURN, null));
		}
		
		return gameOptions;
	}
	
	/**
	 * Checks if a given rule is valid for the specified dice configuration.
	 *
	 * @param ruleType   The type of rule to check.
	 * @param diceSetMap A map of dice values to their counts.
	 * @param value      An optional value needed for some rules, like MULTIPLE.
	 *
	 * @return true if the rule is valid, false otherwise.
	 */
	public boolean isRuleValid(RuleType ruleType, Map<Integer, Integer> diceSetMap, Integer value) {
		return switch (ruleType) {
			case STRAIT -> new StraitRule(value).isValid(diceSetMap);
			case SET -> new SetRule(value).isValid(diceSetMap);
			case MULTIPLE -> new MultipleRule(value).isValid(diceSetMap, value);
			case ADD_MULTIPLE -> new AddMultipleRule(value).isValid(diceSetMap, value);
			case SINGLE -> new SingleRule(value).isValid(diceSetMap, value);
		};
	}
}

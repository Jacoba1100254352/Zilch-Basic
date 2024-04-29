package rules.managers;


import model.entities.GameOption;
import model.entities.Player;
import model.managers.AbstractManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static rules.managers.RuleType.*;


/**
 * Unified manager for rule evaluation and management.
 */
public class RuleManager extends AbstractManager implements IRuleManager
{
	private final IRuleRegistry ruleRegistry;
	private final String gameId;
	
	public RuleManager(IRuleRegistry ruleRegistry, String gameId) {
		this.ruleRegistry = ruleRegistry;
		this.gameId = gameId;
	}
	
	/**
	 * Evaluates the available game options based on the current dice state of a player.
	 *
	 * @param player The player whose dice are to be evaluated.
	 *
	 * @return A list of valid game options for the player.
	 */
	@Override
	public List<GameOption> evaluateRules(Player player, boolean isOptionSelectedForCurrentRoll) {
		List<GameOption> gameOptions = new ArrayList<>();
		Map<Integer, Integer> diceSetMap = player.dice().diceSetMap();
		
		if (ruleRegistry.getRule(gameId, STRAIT).isValid(diceSetMap, null)) {
			gameOptions.add(new GameOption(STRAIT, null));
		}
		if (ruleRegistry.getRule(gameId, SET).isValid(diceSetMap, null)) {
			gameOptions.add(new GameOption(SET, null));
		}
		
		for (int dieValue = 1; dieValue <= 6; dieValue++) {
			if (ruleRegistry.getRule(gameId, MULTIPLE).isValid(diceSetMap, dieValue)) {
				gameOptions.add(new GameOption(MULTIPLE, dieValue));
			}
			if (ruleRegistry.getRule(gameId, SINGLE).isValid(diceSetMap, dieValue)) {
				gameOptions.add(new GameOption(SINGLE, dieValue));
			}
		}
		
		
		// TODO: These should probably be their own rule to follow the above pattern
		// Option to roll again if a score has been made and an option selected in the current roll.
		if (player.score().getRoundScore() > 0 && isOptionSelectedForCurrentRoll) {
			gameOptions.add(new GameOption(ROLL_AGAIN, null));
		}
		
		// Option to end turn if the score exceeds 1000.
		if (player.score().getPermanentScore() >= 1000 || player.score().getRoundScore() >= 1000) {
			gameOptions.add(new GameOption(END_TURN, null));
		}
		
		return gameOptions;
	}
	
	@Override
	protected void doInitialize() {
	
	}
}

package rules.managers;


import model.entities.GameOption;
import model.entities.Player;
import rules.models.IConstantRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static rules.managers.RuleType.*;


/**
 * Unified manager for rule evaluation and management.
 */
public class RuleManager implements IRuleManager
{
	private final IRuleRegistry ruleRegistry;
	private final String gameId;
	
	@SuppressWarnings("unused")
	public RuleManager(String gameId) {
		this(new RuleRegistry(), gameId);
	}
	
	public RuleManager(IRuleRegistry ruleRegistry, String gameId) {
		this.ruleRegistry = ruleRegistry;
		this.gameId = gameId;
	}
	
	/**
	 * Initializes the rules for a game.
	 *
	 * @param gameId          The ID of the game to initialize rules for.
	 * @param addMultipleMin  The minimum number of dice required to add to a multiple.
	 * @param multipleMin     The minimum number of dice required to be the same value.
	 * @param singleValues    The set of values that can be scored as singles.
	 * @param setMin          The minimum number of dice required to be the same value.
	 * @param numStraitValues The number of values required to be in a strait.
	 */
	@Override
	public void initializeRules(
			String gameId, Integer addMultipleMin, Integer multipleMin, Set<Integer> singleValues, Integer setMin, Integer numStraitValues
	) {
		ruleRegistry.initializeRulesForGame(gameId, addMultipleMin, multipleMin, singleValues, setMin, numStraitValues);
	}
	
	/**
	 * Evaluates the available game options based on the current dice state of a player.
	 *
	 * @param player The player whose dice are to be evaluated.
	 *
	 * @return A list of valid game options for the player.
	 */
	@Override
	public List<GameOption> evaluateRules(Player player, int numOptionsSelected) {
		List<GameOption> gameOptions = new ArrayList<>();
		Map<Integer, Integer> diceSetMap = player.dice().diceSetMap();
		
		if (ruleRegistry.getRule(gameId, STRAIT).isValid(diceSetMap, null)) {
			gameOptions.add(new GameOption(STRAIT, null, "Score a Strait"));
		}
		if (ruleRegistry.getRule(gameId, SET).isValid(diceSetMap, null)) {
			gameOptions.add(new GameOption(SET, null, "Score a Set"));
		}
		
		for (int dieValue = 1; dieValue <= 6; dieValue++) {
			if (ruleRegistry.getRule(gameId, ADD_MULTIPLE).isValid(diceSetMap, dieValue)) {
				gameOptions.add(new GameOption(ADD_MULTIPLE, dieValue, "Score Added Multiple " + dieValue));
			}
			if (ruleRegistry.getRule(gameId, MULTIPLE).isValid(diceSetMap, dieValue)) {
				gameOptions.add(new GameOption(MULTIPLE, dieValue, "Score Multiple " + dieValue));
			}
			if (ruleRegistry.getRule(gameId, SINGLE).isValid(diceSetMap, dieValue)) {
				gameOptions.add(new GameOption(SINGLE, dieValue, "Score Single " + dieValue));
			}
		}
		
		
		// Option to roll again if a score has been made and an option selected in the current roll.
		if (((IConstantRule) ruleRegistry.getRule(gameId, ROLL_AGAIN)).isValid(player.score().getRoundScore(), numOptionsSelected)) {
			gameOptions.add(new GameOption(ROLL_AGAIN, null, "Roll Again"));
		}
		
		// Option to end turn if the score exceeds 1000.
		if (((IConstantRule) ruleRegistry.getRule(gameId, END_TURN)).isValid(player.score().getPermanentScore(), player.score().getRoundScore())) {
			gameOptions.add(new GameOption(END_TURN, null, "End Turn"));
		}
		
		return gameOptions;
	}
}

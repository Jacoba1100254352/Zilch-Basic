package model.managers;


import model.entities.GameOption;
import rules.context.RuleContext;
import rules.managers.IRuleManager;
import rules.managers.RuleType;
import rules.variable.IRule;

import java.util.ArrayList;
import java.util.List;


/**
 * Stores the currently available scoring options for a turn and remembers the
 * option chosen by the player until it is applied.
 */
public class GameOptionManager
{
	private final IRuleManager ruleManager;
	private final List<GameOption> gameOptions = new ArrayList<>();
	private GameOption selectedGameOption;

	/**
	 * Creates an option manager backed by the supplied rule manager.
	 */
	public GameOptionManager(IRuleManager ruleManager) {
		this.ruleManager = ruleManager;
	}

	/**
	 * Returns whether a concrete option has been selected for application.
	 */
	public boolean isValid() {
		return this.selectedGameOption != null;
	}

	/**
	 * Rebuilds the turn's option list from the active rule set.
	 */
	public void evaluateGameOptions(RuleContext ruleContext) {
		gameOptions.clear();
		gameOptions.addAll(ruleManager.evaluateRules(ruleContext));
	}

	/**
	 * Returns whether a non-option game rule is active for this game.
	 */
	public boolean isRuleActive(RuleType ruleType) {
		return ruleManager.isRuleActive(ruleType);
	}

	/**
	 * Returns the discovered rule object for rule-specific configuration.
	 */
	public IRule getRule(RuleType ruleType) {
		return ruleManager.getRule(ruleType);
	}

	/**
	 * Applies either the provided option or the previously selected option.
	 */
	public void applyGameOption(RuleContext ruleContext, GameOption gameOption) {
		GameOption optionToApply = gameOption == null ? this.selectedGameOption : gameOption;
		if (optionToApply == null) {
			throw new IllegalStateException("No game option has been selected.");
		}
		this.selectedGameOption = optionToApply;
		ruleManager.applyRule(ruleContext, optionToApply);
	}

	/**
	 * Returns a defensive copy of the current turn's generated options.
	 */
	public List<GameOption> getGameOptions() {
		return new ArrayList<>(gameOptions);
	}

	/**
	 * Returns the option currently selected by the player, if any.
	 */
	public GameOption getSelectedGameOption() {
		return selectedGameOption;
	}

	/**
	 * Stores the option selected by the player for later application.
	 */
	public void setSelectedGameOption(GameOption gameOption) {
		this.selectedGameOption = gameOption;
	}
}

package rules.variable;


import model.entities.Dice;
import model.entities.GameOption;
import rules.context.RuleContext;
import rules.managers.RuleType;

import java.util.Map;


/**
 * Template base class for selectable scoring rules.
 * Shared metadata, option construction, and dice-consumption behavior live
 * here so concrete rules only implement scoring-specific logic.
 */
public abstract class AbstractVariableRule implements IVariableRule
{
	private final RuleType ruleType;
	private final String displayName;
	private final String description;

	protected AbstractVariableRule(RuleType ruleType, String displayName, String description) {
		this.ruleType = ruleType;
		this.displayName = displayName;
		this.description = description;
	}

	protected abstract void setConfigValue(Object value);

	@Override
	public void configure(Object configValue) {
		setConfigValue(configValue == null ? getDefaultConfig() : configValue);
	}

	@Override
	public RuleType getRuleType() {
		return ruleType;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	protected GameOption buildOption(Integer selectedValue, int pointsAwarded, Map<Integer, Integer> consumedDice) {
		return new GameOption(ruleType, displayName, description, selectedValue, pointsAwarded, consumedDice);
	}

	@Override
	/**
	 * Applies the scored option by updating the round score and consuming the
	 * dice listed by the option. Subclasses can extend this via {@code afterApply}.
	 */
	public void apply(RuleContext context, GameOption option) {
		context.player().score().increaseRoundScore(option.pointsAwarded());
		consumeDice(context.player().dice(), option.consumedDice());
		afterApply(context, option);
	}

	/**
	 * Hook for rules that need additional per-turn bookkeeping after an option
	 * has been applied, such as tracking previously scored multiples.
	 */
	protected void afterApply(RuleContext context, GameOption option) {
	}

	private void consumeDice(Dice dice, Map<Integer, Integer> consumedDice) {
		for (Map.Entry<Integer, Integer> entry : consumedDice.entrySet()) {
			int dieValue = entry.getKey();
			int remaining = dice.getDiceSetMap().getOrDefault(dieValue, 0) - entry.getValue();
			if (remaining > 0) {
				dice.getDiceSetMap().put(dieValue, remaining);
			} else {
				dice.getDiceSetMap().remove(dieValue);
			}
		}
		dice.calculateNumDiceInPlay();
	}
}

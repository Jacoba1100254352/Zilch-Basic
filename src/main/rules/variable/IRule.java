package rules.variable;


import model.entities.GameOption;
import rules.context.RuleContext;
import rules.managers.RuleType;

import java.util.List;


/**
 * Core rule strategy contract. Implementations are responsible for:
 * discovering whether they can score the current roll, exposing concrete
 * options to the user, and applying the chosen option.
 */
public interface IRule
{
	RuleType getRuleType();

	String getDisplayName();

	String getDescription();

	default boolean isSelectableAtSetup() {
		return true;
	}

	void configure(Object configValue);

	Object getDefaultConfig();

	List<GameOption> evaluate(RuleContext context);

	void apply(RuleContext context, GameOption option);
}

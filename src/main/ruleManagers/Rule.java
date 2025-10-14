package ruleManagers;


import models.GameOption;

import java.util.List;


public interface Rule
{
        /**
         * Unique identifier for this rule.
         */
        String id();

        /**
         * Human readable name for this rule (used when toggling rules on/off).
         */
        String displayName();

        /**
         * Determine the set of options made available by this rule for the current context.
         */
        List<GameOption> evaluateOptions(RuleContext context);

        /**
         * Apply the effects of the rule for the selected option.
         */
        void apply(RuleContext context, GameOption option);

        /**
         * Provide a description for the given option to display in the UI.
         */
        String describe(GameOption option);
}

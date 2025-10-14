package ruleManagers;

import models.GameOption;

import java.util.List;

/**
 * Strategy interface describing a scoring rule that can be enabled or disabled at runtime.
 */
public interface RuleStrategy
{
        /** Identifier used to enable/disable the strategy. */
        String id();

        /** Human readable name of the rule. */
        String displayName();

        /** Optional description of the rule shown alongside available options. */
        default String description()
        {
                return "";
        }

        /** Priority used to control ordering of options when multiple strategies are available. */
        default int priority()
        {
                return 0;
        }

        /**
         * Evaluate the current game state and produce zero or more selectable options for the rule.
         *
         * @param context Provides access to the current game state.
         * @return Available options for the rule.
         */
        List<GameOption> evaluateOptions(RuleContext context);

        /**
         * Applies the side effects of the selected option.
         *
         * @param option  The option chosen by the player.
         * @param context Provides access to the current game state.
         */
        void apply(GameOption option, RuleContext context);
}

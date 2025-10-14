package ruleManagers;

import models.GameOption;

import java.util.List;

/**
 * Strategy for both generating game options and applying the associated scoring logic.
 */
public interface Rule
{
        /** Unique identifier used when selecting rules. */
        String id();

        /** Human friendly name shown to players. */
        String displayName();

        /** Whether the rule is enabled by default. */
        default boolean enabledByDefault() {
                return true;
        }

        /** Type of {@link GameOption} produced by this rule. */
        GameOption.Type optionType();

        /**
         * Builds the list of available options for the current game state.
         *
         * @param context contextual information about the current roll.
         * @return the list of options produced by this rule.
         */
        List<GameOption> evaluate(RuleContext context);

        /**
         * Applies the game logic associated with the selected option.
         *
         * @param option  the option that was selected by the player.
         * @param context contextual information about the current roll.
         */
        void apply(GameOption option, RuleContext context);
}

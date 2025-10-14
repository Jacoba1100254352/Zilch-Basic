package ui;


import ruleManagers.RuleDescriptor;

import java.util.List;


/**
 * Abstraction for retrieving user input (or simulating it in tests).
 */
public interface UserInputHandler
{
	
	/**
	 * Asks for and returns the desired score limit.
	 */
	int getValidScoreLimit();
	
	/**
	 * Asks for and returns the list of player names.
	 */
	List<String> getPlayerNames();
	
	/**
	 * Prompts the player to pick one of the current game options (or end turn, reroll, etc.).
	 */
        void inputGameOption();

        /**
         * Pauses until the user presses Enter (in console mode),
         * or does nothing in test mode.
         */
        void pauseAndContinue();

        /**
         * Allows the player to choose which scoring rules are active for the game.
         *
         * @param availableRules descriptors of the rules that can be enabled.
         * @return the identifiers of the rules that should remain enabled.
         */
        List<String> selectEnabledRules(List<RuleDescriptor> availableRules);
}

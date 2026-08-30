package ui;


import model.entities.GameOption;
import model.entities.Player;
import model.entities.TurnContinuation;
import rules.managers.RuleType;

import java.util.List;
import java.util.Map;


public interface IUserInteraction
{
	int getNumberOfPlayers();

	List<String> getPlayerNames(int numPlayers);

	int getValidScoreLimit();

	int getValidOpeningScoreLimit(int scoreLimit);

	Map<RuleType, Object> selectRules();

	GameOption chooseGameOption(Player currentPlayer, List<GameOption> gameOptions);

	/**
	 * Asks whether the player wants to apply another scoring option from the
	 * current physical roll. Existing interaction implementations retain the
	 * former one-option behavior unless they opt in by overriding this method.
	 */
	default boolean shouldScoreMore(Player currentPlayer, List<GameOption> remainingOptions) {
		return false;
	}

	boolean shouldRollAgain(Player currentPlayer, boolean canBankPoints, int openingScoreLimit);

	boolean shouldSteal(Player currentPlayer, TurnContinuation continuation);
}

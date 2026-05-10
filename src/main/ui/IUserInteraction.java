package ui;


import model.entities.GameOption;
import model.entities.Player;
import rules.managers.RuleType;

import java.util.List;
import java.util.Map;


public interface IUserInteraction
{
	int getNumberOfPlayers();

	List<String> getPlayerNames(int numPlayers);

	int getValidScoreLimit();

	Map<RuleType, Object> selectRules();

	GameOption chooseGameOption(Player currentPlayer, List<GameOption> gameOptions);

	boolean shouldRollAgain(Player currentPlayer, boolean canBankPoints);
}

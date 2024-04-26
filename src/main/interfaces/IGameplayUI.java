package interfaces;


import models.Dice;
import models.GameOption;
import models.Player;
import models.Score;

import java.util.List;
import java.util.function.Supplier;


public interface IGameplayUI
{
	void displayWelcomeMessage();
	
	void displayGameOptions(Score score, List<GameOption> gameOptions, boolean isOptionSelectedForCurrentRoll);
	
	void displayCurrentScore(String playerName, int roundScore);
	
	void displayDice(Dice dice);
	
	void displayHighScoreInfo(Player currentPlayer, String highestScoringPlayerName);
	
	void displayMessage(String message);
	
	void displayLastRoundMessage(Player gameEndingPlayer, Supplier<Boolean> waitFunction);
	
	void announceTie(List<Player> tiedPlayers, int score);
	
	void announceWinner(Player winner, int score);
	
	void clear();
	
	void pauseAndContinue(Supplier<Boolean> waitFunction);
	
	void displayCurrentScore(Player currentPlayer);
	// Add other UI methods as needed
}

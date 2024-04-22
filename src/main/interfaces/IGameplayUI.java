package interfaces;


import models.GameOption;
import models.Player;

import java.util.List;


public interface IGameplayUI
{
	void displayWelcomeMessage();
	
	void displayGameOptions(List<GameOption> gameOptions);
	
	void displayCurrentScore(String playerName, int roundScore);
	
	void displayDice();
	
	void displayHighScoreInfo(Player currentPlayer, String highestScoringPlayerName);
	
	void displayMessage(String message);
	
	void displayLastRoundMessage(Player gameEndingPlayer);
	
	void announceTie(List<Player> tiedPlayers, int score);
	
	void announceWinner(Player winner, int score);
	
	void clear();
	
	void pauseAndContinue();
	
	void displayCurrentScore(Player currentPlayer);
	// Add other UI methods as needed
}

package ui;


import model.entities.Dice;
import model.entities.GameOption;
import model.entities.Player;
import model.entities.Score;

import java.util.List;


public interface IGameplayUI
{
	void displayWelcomeMessage();
	
	void displayGameOptions(Score score, List<GameOption> gameOptions, boolean isOptionSelectedForCurrentRoll);
	
	void displayCurrentScore(String playerName, int roundScore);
	
	void displayDice(Dice dice);
	
	void displayHighScoreInfo(Player currentPlayer, String highestScoringPlayerName);
	
	void displayMessage(String message);
	
	void displayAndWait(String message);
	
	void displayLastRoundMessage(Player gameEndingPlayer, Runnable waitFunction);
	
	void announceTie(List<Player> tiedPlayers, int score);
	
	void announceWinner(Player winner, int score);
	
	void clear();
	
	void pauseAndContinue(Runnable waitFunction);
	
	void displayCurrentScore(Player currentPlayer);
	// Add other UI methods as needed
}

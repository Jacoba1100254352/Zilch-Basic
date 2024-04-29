package model.managers;


import model.entities.Player;

import java.util.List;


public interface IPlayerManager
{
	void switchToNextPlayer();
	
	Player findHighestScoringPlayer();
	
	List<Player> getPlayers();
	
	void setPlayers(List<Player> players);
	
	Player getCurrentPlayer();
	
	void setCurrentPlayer(Player player);
	
	Player getGameEndingPlayer();
	
	void setGameEndingPlayer(Player player);
}

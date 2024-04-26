package interfaces;


import models.Player;

import java.util.List;


public interface IPlayerManager
{
	void switchToNextPlayer();
	
	Player findHighestScoringPlayer();
	
	List<Player> getPlayers();
	
	Player getCurrentPlayer();
	
	void setCurrentPlayer(Player player);
}

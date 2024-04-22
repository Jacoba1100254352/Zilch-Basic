package modelManagers;


import models.Dice;
import models.Player;
import models.Score;


public class PlayerActionManager
{
	private final PlayerManager playerManager;
	
	public PlayerActionManager(PlayerManager playerManager) {
		this.playerManager = playerManager;
	}
	
	public Player getCurrentPlayer() {
		return playerManager.getCurrentPlayer();
	}
	
	public Score getScore() {
		return getCurrentPlayer().score();
	}
	
	public Dice getDice() {
		return getCurrentPlayer().dice();
	}
}

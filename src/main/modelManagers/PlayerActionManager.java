package modelManagers;


import models.Dice;
import models.Player;
import models.Score;


public abstract class PlayerActionManager
{
	protected PlayerManager playerManager;
	
	public PlayerActionManager(PlayerManager playerManager) {
		this.playerManager = playerManager;
	}
	
	protected Player getCurrentPlayer() {
		return playerManager.getCurrentPlayer();
	}
	
	protected Score getScore() {
		return getCurrentPlayer().score();
	}
	
	protected Dice getDice() {
		return getCurrentPlayer().dice();
	}
}

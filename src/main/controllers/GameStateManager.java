package controllers;


import model.entities.Player;
import model.managers.ActionManager;
import model.managers.GameOptionManager;
import ui.IGameplayUI;


public class GameStateManager extends AbstractGameStateManager
{
	public GameStateManager(GameOptionManager gameOptionManager, IGameplayUI uiManager, ActionManager actionManager) {
		super(gameOptionManager, uiManager, actionManager);
	}
	
	@Override
	public void handleFirstRollBust() {
		uiManager.displayAndWait("You have busted on the first roll, try again");
		Player currentPlayer = actionManager.getCurrentPlayer();
		if (currentPlayer.score().getRoundScore() == 0) {
			currentPlayer.score().increaseRoundScore(50);
		}
		isBust = false;
		setReroll(true);
		setContinueTurn(true);
	}
	
	@Override
	public void handleBust() {
		uiManager.displayAndWait("You have busted");
		actionManager.getCurrentPlayer().score().setRoundScore(0);
		isBust = true;
		setReroll(false);
		setContinueTurn(false);
	}
}

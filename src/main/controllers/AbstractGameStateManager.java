package controllers;


import model.entities.Player;
import model.managers.ActionManager;
import model.managers.GameOptionManager;
import ui.IGameplayUI;


public abstract class AbstractGameStateManager implements IGameStateManager
{
	protected GameOptionManager gameOptionManager;
	protected IGameplayUI uiManager;
	protected ActionManager actionManager;
	
	protected boolean reroll;
	protected boolean continueTurn;
	protected boolean isBust;
	
	public AbstractGameStateManager(GameOptionManager gameOptionManager, IGameplayUI uiManager, ActionManager actionManager) {
		this.gameOptionManager = gameOptionManager;
		this.uiManager = uiManager;
		this.actionManager = actionManager;
		
		this.reroll = false;
		this.continueTurn = true;
		this.isBust = false;
	}
	
	@Override
	public void initializeRollCycle() {
		isBust = false;
		reroll = true;
		continueTurn = true;
		gameOptionManager.setSelectedGameOption(null);
		Player currentPlayer = actionManager.getCurrentPlayer();
		currentPlayer.score().setScoreFromMultiples(0);
		currentPlayer.score().setRoundScore(0);
		actionManager.replenishAllDice();
	}
	
	@Override
	public abstract void handleFirstRollBust();
	
	@Override
	public abstract void handleBust();
	
	@Override
	public boolean isBust() {
		return isBust;
	}
	
	@Override
	public void setBust(boolean bust) {
		this.isBust = bust;
	}
	
	@Override
	public boolean getReroll() {
		return reroll;
	}
	
	@Override
	public void setReroll(boolean reroll) {
		this.reroll = reroll;
	}
	
	@Override
	public boolean getContinueTurn() {
		return continueTurn;
	}
	
	@Override
	public void setContinueTurn(boolean continueTurn) {
		if (this.isBust) {
			this.continueTurn = false;
		} else {
			this.continueTurn = continueTurn;
		}
	}
}

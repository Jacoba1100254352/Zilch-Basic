package managers;


import modelManagers.ActionManager;
import modelManagers.GameEngine;
import models.Player;
import ui.ConsoleGameplayUI;


/**
 * Manages the state of the game during play.
 */
public class GameStateManager
{
	private final GameEngine gameEngine;
	private final ActionManager actionManager;
	private final ConsoleGameplayUI consoleGameplayUI;
	
	private boolean reroll;
	private boolean continueTurn;
	private boolean isBust;
	
	public GameStateManager(GameEngine gameEngine, ActionManager actionManager, ConsoleGameplayUI consoleGameplayUI) {
		this.gameEngine = gameEngine;
		this.actionManager = actionManager;
		this.consoleGameplayUI = consoleGameplayUI;
		
		this.reroll = false;
		this.continueTurn = true;
		this.isBust = false;
	}
	
	/**
	 * Initialize each segment of 6-dice re-rolls
	 */
	public void initializeRollCycle() {
		isBust = false;
		reroll = true;
		continueTurn = true;
		
		// Reset the current game option within the GameEngine
		gameEngine.setSelectedGameOption(null);
		
		// Initialize Score
		Player currentPlayer = actionManager.getCurrentPlayer();
		currentPlayer.score().setScoreFromMultiples(0);
		currentPlayer.score().setRoundScore(0);
		
		// Initialize Dice
		actionManager.replenishAllDice();
		
		// No need to reevaluate game options here since it's handled within the game turn
	}
	
	public void handleFirstRollBust() {
		consoleGameplayUI.displayMessage("You have busted on the first roll, try again");
		consoleGameplayUI.pauseAndContinue();
		
		Player currentPlayer = actionManager.getCurrentPlayer();
		if (currentPlayer.score().getRoundScore() == 0) {
			currentPlayer.score().increaseRoundScore(50);  // Provide minimal score after bust
		}
		
		isBust = false;
		setReroll(true);
		continueTurn = true;
	}
	
	public void handleBust() {
		consoleGameplayUI.displayMessage("You have busted");
		consoleGameplayUI.pauseAndContinue();
		
		actionManager.getCurrentPlayer().score().setRoundScore(0);
		
		isBust = true;
		setReroll(false);
		continueTurn = false;
	}
	
	///   Getters and Setters   ///
	
	public boolean isBust() {
		return isBust;
	}
	
	public void setBust(boolean bust) {
		isBust = bust;
	}
	
	public boolean getReroll() {
		return reroll;
	}
	
	public void setReroll(boolean reroll) {
		this.reroll = reroll;
	}
	
	public boolean getContinueTurn() {
		return continueTurn;
	}
	
	public void setContinueTurn(boolean continueTurn) {
		if (this.isBust) {
			this.continueTurn = false;
		} else {
			this.continueTurn = continueTurn;
		}
	}
}

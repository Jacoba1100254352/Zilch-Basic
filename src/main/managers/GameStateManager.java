package managers;


import models.Player;
import ui.ConsoleGameplayUI;


public class GameStateManager
{
	private final GameCoordinator gameCoordinator;
	
	private boolean reroll;
	private boolean continueTurn;
	private boolean isBust;
	
	public GameStateManager(GameCoordinator gameCoordinator) {
		this.gameCoordinator = gameCoordinator;
		
		this.reroll = false;
		this.continueTurn = true;
		this.isBust = false;
	}
	
	
	///   Main Functions   ///
	
	/**
	 * Initialize each segment of 6-dice re-rolls
	 */
	public void initializeRollCycle() {
		isBust = false;
		reroll = true;
		continueTurn = true; // Set to false when desiring to continue to the next player
		
		gameCoordinator.getGameOptionManager().setSelectedGameOption(null); // Reset the current game option
		
		// Initialize Score
		gameCoordinator.getPlayerManager().getCurrentPlayer().score().setScoreFromMultiples(0);
		gameCoordinator.getPlayerManager().getCurrentPlayer().score().setRoundScore(0);
		
		// Initialize Dice
		gameCoordinator.getPlayerManager().replenishAllDice();
		
		// Reevaluate game options
		gameCoordinator.getGameOptionManager().setPreviouslySelectedMultipleValue(null);
	}
	
	public void handleFirstRollBust() {
		ConsoleGameplayUI consoleGameplayUI = gameCoordinator.getGameplayUI();
		consoleGameplayUI.displayMessage("You have busted on the first roll, try again");
		consoleGameplayUI.pauseAndContinue();
		
		Player currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
		if (currentPlayer.score().getRoundScore() == 0) {
			currentPlayer.score().increaseRoundScore(50);
		}
		
		// If the player has any points on the next bust, it's a bust, so this fixes logic in the flowManager
		isBust = false;
		setReroll(true);
		continueTurn = true;
	}
	
	public void handleBust() {
		ConsoleGameplayUI consoleGameplayUI = gameCoordinator.getGameplayUI();
		consoleGameplayUI.displayMessage("You have busted");
		consoleGameplayUI.pauseAndContinue();
		
		gameCoordinator.getPlayerManager().getCurrentPlayer().score().setRoundScore(0);
		
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
		} else if (gameCoordinator.getPlayerManager().getCurrentPlayer().score().getRoundScore() >= 1000) {
			this.continueTurn = continueTurn;
		} else {
			this.continueTurn = true;
		}
	}
}

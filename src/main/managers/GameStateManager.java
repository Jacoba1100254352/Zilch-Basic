package managers;


import models.Player;
import ui.GameplayUI;


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
		GameplayUI gameplayUI = gameCoordinator.getGameplayUI();
		gameplayUI.displayMessage("You have busted on the first roll, try again");
		gameCoordinator.getUserInputHandler().pauseAndContinue();
		
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
		GameplayUI gameplayUI = gameCoordinator.getGameplayUI();
		gameplayUI.displayMessage("You have busted");
		gameCoordinator.getUserInputHandler().pauseAndContinue();
		
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
		int permanentScore = gameCoordinator.getPlayerManager().getCurrentPlayer().score().getPermanentScore();
		int roundScore = gameCoordinator.getPlayerManager().getCurrentPlayer().score().getRoundScore();
		
		if (this.isBust) {
			this.continueTurn = false;
		} else if (permanentScore + roundScore >= 1000) {
			this.continueTurn = continueTurn;
		} else {
			this.continueTurn = true;
		}
	}
}

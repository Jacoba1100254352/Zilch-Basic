package managers;


import modelManagers.PlayerManager;
import models.Player;
import ruleManagers.RuleManager;
import ui.GameplayUI;
import ui.UserInputHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * Coordinates the various aspects of the game including player management, game state, and user interactions.
 */
public class GameCoordinator
{
	
	private final GameplayUI gameplayUI;
	private final GameStateManager gameStateManager;
	private final GameOptionManager gameOptionManager;
	private final GameFlowManager gameFlowManager;
	private final RuleManager ruleManager;
	private UserInputHandler userInputHandler;
	private PlayerManager playerManager;
	private List<Player> players;
	
	/**
	 * Constructs a new GameCoordinator, initializing all necessary components of the game.
	 */
	public GameCoordinator() {
		// Initialize all the managers and UI components needed for the game
		this.ruleManager = new RuleManager(this);
		this.gameStateManager = new GameStateManager(this);
		this.gameOptionManager = new GameOptionManager(this);
		this.gameplayUI = new GameplayUI(this);
		this.gameFlowManager = new GameFlowManager(this);
		this.userInputHandler = null;
		this.playerManager = null; // Will be set during game setup
		this.players = new ArrayList<>(); // List to hold all the players
	}
	
	
	///   Main Functions   ///
	
	/**
	 * Sets up the game by displaying the welcome message and initializing players and score limit.
	 */
	public void setupGame() {
		// Display the welcome message and instructions
		gameplayUI.clear();
		gameplayUI.displayWelcomeMessage();
		userInputHandler.pauseAndContinue();
		
		// Get the score limit and player names from the user
		int scoreLimit = userInputHandler.getValidScoreLimit();
		List<String> playerNames = userInputHandler.getPlayerNames();
		
		// Clear the screen to start the game
		gameplayUI.clear();
		
		// Initialize the PlayerManager with the obtained player names and score limit
		this.playerManager = new PlayerManager(playerNames, scoreLimit);
		this.players = playerManager.getPlayers(); // Store the list of players
	}
	
	/**
	 * Initiates and controls the main game loop.
	 */
	public void playGame() {
		boolean firstTime = true;
		// Main game loop
		do {
			// Set the current player and initialize their turn
			if (!firstTime) {
				playerManager.switchToNextPlayer();
			}
			firstTime = false;
			
			gameStateManager.initializeRollCycle();
			
			// Handle the player's turn
			gameFlowManager.playTurn(playerManager.getCurrentPlayer(), null);
		} while (!gameFlowManager.gameOver(playerManager.getCurrentPlayer())); // Exit the loop if game ends
		
		gameFlowManager.handleGameEnd();
	}
	
	
	///   Helper Functions   ///
	
	public GameplayUI getGameplayUI() {
		return gameplayUI;
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
	
	public void setPlayerManager(PlayerManager playerManager) {
		this.playerManager = playerManager;
	}
	
	public RuleManager getRuleManager() {
		return ruleManager;
	}
	
	public GameStateManager getGameStateManager() {
		return gameStateManager;
	}
	
	public GameOptionManager getGameOptionManager() {
		return gameOptionManager;
	}
	
	public UserInputHandler getUserInputHandler() {
		return userInputHandler;
	}
	
	public void setUserInputHandler(UserInputHandler handler) {
		this.userInputHandler = handler;
	}
}

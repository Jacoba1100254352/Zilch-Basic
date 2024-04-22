package managers;

import interfaces.IDiceManager;
import interfaces.IEventDispatcher;
import interfaces.IScoreManager;
import modelManagers.DiceManager;
import modelManagers.PlayerManager;
import modelManagers.ScoreManager;
import models.Player;

import java.util.List;

/**
 * Coordinates the various aspects of the game including player management, game state, and user interactions.
 */
public class GameCoordinator {
	
	private static GameCoordinator instance = null;
	private GameEngine gameEngine;
	private IEventDispatcher eventDispatcher;
	
	/**
	 * Constructs a new GameCoordinator, initializing all necessary components of the game.
	 */
	private GameCoordinator(IEventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
		this.gameEngine = new GameEngine(eventDispatcher);
	}
	
	public static GameCoordinator getInstance(IEventDispatcher eventDispatcher) {
		if (instance == null) {
			instance = new GameCoordinator(eventDispatcher);
		}
		return instance;
	}
	
	///   Main Functions   ///
	
	/**
	 * Sets up the game by displaying the welcome message and initializing players and score limit.
	 */
	public void setupGame() {
		// Display the welcome message and instructions
		gameplayUI.displayWelcomeMessage();
		gameplayUI.pauseAndContinue();
		gameplayUI.clear();
		
		// Get the score limit and player names from the user
		int scoreLimit = userInteractionManager.getValidScoreLimit();
		List<String> playerNames = userInteractionManager.getPlayerNames();
		
		// Initialize the PlayerManager with the obtained player names and score limit
		IDiceManager diceManager = new DiceManager();
		IScoreManager scoreManager = new ScoreManager(scoreLimit);
		this.playerManager = new PlayerManager(playerNames, diceManager, scoreManager);
		this.players = playerManager.getPlayers(); // Store the list of players
	}
	
	/**
	 * Initiates and controls the main game loop.
	 *
	 * @param isTest Boolean indicating whether the game is being played or tested.
	 */
	public void playGame(boolean isTest) {
		// Main game loop
		while (true) {
			// Iterate through each player for their turn
			for (Player player : players) {
				// Check if the game-ending condition is met
				if (gameFlowManager.checkGameEndCondition(player)) {
					gameFlowManager.handleGameEnd(isTest);
					return; // Exit the loop if game ends
				}
				
				// Set the current player and initialize their turn
				playerManager.setCurrentPlayer(player);
				gameStateManager.initializeRollCycle();
				
				// Handle the player's turn
				gameFlowManager.playTurn(player, null, isTest);
			}
		}
	}
}

package managers;

import modelManagers.GameOptionManager;
import modelManagers.PlayerManager;
import models.Player;
import ruleManagers.RuleManager;
import ui.GameplayUI;
import ui.UserInteractionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates the various aspects of the game including player management, game state, and user interactions.
 */
public class GameCoordinator {

    private final GameplayUI gameplayUI;
    private final UserInteractionManager userInteractionManager;
    private final GameStateManager gameStateManager;
    private final GameOptionManager gameOptionManager;
    private final GameFlowManager gameFlowManager;
    private final RuleManager ruleManager;
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
        this.userInteractionManager = new UserInteractionManager(this);
        this.playerManager = null; // Will be set during game setup
        this.players = new ArrayList<>(); // List to hold all the players
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
        this.playerManager = new PlayerManager(playerNames, scoreLimit);
        this.players = playerManager.getPlayers(); // Store the list of players
    }

    /**
     * Initiates and controls the main game loop.
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

    public UserInteractionManager getUserInteractionManager() {
        return userInteractionManager;
    }
}

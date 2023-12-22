package managers;

import modelManagers.PlayerManager;
import models.Player;
import models.Score;
import ruleManagers.GameOptionManager;
import ruleManagers.RuleManager;
import ui.GameplayUI;
import ui.UserInteractionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GameCoordinator {

    private final GameplayUI gameplayUI;
    private final UserInteractionManager userInteractionManager;
    private final GameStateManager gameStateManager;
    private final GameOptionManager gameOptionManager;
    private final GameFlowManager gameFlowManager;
    private final RuleManager ruleManager;
    private PlayerManager playerManager;
    private List<Player> players;

    public GameCoordinator() {
        this.ruleManager = new RuleManager(this);
        this.gameStateManager = new GameStateManager(this);
        this.gameOptionManager = new GameOptionManager(this);
        this.gameplayUI = new GameplayUI(this);
        this.gameFlowManager = new GameFlowManager(this);
        this.userInteractionManager = new UserInteractionManager(this);
        this.playerManager = null;
        this.players = new ArrayList<>();
    }


    ///   Main Functions   ///

    public void setupGame() {
        gameplayUI.displayWelcomeMessage();
        gameplayUI.pauseAndContinue();
        gameplayUI.clear();

        int scoreLimit = userInteractionManager.getValidScoreLimit();
        List<String> playerNames = userInteractionManager.getPlayerNames();

        // Initialize player manager with players
        this.playerManager = new PlayerManager(playerNames, scoreLimit);
        this.players = playerManager.getPlayers();
    }

    /**
     * Game flow control methods
     */

    public void playGame() {
        while (true) {
            for (Player player : players) {
                // Delegate to GameFlowManager to check and manage game-end conditions
                if (gameFlowManager.checkGameEndCondition(player)) {
                    gameFlowManager.handleGameEnd();
                    return;
                }

                // Set up for the next player
                playerManager.setCurrentPlayer(player);
                gameStateManager.initializeRollCycle();

                // Delegate the turn play to GameFlowManager
                gameFlowManager.playTurn(player, null);
            }
        }
    }

    public List<Player> getTiedPlayers(int highestScore) {
        return players.stream()
                .filter(player -> player.score().getPermanentScore() == highestScore)
                .collect(Collectors.toList());
    }

    public boolean isTie(List<Player> tiedPlayers) {
        return tiedPlayers.size() > 1;
    }

    public void processPlayerInput() {
        Scanner scanner = new Scanner(System.in);

        do {
            gameplayUI.displayUIElements();
            userInteractionManager.inputGameOption(scanner);
            checkAndHandleTurnContinuation(scanner);
        } while (gameStateManager.getContinueTurn() && gameStateManager.getContinueSelecting() && ruleManager.isOptionAvailable());
    }

    private void checkAndHandleTurnContinuation(Scanner scanner) {
        if (playerManager.getCurrentPlayer().score().getRoundScore() >= 1000 || ruleManager.isOptionAvailable()) {
            handleTurnOptions(playerManager.getCurrentPlayer(), scanner);
        }
    }


    ///   Helper Functions   ///

    private void handleTurnOptions(Player currentPlayer, Scanner scanner) {
        Score score = currentPlayer.score();
        if (score.getRoundScore() >= 1000) {
            gameplayUI.displayHighScoreInfo(currentPlayer, playerManager.findHighestScoringPlayer().name());
            if (userInteractionManager.enterEndTurnOption(scanner, currentPlayer.score())) {
                gameStateManager.setContinueTurn(true, false);
                gameStateManager.setContinueSelecting(false);
                gameplayUI.clear(); // Clear screen.
            }
        } else if (!ruleManager.isOptionAvailable()) {
            gameStateManager.handleNoOptionsLeft();
        }
    }

    public GameplayUI getGameplayUI() {
        return gameplayUI;
    }


    ///   Getters and Setters   ///

    public PlayerManager getPlayerManager() {
        return playerManager;
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
}

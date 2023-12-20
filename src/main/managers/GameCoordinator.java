package managers;

import modelManagers.*;
import models.*;
import rules.*;
import ui.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.List;
import java.util.stream.Collectors;

public class GameCoordinator {
    private final GameplayUI gameplayUI;
    private List<Player> players;
    private final DiceManager diceManager;
    private final UserInteractionManager userInteractionManager;
    private final GameStateManager gameStateManager;
    private final GameOptionManager gameOptionManager;
    private final GameFlowManager gameFlowManager;
    private PlayerManager playerManager;
    private final ScoreManager scoreManager;
    private final RuleManager ruleManager;

    public GameCoordinator() {
        this.ruleManager = new RuleManager(this);
        this.gameStateManager = new GameStateManager(this, ruleManager);
        this.diceManager = new DiceManager(new Dice(new HashMap<>()));
        this.gameOptionManager = new GameOptionManager(this);
        this.gameplayUI = new GameplayUI(this);
        this.gameFlowManager = new GameFlowManager(this);
        this.userInteractionManager = new UserInteractionManager(gameplayUI, gameOptionManager, ruleManager, gameStateManager);
        this.scoreManager = new ScoreManager(this);
        this.playerManager = null;
        this.players = new ArrayList<>();
    }

    public void setupGame() {
        gameplayUI.displayWelcomeMessage();
        gameplayUI.pauseAndContinue();
        gameplayUI.clear();

        int scoreLimit = userInteractionManager.getValidScoreLimit();
        List<String> playerNames = userInteractionManager.getPlayerNames();

        // Initialize player manager with players
        this.playerManager = new PlayerManager(playerNames, scoreLimit);
        this.players = playerManager.getPlayers();

        // Setup dice
        diceManager.replenishAllDice();
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
                diceManager.replenishAllDice();
                gameStateManager.initializeRollCycle();

                // Delegate the turn play to GameFlowManager
                gameFlowManager.playTurn(player);
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
            updateAndProcessGameOption();
            userInteractionManager.processInput(scanner);
            checkAndHandleTurnContinuation(scanner);
        } while (gameStateManager.getTurnContinuationStatus() && gameStateManager.getSelectionContinuationStatus() && ruleManager.isOptionAvailable());
    }

    private void updateAndProcessGameOption() {
        gameOptionManager.updateCurrentGameOption();
        GameOption optionToProcess = gameOptionManager.getCurrentGameOption();

        if (optionToProcess != null && optionToProcess.type() == GameOption.Type.MULTIPLE && !ruleManager.canProcessMultiple(optionToProcess.value())) {
            gameplayUI.displayImpossibleOptionMessage();
        }

        if (optionToProcess != null && gameOptionManager.isValidMove(optionToProcess)) {
            gameOptionManager.processMove(optionToProcess);
        } else {
            System.out.println("No valid move available or invalid move selected.");
        }
    }

    private void checkAndHandleTurnContinuation(Scanner scanner) {
        if (playerManager.getCurrentPlayer().score().getRoundScore() >= 1000 || ruleManager.isOptionAvailable()) {
            handleTurnOptions(playerManager.getCurrentPlayer(), scanner);
        }
    }

    private void handleTurnOptions(Player currentPlayer, Scanner scanner) {
        Score score = currentPlayer.score();
        if (score.getRoundScore() >= 1000) {
            gameplayUI.displayHighScoreInfo(currentPlayer, playerManager.findHighestScoringPlayer().name());
            if (userInteractionManager.enterEndTurnOption(scanner, currentPlayer.score())) {
                gameStateManager.setTurnContinuationStatus(true, false);
                gameStateManager.setSelectionContinuationStatus(false);
                gameplayUI.clear(); // Clear screen.
            }
        } else if (!ruleManager.isOptionAvailable()) {
            gameStateManager.handleNoOptionsLeft();
        }
    }

    /****************************
     *   GET AND SET FUNCTIONS   *
     ****************************/

    public GameplayUI getGameplayUI() {
        return gameplayUI;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public RuleManager getRuleManager() {
        return ruleManager;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public DiceManager getDiceManager() {
        return diceManager;
    }

    public GameStateManager getGameStateManager() {
        return gameStateManager;
    }

    public GameOptionManager getGameOptionManager() {
        return gameOptionManager;
    }

    /************************
     *   ENUM FOR PRINTING   *
     ************************/
    public enum printOptions {
        ENTER, NEXT, REENTER
    }
}

package managers;

import models.Player;
import models.Score;
import rules.RuleManager;
import ui.GameplayUI;

import static models.Dice.FULL_SET_OF_DICE;

public class GameStateManager {
    private final GameCoordinator gameCoordinator;
    private final RuleManager ruleManager;

    private boolean selectedOptionStatus;
    private boolean turnContinuationStatus;
    private boolean selectionContinuationStatus;

    public GameStateManager(GameCoordinator gameCoordinator, RuleManager ruleManager) {
        this.gameCoordinator = gameCoordinator;
        this.ruleManager = ruleManager;

        this.turnContinuationStatus = true;
        this.selectedOptionStatus = false;
        this.selectionContinuationStatus = true;
    }

    /**
     * Initialize each segment of 6-dice re-rolls
     */
    public void initializeRollCycle() {
        turnContinuationStatus = true;
        selectedOptionStatus = false; // No option has yet been selected
        selectionContinuationStatus = true; // Can continue/start selecting

        gameCoordinator.getGameOptionManager().setCurrentGameOption(null); // Reset the current game option

        // Initialize Score
        gameCoordinator.getPlayerManager().getCurrentPlayer().score().setScoreFromMultiples(0);
    }

    public void handleFirstRollBust() {
        GameplayUI gameplayUI = gameCoordinator.getGameplayUI();
        gameplayUI.displayMessage("You have busted on the first roll, try again");
        gameplayUI.pauseAndContinue();

        Player currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        currentPlayer.score().increaseRoundScore(50);

        setTurnContinuationStatus(true, true);
    }

    public void handleBust() {
        GameplayUI gameplayUI = gameCoordinator.getGameplayUI();
        gameplayUI.displayMessage("You have busted");
        gameplayUI.pauseAndContinue();

        Player currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        currentPlayer.score().setRoundScore(0);

        setTurnContinuationStatus(false, true);
    }

    public void handleNoOptionsLeft() {
        GameplayUI gameplayUI = gameCoordinator.getGameplayUI();
        gameplayUI.clear();
        gameplayUI.displayCurrentScore(gameCoordinator.getPlayerManager().getCurrentPlayer());
        gameplayUI.displayDice();
        gameplayUI.displayMessage("There are no options left");
        gameplayUI.pauseAndContinue();
    }

    public void processZeroCommand() {
        Player currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        Score score = currentPlayer.score();
        GameplayUI gameplayUI = gameCoordinator.getGameplayUI();

        if (selectedOptionStatus) {
            selectionContinuationStatus = false;
        } else if ((score.getRoundScore() >= 1000) && ruleManager.isOptionAvailable()) {
            score.increasePermanentScore(score.getRoundScore());
            gameplayUI.displayMessage("Your official score is now: " + score.getPermanentScore());
        } else if (!ruleManager.isOptionAvailable()) {
            gameplayUI.clear();
        } else {
            gameplayUI.clear();
            gameplayUI.displayMessage("You cannot end without a score higher than 1000");
            gameplayUI.pauseAndContinue();
        }
    }

    public boolean shouldContinueTurn() {
        gameCoordinator.getGameStateManager().setSelectedOptionStatus(false);

        if (!ruleManager.isOptionAvailable()) {
            if (gameCoordinator.getPlayerManager().getCurrentPlayer().dice().getNumDiceInPlay() == FULL_SET_OF_DICE && gameCoordinator.getPlayerManager().getCurrentPlayer().score().getRoundScore() == 0) {
                gameCoordinator.getGameStateManager().handleFirstRollBust();
            } else if (gameCoordinator.getPlayerManager().getCurrentPlayer().dice().getNumDiceInPlay() >= 1) {
                gameCoordinator.getGameStateManager().handleBust();
            } else {
                System.out.println("\nYou have a full set of dice now");
                gameCoordinator.getGameplayUI().pauseAndContinue();
                gameCoordinator.getDiceManager().replenishAllDice();
            }
            return false;
        }
        return true;
    }


    ///   GETTERS AND SETTERS   ///

    public boolean getSelectedOptionStatus() {
        return selectedOptionStatus;
    }

    public void setSelectedOptionStatus(boolean isOptionSelected) {
        selectedOptionStatus = isOptionSelected;
    }

    public void setTurnContinuationStatus(boolean continueTurn, boolean isBust) {
        if (isBust || gameCoordinator.getPlayerManager().getCurrentPlayer().score().getRoundScore() >= 1000) {
            turnContinuationStatus = continueTurn;
        } else {
            System.out.println("You are not allowed to end without a permanent or running score higher than 1000");
            turnContinuationStatus = true;
        }
    }

    public boolean getTurnContinuationStatus() {
        return turnContinuationStatus;
    }

    public void setSelectionContinuationStatus(boolean continueSelecting) {
        if (!continueSelecting && !selectedOptionStatus) {
            System.out.println("You must select at least one option");
            selectionContinuationStatus = true;
        } else {
            selectionContinuationStatus = continueSelecting;
        }
    }

    public boolean getSelectionContinuationStatus() {
        return selectionContinuationStatus;
    }
}

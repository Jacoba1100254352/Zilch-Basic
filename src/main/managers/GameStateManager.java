package managers;

import models.Player;
import models.Score;
import ui.GameplayUI;

import static models.Dice.FULL_SET_OF_DICE;

public class GameStateManager {
    private final GameCoordinator gameCoordinator;

    private boolean isOptionSelected;
    private boolean continueTurn;
    private boolean continueSelecting;

    public GameStateManager(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;

        this.continueTurn = true;
        this.isOptionSelected = false;
        this.continueSelecting = true;
    }


    ///   Main Functions   ///

    /**
     * Initialize each segment of 6-dice re-rolls
     */
    public void initializeRollCycle() {
        continueTurn = true;
        isOptionSelected = false; // No option has yet been selected
        continueSelecting = true; // Can continue/start selecting

        gameCoordinator.getGameOptionManager().setSelectedGameOption(null); // Reset the current game option

        // Initialize Score
        gameCoordinator.getPlayerManager().getCurrentPlayer().score().setScoreFromMultiples(0);

        // Initialize Dice
        gameCoordinator.getPlayerManager().replenishAllDice();

        // Reevaluate game options
        gameCoordinator.getGameOptionManager().setPreviouslySelectedMultipleValue(null);
    }

    public void handleFirstRollBust() {
        GameplayUI gameplayUI = gameCoordinator.getGameplayUI();
        gameplayUI.displayMessage("You have busted on the first roll, try again");
        gameplayUI.pauseAndContinue();

        Player currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        currentPlayer.score().increaseRoundScore(50);

        setContinueTurn(true, true);
    }

    public void handleBust() {
        GameplayUI gameplayUI = gameCoordinator.getGameplayUI();
        gameplayUI.displayMessage("You have busted");
        gameplayUI.pauseAndContinue();

        Player currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        currentPlayer.score().setRoundScore(0);

        setContinueTurn(false, true);
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

        if (isOptionSelected) {
            continueSelecting = false;
        } else if ((score.getRoundScore() >= 1000) && gameCoordinator.getRuleManager().isOptionAvailable()) {
            score.increasePermanentScore(score.getRoundScore());
            gameplayUI.displayMessage("Your official score is now: " + score.getPermanentScore());
        } else if (!gameCoordinator.getRuleManager().isOptionAvailable()) {
            gameplayUI.clear();
        } else {
            gameplayUI.clear();
            gameplayUI.displayMessage("You cannot end without a score higher than 1000");
            gameplayUI.pauseAndContinue();
        }
    }

    public boolean canContinueTurn() {
        gameCoordinator.getGameStateManager().optionIsSelected(false);

        if (!gameCoordinator.getRuleManager().isOptionAvailable()) {
            if (gameCoordinator.getPlayerManager().getCurrentPlayer().dice().getNumDiceInPlay() == FULL_SET_OF_DICE && gameCoordinator.getPlayerManager().getCurrentPlayer().score().getRoundScore() == 0) {
                gameCoordinator.getGameStateManager().handleFirstRollBust();
            } else if (gameCoordinator.getPlayerManager().getCurrentPlayer().dice().getNumDiceInPlay() >= 1) {
                gameCoordinator.getGameStateManager().handleBust();
            } else {
                gameCoordinator.getGameplayUI().displayMessage("\nYou have a full set of dice now");
                gameCoordinator.getGameplayUI().pauseAndContinue();
                gameCoordinator.getPlayerManager().replenishAllDice();
            }
            return false;
        }
        return true;
    }


    ///   GETTERS AND SETTERS   ///

    public boolean isOptionSelected() {
        return isOptionSelected;
    }

    public void optionIsSelected(boolean isOptionSelected) {
        this.isOptionSelected = isOptionSelected;
    }

    public void setContinueTurn(boolean continueTurn, boolean isBust) {
        if (isBust || gameCoordinator.getPlayerManager().getCurrentPlayer().score().getRoundScore() >= 1000) {
            this.continueTurn = continueTurn;
        } else {
            gameCoordinator.getGameplayUI().displayMessage("You are not allowed to end without a permanent or running score higher than 1000");
            this.continueTurn = true;
        }
    }

    public boolean getContinueTurn() {
        return continueTurn;
    }

    public boolean getContinueSelecting() {
        return continueSelecting;
    }

    public void setContinueSelecting(boolean continueSelecting) {
        if (!continueSelecting && !isOptionSelected) {
            gameCoordinator.getGameplayUI().displayMessage("You must select at least one option");
            this.continueSelecting = true;
        } else {
            this.continueSelecting = continueSelecting;
        }
    }
}

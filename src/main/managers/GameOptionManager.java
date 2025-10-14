package managers;

import models.GameOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages game options available to players based on the current game state.
 */
public class GameOptionManager {
    private final GameCoordinator gameCoordinator;
    private final List<GameOption> gameOptions;
    private GameOption selectedGameOption;
    private boolean optionSelectedForCurrentRoll;

    public GameOptionManager(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
        this.gameOptions = new ArrayList<>();
        this.selectedGameOption = null;
    }

    ///   Main Functions   ///

    /**
     * Evaluates and updates the game options available based on the current state of the game.
     */
    public void evaluateGameOptions() {
        gameOptions.clear();
        gameOptions.addAll(gameCoordinator.getRuleManager().evaluateAvailableOptions());
    }

    /**
     * Checks if the selected game option is a valid move.
     *
     * @return true if the move is valid, false otherwise.
     */
    public boolean isValidMove() {
        if (selectedGameOption == null) {
            return false;
        }

        return gameOptions.stream()
                          .anyMatch(option -> option.type() == selectedGameOption.type()
                                  && (option.value() == null || option.value().equals(selectedGameOption.value())));
    }

    /**
     * Processes the selected game option.
     */
    public void processMove() {
        if (selectedGameOption == null) {
            gameCoordinator.getGameplayUI().displayMessage("No option selected.");
            return;
        }

        if (isValidMove()) {
            gameCoordinator.getRuleManager().apply(selectedGameOption);
        } else {
            gameCoordinator.getGameplayUI().displayMessage("Invalid move selected.");
        }

        setSelectedGameOption(null);
    }

    ///   Helper Functions   ///

    public List<GameOption> getGameOptions() {
        return gameOptions;
    }

    public boolean isOptionSelectedForCurrentRoll() {
        return optionSelectedForCurrentRoll;
    }

    public void setOptionSelectedForCurrentRoll(boolean optionSelectedForCurrentRoll) {
        this.optionSelectedForCurrentRoll = optionSelectedForCurrentRoll;
    }

    public void setSelectedGameOption(GameOption gameOption) {
        this.selectedGameOption = gameOption;
    }
}

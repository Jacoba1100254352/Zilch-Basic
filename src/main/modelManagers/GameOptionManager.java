package modelManagers;

import managers.GameCoordinator;
import models.GameOption;

import java.util.ArrayList;
import java.util.List;

import static models.Dice.FULL_SET_OF_DICE;

/**
 * Manages game options available to players based on the current game state.
 */
public class GameOptionManager {
    private final GameCoordinator gameCoordinator;
    private final List<GameOption> gameOptions;
    private GameOption selectedGameOption;
    private Integer previouslySelectedMultipleValue;
    private boolean optionSelectedForCurrentRoll;

    public GameOptionManager(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
        this.gameOptions = new ArrayList<>();
        this.selectedGameOption = null;
        this.previouslySelectedMultipleValue = null;
    }


    ///   Main Functions   ///

    /**
     * Evaluates and updates the game options available based on the current state of the game.
     */
    public void evaluateGameOptions() {
        // Clear existing game options
        gameOptions.clear();

        // Add game options based on the current dice state and rules
        addGameOptionsBasedOnRules();
    }

    /**
     * Checks if the selected game option is a valid move.
     *
     * @return true if the move is valid, false otherwise.
     */
    public boolean isValidMove() {
        // Check if the selected option matches any of the available options
        return gameOptions.stream()
                .anyMatch(option -> option.type() == selectedGameOption.type() &&
                        (option.value() == null || option.value().equals(selectedGameOption.value())));
    }

    /**
     * Processes the selected game option.
     */
    public void processMove() {
        // Check if an option has been selected
        if (selectedGameOption == null) {
            gameCoordinator.getGameplayUI().displayMessage("No option selected.");
            return;
        }

        // Process the move if it's valid
        if (isValidMove()) {
            processSelectedOption();
        } else {
            gameCoordinator.getGameplayUI().displayMessage("Invalid move selected.");
        }
    }


    ///   Helper Functions   ///

    // Adds game options based on the current rules and dice state
    private void addGameOptionsBasedOnRules() {
        // Add straits and sets if applicable
        if (gameCoordinator.getRuleManager().isStrait()) {
            gameOptions.add(new GameOption(GameOption.Type.STRAIT, null));
        }
        if (gameCoordinator.getRuleManager().isSet()) {
            gameOptions.add(new GameOption(GameOption.Type.SET, null));
        }

        // Add multiples and singles based on the dice values
        for (int dieValue = 1; dieValue <= FULL_SET_OF_DICE; dieValue++) {
            addMultipleAndSingleOptions(dieValue);
        }
    }

    // Adds multiple and single game options based on a given die value
    private void addMultipleAndSingleOptions(int dieValue) {
        if (gameCoordinator.getRuleManager().isDesiredMultipleAvailable(dieValue)) {
            gameOptions.add(new GameOption(GameOption.Type.MULTIPLE, dieValue));
        }
        if (dieValue == 1 || dieValue == 5) {
            if (gameCoordinator.getRuleManager().isSingle(dieValue)) {
                gameOptions.add(new GameOption(GameOption.Type.SINGLE, dieValue));
            }
        }
    }

    // Processes the selected game option based on its type
    private void processSelectedOption() {
        switch (selectedGameOption.type()) {
            case STRAIT -> handleStraits();
            case SET -> handleSets();
            case MULTIPLE -> handleMultiples();
            case SINGLE -> handleSingles();
            default -> gameCoordinator.getGameplayUI().displayMessage("Invalid move selected.");
        }

        // Reset the selectedGameOption after processing
        setSelectedGameOption(null);
    }

    // Handles the scoring and dice removal for straits
    private void handleStraits() {
        gameCoordinator.getPlayerManager().scoreStraits();
        gameCoordinator.getPlayerManager().removeAllDice();
    }

    // Handles the scoring and dice removal for sets
    private void handleSets() {
        gameCoordinator.getPlayerManager().scoreSets();
        gameCoordinator.getPlayerManager().removeAllDice();
    }

    // Handles the scoring and dice removal for multiples
    private void handleMultiples() {
        previouslySelectedMultipleValue = selectedGameOption.value();
        gameCoordinator.getPlayerManager().scoreMultiple(selectedGameOption.value());
        gameCoordinator.getPlayerManager().eliminateDice(selectedGameOption.value());
    }

    // Handles the scoring and dice removal for singles
    private void handleSingles() {
        gameCoordinator.getPlayerManager().scoreSingle(selectedGameOption.value());
        gameCoordinator.getPlayerManager().removeDice(selectedGameOption.value(), 1);
    }


    ///   Getters and Setters   ///

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

    public Integer getPreviouslySelectedMultipleValue() {
        return previouslySelectedMultipleValue;
    }

    public void setPreviouslySelectedMultipleValue(Integer value) {
        this.previouslySelectedMultipleValue = value;
    }
}

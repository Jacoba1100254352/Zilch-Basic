package modelManagers;

import managers.GameCoordinator;
import models.GameOption;

import java.util.ArrayList;
import java.util.List;

import static models.Dice.FULL_SET_OF_DICE;

public class GameOptionManager {
    private final GameCoordinator gameCoordinator;
    private GameOption currentGameOption;

    public GameOptionManager(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
        this.currentGameOption = null;
    }

    public boolean isValidMove(GameOption gameOption) {
        return evaluateGameOptions().stream()
                .anyMatch(option -> option.type() == gameOption.type() &&
                        (option.value() == null || option.value().equals(gameOption.value())));
    }

    public List<GameOption> evaluateGameOptions() {
        List<GameOption> options = new ArrayList<>();

        if (gameCoordinator.getRuleManager().isStrait()) {
            options.add(new GameOption(GameOption.Type.STRAIT, null));
        }
        if (gameCoordinator.getRuleManager().isSet()) {
            options.add(new GameOption(GameOption.Type.SET, null));
        }

        for (int dieValue = 1; dieValue <= FULL_SET_OF_DICE; dieValue++) {
            if (gameCoordinator.getRuleManager().isDesiredMultipleAvailable(dieValue)) {
                options.add(new GameOption(GameOption.Type.MULTIPLE, dieValue));
            }
        }

        if (gameCoordinator.getRuleManager().isSingle(1)) {
            options.add(new GameOption(GameOption.Type.SINGLE, 1));
        }
        if (gameCoordinator.getRuleManager().isSingle(5)) {
            options.add(new GameOption(GameOption.Type.SINGLE, 5));
        }

        return options;
    }

    // This method can be used by the gameplay manager to process each move
    public void processMove(GameOption gameOption) {
        if (isValidMove(gameOption)) {
            switch (gameOption.type()) {
                case STRAIT -> handleStraits();
                case SET -> handleSets();
                case MULTIPLE -> handleMultiples(gameOption.value());
                case SINGLE -> handleSingles(gameOption.value());
            }
        } else {
            gameCoordinator.getGameplayUI().displayMessage("Invalid move selected.");
        }
    }

    public void applyAllPossibleOptions() {
        List<GameOption> options = evaluateGameOptions();
        for (GameOption option : options) {
            processMove(option);
        }
    }

    private void handleStraits() {
        gameCoordinator.getScoreManager().scoreStraits();
        gameCoordinator.getDiceManager().removeAllDice();
        gameCoordinator.getGameStateManager().setSelectedOptionStatus(true);
    }

    private void handleSets() {
        gameCoordinator.getScoreManager().scoreSets();
        gameCoordinator.getDiceManager().removeAllDice();
        gameCoordinator.getGameStateManager().setSelectedOptionStatus(true);
    }

    private void handleMultiples(int value) {
        gameCoordinator.getScoreManager().scoreMultiple(value);
        gameCoordinator.getDiceManager().eliminateDice(value);
        gameCoordinator.getGameStateManager().setSelectedOptionStatus(true);
    }

    private void handleSingles(int value) {
        gameCoordinator.getScoreManager().scoreSingle(value);
        gameCoordinator.getDiceManager().removeDice(value, 1); // TODO: Adjust so they can remove those they choose
        gameCoordinator.getGameStateManager().setSelectedOptionStatus(true);
    }

    public void updateCurrentGameOption() {
        List<GameOption> availableOptions = evaluateGameOptions();

        // Reset the current game option
        GameOption gameOption = null;

        if (!availableOptions.isEmpty()) {
            // For simplicity, just choose the first available option
            // TODO: add more sophisticated logic here (change from getFirst to get the best option for the user
            gameOption = availableOptions.getFirst();
        }

        // Update the GameStateManager with the new current game option
        this.currentGameOption = gameOption;
    }

    public GameOption getCurrentGameOption() {
        return this.currentGameOption;
    }
    public void setCurrentGameOption(GameOption gameOption) {
        this.currentGameOption = gameOption;
    }
}

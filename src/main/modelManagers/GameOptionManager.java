package modelManagers;

import managers.GameCoordinator;
import models.GameOption;

import java.util.ArrayList;
import java.util.List;

import static models.Dice.FULL_SET_OF_DICE;

public class GameOptionManager {
    private final GameCoordinator gameCoordinator;
    private final List<GameOption> gameOptions;
    private GameOption selectedGameOption;
    private Integer previouslySelectedMultipleValue;

    public GameOptionManager(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
        this.gameOptions = new ArrayList<>();
        this.selectedGameOption = null;
        this.previouslySelectedMultipleValue = null;
    }


    ///   Main Functions   ///

    public void evaluateGameOptions() {
        gameOptions.clear();

        if (gameCoordinator.getRuleManager().isStrait()) {
            gameOptions.add(new GameOption(GameOption.Type.STRAIT, null));
        }
        if (gameCoordinator.getRuleManager().isSet()) {
            gameOptions.add(new GameOption(GameOption.Type.SET, null));
        }

        for (int dieValue = 1; dieValue <= FULL_SET_OF_DICE; dieValue++) {
            if (gameCoordinator.getRuleManager().isDesiredMultipleAvailable(dieValue)) {
                gameOptions.add(new GameOption(GameOption.Type.MULTIPLE, dieValue));
            }
        }

        if (gameCoordinator.getRuleManager().isSingle(1)) {
            gameOptions.add(new GameOption(GameOption.Type.SINGLE, 1));
        }
        if (gameCoordinator.getRuleManager().isSingle(5)) {
            gameOptions.add(new GameOption(GameOption.Type.SINGLE, 5));
        }
    }

    public boolean isValidMove() {
        return gameOptions.stream()
                .anyMatch(option -> option.type() == selectedGameOption.type() &&
                        (option.value() == null || option.value().equals(selectedGameOption.value())));
    }

    public void processMove() {
        if (isValidMove()) {
            switch (selectedGameOption.type()) {
                case STRAIT -> handleStraits();
                case SET -> handleSets();
                case MULTIPLE -> handleMultiples();
                case SINGLE -> handleSingles();
            }
        } else {
            gameCoordinator.getGameplayUI().displayMessage("Invalid move selected.");
        }
    }

    public void processOptionInt(int val) {
        GameOption.Type optionType = determineGameOptionType(val);
        if (optionType != null) {
            selectedGameOption = new GameOption(optionType, val);
            processMove();
        } else {
            gameCoordinator.getGameplayUI().displayImpossibleOptionMessage();
        }
    }

    public void processOptionS(char ch) {
        GameOption.Type optionType = switch (ch) {
            case 't' -> gameOptions.stream().anyMatch(option -> option.type() == GameOption.Type.STRAIT) ? GameOption.Type.STRAIT : null;
            case 'e' -> gameOptions.stream().anyMatch(option -> option.type() == GameOption.Type.SET) ? GameOption.Type.SET : null;
            case '1', '5' -> gameOptions.stream().anyMatch(option -> option.type() == GameOption.Type.SINGLE) ? GameOption.Type.SINGLE : null;
            default -> null;
        };

        Integer val = switch (ch) {
            case '1' -> 1;
            case '5' -> 5;
            default -> null;
        };

        if (optionType != null) {
            selectedGameOption = new GameOption(optionType, val);
            processMove();
        } else {
            gameCoordinator.getGameplayUI().displayImpossibleOptionMessage();
        }
    }

    public void processOptionM(char ch) {
        int val = Character.getNumericValue(ch);
        GameOption.Type multipleType = determineGameOptionType(val);

        if (multipleType == GameOption.Type.MULTIPLE) {
            selectedGameOption = new GameOption(multipleType, val);
            processMove();
        } else {
            gameCoordinator.getGameplayUI().displayFailedMultipleMessage();
        }
    }


    ///   Helper Functions   ///

    private GameOption.Type determineGameOptionType(int val) {
        return (gameCoordinator.getRuleManager().isStrait()) ? GameOption.Type.STRAIT
                : (gameCoordinator.getRuleManager().isSet()) ? GameOption.Type.SET
                : (gameCoordinator.getRuleManager().isDesiredMultipleAvailable(val)) ? GameOption.Type.MULTIPLE
                : (gameCoordinator.getRuleManager().isSingle(val)) ? GameOption.Type.SINGLE
                : null; // Default case if no type matches
    }

    private void handleStraits() {
        gameCoordinator.getPlayerManager().scoreStraits();
        gameCoordinator.getPlayerManager().removeAllDice();
        gameCoordinator.getGameStateManager().optionIsSelected(true);
    }

    private void handleSets() {
        gameCoordinator.getPlayerManager().scoreSets();
        gameCoordinator.getPlayerManager().removeAllDice();
        gameCoordinator.getGameStateManager().optionIsSelected(true);
    }

    private void handleMultiples() {
        // TODO: Verify that this is sufficient and no checks for a previous value is needed (pSMV != null)
        previouslySelectedMultipleValue = selectedGameOption.value();

        gameCoordinator.getPlayerManager().scoreMultiple(selectedGameOption.value());
        gameCoordinator.getPlayerManager().eliminateDice(selectedGameOption.value());
        gameCoordinator.getGameStateManager().optionIsSelected(true);
    }

    private void handleSingles() {
        gameCoordinator.getPlayerManager().scoreSingle(selectedGameOption.value());
        gameCoordinator.getPlayerManager().removeDice(selectedGameOption.value(), 1); // TODO: Adjust so they can remove those they choose
        gameCoordinator.getGameStateManager().optionIsSelected(true);
    }


    ///   Getters and Setters   ///

    public List<GameOption> getGameOptions() {
        return gameOptions;
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

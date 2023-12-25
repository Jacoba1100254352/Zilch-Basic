package ruleManagers;

import managers.GameCoordinator;

import java.util.Map;

import static models.Dice.FULL_SET_OF_DICE;

public class RuleManager {
    private final GameCoordinator gameCoordinator;

    public RuleManager(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
    }


    ///   Main Functions   ///

    public boolean isOptionAvailable() {
        return (isStrait() || isSet() || isMultiple() || canAddMultiples() || isSingle(1) || isSingle(5));
    }

    public boolean isStrait() {
        Map<Integer, Integer> diceSetMap = gameCoordinator.getPlayerManager().getCurrentPlayer().dice().diceSetMap();

        // Check if the map size is exactly 6 (for numbers 1 to 6)
        if (diceSetMap.size() != FULL_SET_OF_DICE) {
            return false;
        }

        // Check if each number from 1 to 6 appears exactly once
        for (int i = 1; i <= FULL_SET_OF_DICE; i++) {
            if (!diceSetMap.containsKey(i) || diceSetMap.get(i) != 1) {
                return false;
            }
        }

        return true;
    }

    public boolean isSet() {
        return (gameCoordinator.getPlayerManager().getCurrentPlayer().dice().diceSetMap().size() == 3 && !isStrait() && !isMultiple()) &&
                gameCoordinator.getPlayerManager().getCurrentPlayer().dice().diceSetMap().values().stream()
                        .allMatch(count -> count == 2);
    }

    public boolean isSingle(int single) {
        return ((single == 1 || single == 5) &&
                gameCoordinator.getPlayerManager().getCurrentPlayer().dice().diceSetMap().containsKey(single));
    }

    public boolean isMultiple() {
        return gameCoordinator.getPlayerManager().getCurrentPlayer().dice().diceSetMap().values().stream().anyMatch(count -> count >= 3);
    }

    public boolean canAddMultiples() {
        return gameCoordinator.getPlayerManager().getCurrentPlayer().dice().diceSetMap().containsKey(gameCoordinator.getGameOptionManager().getPreviouslySelectedMultipleValue());
    }

    public boolean isDesiredMultipleAvailable(int desiredMultiple) {
        return gameCoordinator.getPlayerManager().getCurrentPlayer().dice().diceSetMap().getOrDefault(desiredMultiple, 0) >= 3;
    }
}

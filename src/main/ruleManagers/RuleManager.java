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
        return (diceSetMap.size() == FULL_SET_OF_DICE) &&
                diceSetMap.entrySet().stream().allMatch(e -> e.getKey() == (e.getValue() == 1 ? 1 : 0));
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

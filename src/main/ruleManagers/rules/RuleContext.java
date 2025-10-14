package ruleManagers.rules;

import managers.GameCoordinator;
import modelManagers.PlayerManager;
import models.Player;

import java.util.Collections;
import java.util.Map;

public class RuleContext {
    private final GameCoordinator gameCoordinator;
    private Integer previouslySelectedMultipleValue;

    public RuleContext(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
    }

    public GameCoordinator getGameCoordinator() {
        return gameCoordinator;
    }

    public PlayerManager getPlayerManager() {
        return gameCoordinator != null ? gameCoordinator.getPlayerManager() : null;
    }

    public Player getCurrentPlayer() {
        PlayerManager playerManager = getPlayerManager();
        return playerManager != null ? playerManager.getCurrentPlayer() : null;
    }

    public Map<Integer, Integer> getDiceSetMap() {
        PlayerManager playerManager = getPlayerManager();
        Player currentPlayer = getCurrentPlayer();
        if (playerManager == null || currentPlayer == null) {
            return Collections.emptyMap();
        }
        return playerManager.getDice(currentPlayer);
    }

    public Integer getPreviouslySelectedMultipleValue() {
        return previouslySelectedMultipleValue;
    }

    public void setPreviouslySelectedMultipleValue(Integer previouslySelectedMultipleValue) {
        this.previouslySelectedMultipleValue = previouslySelectedMultipleValue;
    }

    public void clearPreviouslySelectedMultipleValue() {
        this.previouslySelectedMultipleValue = null;
    }
}

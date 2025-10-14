package ruleManagers;

import managers.GameCoordinator;
import managers.GameOptionManager;
import modelManagers.PlayerManager;

import java.util.Collections;
import java.util.Map;

public class RuleContext
{
        private final GameCoordinator gameCoordinator;
        private final Map<Integer, Integer> diceSetMap;

        public RuleContext(GameCoordinator gameCoordinator, Map<Integer, Integer> diceSetMap) {
                this.gameCoordinator = gameCoordinator;
                this.diceSetMap = Collections.unmodifiableMap(diceSetMap);
        }

        public GameCoordinator getGameCoordinator() {
                return gameCoordinator;
        }

        public PlayerManager getPlayerManager() {
                return gameCoordinator.getPlayerManager();
        }

        public GameOptionManager getGameOptionManager() {
                return gameCoordinator.getGameOptionManager();
        }

        public Map<Integer, Integer> getDiceSetMap() {
                return diceSetMap;
        }

        public Integer getPreviouslySelectedMultipleValue() {
                return getGameOptionManager().getPreviouslySelectedMultipleValue();
        }
}

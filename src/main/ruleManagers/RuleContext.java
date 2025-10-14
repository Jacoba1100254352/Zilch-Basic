package ruleManagers;

import managers.GameCoordinator;
import modelManagers.PlayerManager;

import java.util.Collections;
import java.util.Map;

/**
 * Provides rules with access to the information they require to evaluate and apply game options.
 */
public class RuleContext
{
        private final GameCoordinator gameCoordinator;
        private final RuleManager ruleManager;
        private final Map<Integer, Integer> diceSetMap;

        public RuleContext(GameCoordinator gameCoordinator, RuleManager ruleManager, Map<Integer, Integer> diceSetMap) {
                this.gameCoordinator = gameCoordinator;
                this.ruleManager = ruleManager;
                this.diceSetMap = Collections.unmodifiableMap(diceSetMap);
        }

        public GameCoordinator getGameCoordinator() {
                return gameCoordinator;
        }

        public PlayerManager getPlayerManager() {
                return gameCoordinator.getPlayerManager();
        }

        public Map<Integer, Integer> getDiceSetMap() {
                return diceSetMap;
        }

        public Integer getPreviouslySelectedMultipleValue() {
                return ruleManager.getPreviouslySelectedMultipleValue();
        }

        public void setPreviouslySelectedMultipleValue(Integer value) {
                ruleManager.setPreviouslySelectedMultipleValue(value);
        }

        public void clearPreviouslySelectedMultipleValue() {
                ruleManager.setPreviouslySelectedMultipleValue(null);
        }
}

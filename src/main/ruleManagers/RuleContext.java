package ruleManagers;

import managers.GameCoordinator;
import managers.GameOptionManager;
import modelManagers.PlayerManager;

import java.util.Map;

/**
 * Provides contextual access to the current game state for rule strategies.
 */
public class RuleContext
{
        private final GameCoordinator gameCoordinator;

        public RuleContext(GameCoordinator gameCoordinator)
        {
                this.gameCoordinator = gameCoordinator;
        }

        public GameCoordinator gameCoordinator()
        {
                return gameCoordinator;
        }

        public PlayerManager playerManager()
        {
                return gameCoordinator.getPlayerManager();
        }

        public GameOptionManager gameOptionManager()
        {
                return gameCoordinator.getGameOptionManager();
        }

        public Map<Integer, Integer> currentDice()
        {
                return playerManager().getDice(playerManager().getCurrentPlayer());
        }

        public Integer previouslySelectedMultiple()
        {
                return gameOptionManager().getPreviouslySelectedMultipleValue();
        }

        public void setPreviouslySelectedMultiple(Integer value)
        {
                gameOptionManager().setPreviouslySelectedMultipleValue(value);
        }
}

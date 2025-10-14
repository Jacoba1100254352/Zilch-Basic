import managers.GameCoordinator;
import modelManagers.PlayerManager;
import models.GameOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ruleManagers.RuleDescriptor;
import ruleManagers.RuleManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RuleManagerTest
{
        private GameCoordinator gameCoordinator;
        private RuleManager ruleManager;
        private PlayerManager playerManager;

        @BeforeEach
        void setUp() {
                gameCoordinator = new GameCoordinator();
                gameCoordinator.setUserInputHandler(new FakeUserInputHandler(gameCoordinator));
                playerManager = new PlayerManager(List.of("Tester"), 5000);
                gameCoordinator.setPlayerManager(playerManager);
                ruleManager = gameCoordinator.getRuleManager();
        }

        private Map<Integer, Integer> diceMap() {
                return playerManager.getCurrentPlayer().dice().getDiceSetMap();
        }

        private void setDice(Integer... values) {
                Map<Integer, Integer> map = new HashMap<>();
                for (int value : values) {
                        map.merge(value, 1, Integer::sum);
                }
                playerManager.getCurrentPlayer().dice().setDiceSetMap(map);
        }

        @Test
        void evaluateAvailableOptionsReturnsStraitWhenActive() {
                setDice(1, 2, 3, 4, 5, 6);

                List<GameOption> options = ruleManager.evaluateAvailableOptions(diceMap());
                assertTrue(options.stream().anyMatch(o -> o.type() == GameOption.Type.STRAIT));
        }

        @Test
        void configureActiveRulesCanDisableStrait() {
                setDice(1, 2, 3, 4, 5, 6);
                List<String> enabled = ruleManager.getAvailableRuleDescriptors().stream()
                                                   .map(RuleDescriptor::id)
                                                   .filter(id -> !"strait".equals(id))
                                                   .toList();
                ruleManager.configureActiveRules(enabled);

                List<GameOption> options = ruleManager.evaluateAvailableOptions(diceMap());
                assertFalse(options.stream().anyMatch(o -> o.type() == GameOption.Type.STRAIT));
        }

        @Test
        void resetTurnStateClearsStoredMultiple() {
                ruleManager.setPreviouslySelectedMultipleValue(3);
                ruleManager.resetTurnState();
                assertNull(ruleManager.getPreviouslySelectedMultipleValue());
        }
}

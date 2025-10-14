package ruleManagers;


import managers.GameCoordinator;
import modelManagers.PlayerManager;
import models.GameOption;
import models.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class RuleManagerTest
{

        private GameCoordinator gameCoordinator;
        private RuleManager ruleManager;
        private Player currentPlayer;

        @BeforeEach
        void setUp() {
                final int scoreLimit = 5000;
                gameCoordinator = new GameCoordinator();
                List<String> playerNames = List.of("TestPlayer");
                gameCoordinator.setPlayerManager(new PlayerManager(playerNames, scoreLimit));

                ruleManager = gameCoordinator.getRuleManager();
                currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        }

        @Test
        @DisplayName("Positive: Strait")
        void straitRuleProvidesOption() {
                setCurrentPlayerDiceMap(1, 2, 3, 4, 5, 6);
                assertTrue(evaluateOptions().contains(new GameOption(GameOption.Type.STRAIT, null)));
        }

        @Test
        @DisplayName("Negative: Strait")
        void straitRuleDoesNotProvideOption() {
                setCurrentPlayerDiceMap(1, 2, 2, 3, 4, 5);
                assertFalse(evaluateOptions().contains(new GameOption(GameOption.Type.STRAIT, null)));
        }

        @Test
        @DisplayName("Positive: Set")
        void setRuleProvidesOption() {
                setCurrentPlayerDiceMap(2, 2, 3, 3, 5, 5);
                assertTrue(evaluateOptions().contains(new GameOption(GameOption.Type.SET, null)));
        }

        @Test
        @DisplayName("Negative: Set")
        void setRuleDoesNotProvideOption() {
                setCurrentPlayerDiceMap(2, 2, 3, 4, 5, 6);
                assertFalse(evaluateOptions().contains(new GameOption(GameOption.Type.SET, null)));
        }

        @Test
        @DisplayName("Positive: Single")
        void singleRuleProvidesOption() {
                setCurrentPlayerDiceMap(1, 2, 3, 4, 5, 6);
                assertTrue(evaluateOptions().contains(new GameOption(GameOption.Type.SINGLE, 1)));
        }

        @Test
        @DisplayName("Negative: Single")
        void singleRuleDoesNotProvideOption() {
                setCurrentPlayerDiceMap(2, 2, 3, 4, 5, 6);
                assertFalse(evaluateOptions().contains(new GameOption(GameOption.Type.SINGLE, 1)));
        }

        @Test
        @DisplayName("Positive: Multiple")
        void multipleRuleProvidesOption() {
                setCurrentPlayerDiceMap(2, 2, 2, 3, 4, 5);
                assertTrue(evaluateOptions().contains(new GameOption(GameOption.Type.MULTIPLE, 2)));
        }

        @Test
        @DisplayName("Negative: Multiple")
        void multipleRuleDoesNotProvideOption() {
                setCurrentPlayerDiceMap(2, 2, 3, 4, 5, 6);
                assertFalse(evaluateOptions().contains(new GameOption(GameOption.Type.MULTIPLE, 2)));
        }

        @Test
        @DisplayName("Positive: Add Multiple")
        void addMultipleRuleProvidesOption() {
                setCurrentPlayerDiceMap(2, 4, 5);
                gameCoordinator.getGameOptionManager().setPreviouslySelectedMultipleValue(2);
                assertTrue(evaluateOptions().contains(new GameOption(GameOption.Type.ADD_MULTIPLE, 2)));
        }

        @Test
        @DisplayName("Negative: Add Multiple")
        void addMultipleRuleDoesNotProvideOption() {
                setCurrentPlayerDiceMap(3, 4, 5, 6);
                gameCoordinator.getGameOptionManager().setPreviouslySelectedMultipleValue(2);
                assertFalse(evaluateOptions().contains(new GameOption(GameOption.Type.ADD_MULTIPLE, 2)));
        }

        @Test
        @DisplayName("Rule can be disabled")
        void disablingRuleRemovesOptions() {
                setCurrentPlayerDiceMap(1, 2, 3, 4, 5, 6);
                ruleManager.setRuleEnabled("strait", false);
                assertFalse(evaluateOptions().contains(new GameOption(GameOption.Type.STRAIT, null)));
        }

        private List<GameOption> evaluateOptions() {
                Map<Integer, Integer> diceSetMap = currentPlayer.dice().getDiceSetMap();
                RuleContext context = new RuleContext(gameCoordinator, diceSetMap);
                return ruleManager.evaluateOptions(context);
        }

        private void setCurrentPlayerDiceMap(Integer... values) {
                Map<Integer, Integer> diceMap = new HashMap<>();
                for (int value : values) {
                        diceMap.merge(value, 1, Integer::sum);
                }

                currentPlayer.dice().setDiceSetMap(diceMap);
                currentPlayer.dice().calculateNumDiceInPlay();
        }
}
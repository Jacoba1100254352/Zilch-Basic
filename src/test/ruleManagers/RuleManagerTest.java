package ruleManagers;

import managers.GameCoordinator;
import modelManagers.PlayerManager;
import models.GameOption;
import models.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ruleManagers.rules.SingleRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RuleManagerTest {

    private RuleManager ruleManager;
    private Player currentPlayer;
    private Map<Integer, Integer> diceSetMap;

    @BeforeEach
    void setUp() {
        GameCoordinator gameCoordinator = new GameCoordinator();
        ruleManager = gameCoordinator.getRuleManager();
        List<String> playerNames = List.of("TestPlayer");
        gameCoordinator.setPlayerManager(new PlayerManager(playerNames, 5000));
        currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        diceSetMap = currentPlayer.dice().getDiceSetMap();
    }

    @Test
    @DisplayName("Automatically loads rules implementing the Rule interface")
    void loadsRulesDynamically() {
        List<String> ruleIds = ruleManager.getRegisteredRuleIds();
        assertTrue(ruleIds.contains("StraitRule"));
        assertTrue(ruleIds.contains("SetRule"));
        assertTrue(ruleIds.contains("MultipleRule"));
        assertTrue(ruleIds.contains("AddMultipleRule"));
        assertTrue(ruleIds.contains("SingleRule"));
    }

    @Test
    @DisplayName("Evaluates options for a strait correctly")
    void evaluateStrait() {
        setCurrentPlayerDiceMap(1, 2, 3, 4, 5, 6);
        List<GameOption> options = ruleManager.evaluateAvailableOptions();
        assertTrue(options.contains(new GameOption(GameOption.Type.STRAIT, null)));
    }

    @Test
    @DisplayName("Disabling a rule removes its options")
    void disablingRuleRemovesOptions() {
        setCurrentPlayerDiceMap(1, 5, 2, 3, 4, 6);
        ruleManager.evaluateAvailableOptions();
        assertTrue(ruleManager.isRuleValid(new GameOption(GameOption.Type.SINGLE, 1)));

        ruleManager.setRuleEnabled(SingleRule.class, false);
        List<GameOption> options = ruleManager.evaluateAvailableOptions();
        assertFalse(options.stream().anyMatch(option -> option.type() == GameOption.Type.SINGLE));
    }

    @Test
    @DisplayName("Applying a rule executes its logic")
    void applyRuleUpdatesGameState() {
        setCurrentPlayerDiceMap(3, 3, 3, 4, 5, 6);
        List<GameOption> options = ruleManager.evaluateAvailableOptions();
        GameOption multipleThree = options.stream()
                                          .filter(option -> option.type() == GameOption.Type.MULTIPLE && option.value() == 3)
                                          .findFirst()
                                          .orElseThrow();

        ruleManager.apply(multipleThree);

        assertEquals(300, currentPlayer.score().getRoundScore());
        assertEquals(0, currentPlayer.dice().getNumDiceInPlay());
        assertEquals(3, ruleManager.getPreviouslySelectedMultipleValue());
    }

    private void setCurrentPlayerDiceMap(Integer... values) {
        Map<Integer, Integer> diceMap = new HashMap<>();
        for (int value : values) {
            diceMap.merge(value, 1, Integer::sum);
        }
        diceSetMap.clear();
        diceSetMap.putAll(diceMap);
        currentPlayer.dice().calculateNumDiceInPlay();
    }
}

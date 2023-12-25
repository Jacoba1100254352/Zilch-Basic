package ruleManagers;

import managers.GameCoordinator;
import modelManagers.PlayerManager;
import models.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleManagerTest {

    private RuleManager ruleManager;
    private GameCoordinator gameCoordinator;
    private Player currentPlayer;

    @BeforeEach
    void setUp() {
        final int scoreLimit = 5000;
        gameCoordinator = new GameCoordinator(); // Assuming this can be instantiated
        List<String> playerNames = List.of("TestPlayer");

        // Initialize player manager with players
        gameCoordinator.setPlayerManager(new PlayerManager(playerNames, scoreLimit));
        currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        ruleManager = new RuleManager(gameCoordinator);
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test if necessary
    }

    @Test
    @DisplayName("Positive: Option Available")
    void isOptionAvailablePass() {
        setCurrentPlayerDiceMap(1, 2, 3, 4, 5, 6);
        assertTrue(ruleManager.isOptionAvailable(), "isOptionAvailable should return true for a valid dice set");
    }

    @Test
    @DisplayName("Negative: Option Available")
    void isOptionAvailableFail() {
        setCurrentPlayerDiceMap(2, 2, 3, 4, 4, 6);
        assertFalse(ruleManager.isOptionAvailable(), "isOptionAvailable should return false when no options available");
    }

    @Test
    @DisplayName("Positive: Strait")
    void isStraitPass() {
        setCurrentPlayerDiceMap(1, 2, 3, 4, 5, 6);
        assertTrue(ruleManager.isStrait(), "isStrait should return true for a strait dice set");
    }

    @Test
    @DisplayName("Negative: Strait")
    void isStraitFail() {
        setCurrentPlayerDiceMap(1, 2, 2, 4, 5, 6);
        assertFalse(ruleManager.isStrait(), "isStrait should return false when no strait exists");
    }

    @Test
    @DisplayName("Positive: Set")
    void isSetPass() {
        setCurrentPlayerDiceMap(2, 2, 3, 3, 4, 4);
        assertTrue(ruleManager.isSet(), "isSet should return true for a set dice set");
    }

    @Test
    @DisplayName("Negative: Set")
    void isSetFail() {
        setCurrentPlayerDiceMap(2, 3, 3, 3, 4, 4);
        assertFalse(ruleManager.isSet(), "isSet should return false when no set exists");
    }

    @Test
    @DisplayName("Positive: Single")
    void isSinglePass() {
        setCurrentPlayerDiceMap(1, 2, 3, 4, 5);
        assertTrue(ruleManager.isSingle(1), "isSingle should return true for a single 1");
        assertTrue(ruleManager.isSingle(5), "isSingle should return true for a single 5");
    }

    @Test
    @DisplayName("Negative: Single")
    void isSingleFail() {
        setCurrentPlayerDiceMap(2, 2, 3, 4, 4);
        assertFalse(ruleManager.isSingle(1), "isSingle should return false when no single 1 exists");
        assertFalse(ruleManager.isSingle(5), "isSingle should return false when no single 5 exists");
    }

    @Test
    @DisplayName("Positive: Multiple")
    void isMultiplePass() {
        setCurrentPlayerDiceMap(2, 2, 2, 3, 4, 5);
        assertTrue(ruleManager.isMultiple(), "isMultiple should return true for a multiple dice set");

        setCurrentPlayerDiceMap(2, 2, 2, 3, 3, 3);
        assertTrue(ruleManager.isMultiple(), "isMultiple should return true for double multiple sets");
    }

    @Test
    @DisplayName("Negative: Multiple")
    void isMultipleFail() {
        setCurrentPlayerDiceMap(1, 2, 2, 3, 4, 5);
        assertFalse(ruleManager.isMultiple(), "isMultiple should return false when no multiple exists");
    }

    @Test
    @DisplayName("Positive: Add Multiple")
    void canAddMultiplesPass() {
        setCurrentPlayerDiceMap(2, 4, 5);
        gameCoordinator.getGameOptionManager().setPreviouslySelectedMultipleValue(2);
        assertTrue(ruleManager.canAddMultiples(), "canAddMultiples should return true when multiples can be added");
    }

    @Test
    @DisplayName("Negative: Add Multiple")
    void canAddMultiplesFail() {
        setCurrentPlayerDiceMap(3, 4, 5);
        gameCoordinator.getGameOptionManager().setPreviouslySelectedMultipleValue(2);
        assertFalse(ruleManager.canAddMultiples(), "canAddMultiples should return false when multiples cannot be added");
    }

    @Test
    @DisplayName("Positive: Desired Multiple Available")
    void isDesiredMultipleAvailablePass() {
        setCurrentPlayerDiceMap(2, 2, 2, 3, 4, 5);
        assertTrue(ruleManager.isDesiredMultipleAvailable(2), "isDesiredMultipleAvailable should return true for an available multiple");
    }

    @Test
    @DisplayName("Negative: Desired Multiple Available")
    void isDesiredMultipleAvailableFalse() {
        setCurrentPlayerDiceMap(2, 2, 2, 3, 4, 5);
        assertFalse(ruleManager.isDesiredMultipleAvailable(6), "isDesiredMultipleAvailable should return false for an unavailable multiple");
    }

    private void setCurrentPlayerDiceMap(Integer... values) {
        Map<Integer, Integer> diceMap = new HashMap<>();
        for (int value : values) {
            diceMap.merge(value, 1, Integer::sum);
        }

        currentPlayer.dice().setDiceSetMap(diceMap);
    }
}

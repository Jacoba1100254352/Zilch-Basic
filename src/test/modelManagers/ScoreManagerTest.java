package modelManagers;

import models.Dice;
import models.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ScoreManagerTest {

    private ScoreManager scoreManager;
    private Score score;
    private Dice dice;

    @BeforeEach
    void setUp() {
        // Setup Score and Dice for a player
        final int scoreLimit = 5000;
        List<String> playerNames = List.of("TestPlayer");

        // PlayerManager to return our test player
        scoreManager = new ScoreManager(new PlayerManager(playerNames, scoreLimit));

        // Setup Score and Dice for a player
        score = scoreManager.getScore();
        dice = scoreManager.getDice();
    }

    @Test
    @DisplayName("Positive: Score Straits")
    void scoreStraitPass() {
        scoreManager.scoreStraits();
        assertEquals(1000, score.getRoundScore(), "Scoring straits should add 1000 to round score");
    }

    @Test
    @DisplayName("Negative: Score Straits")
    void scoreStraitFail() {
        scoreManager.scoreStraits();
        assertNotEquals(500, score.getRoundScore(), "Scoring straits should not add 500 to round score");
    }

    @Test
    @DisplayName("Positive: Score Sets")
    void scoreSetsPass() {
        scoreManager.scoreSets();
        assertEquals(1000, score.getRoundScore(), "Scoring sets should add 1000 to round score");
    }

    @Test
    @DisplayName("Negative: Score Sets")
    void scoreSetsFail() {
        scoreManager.scoreSets();
        assertNotEquals(500, score.getRoundScore(), "Scoring sets should not add 500 to round score");
    }

    @Test
    @DisplayName("Positive: Score Single")
    void scoreSinglePass() {
        scoreManager.scoreSingle(1);
        assertEquals(100, score.getRoundScore(), "Scoring a single 1 should add 100 to round score");

        scoreManager.scoreSingle(5);
        assertEquals(150, score.getRoundScore(), "Scoring a single 5 should add 50 to round score (total 150)");
    }

    @Test
    @DisplayName("Negative: Score Single")
    void scoreSingleFail() {
        scoreManager.scoreSingle(1);
        assertNotEquals(50, score.getRoundScore(), "Scoring a single 1 should not add 50 to round score");

        scoreManager.scoreSingle(5);
        assertNotEquals(100, score.getRoundScore(), "Scoring a single 5 should not add 100 to round score (total 100)");
    }

    @Test
    @DisplayName("Positive: Score Multiple")
    void scoreMultiplePass() {
        // Set up dice for multiple scoring
        Map<Integer, Integer> diceMap = new HashMap<>();
        diceMap.put(3, 4); // 4 threes
        dice.setDiceSetMap(diceMap);

        scoreManager.scoreMultiple(3);
        int expectedScore = (3 * 100) * (int)Math.pow(2, 1); // 300 * 2 = 600
        assertEquals(expectedScore, score.getRoundScore(), "Scoring multiples should add correctly calculated score");

        // Add another three to the dice
        diceMap.put(3, 5);
        dice.setDiceSetMap(diceMap);

        scoreManager.scoreMultiple(3);
        expectedScore = (3 * 100) * (int)Math.pow(2, 2); // 300 * 4 = 1200
        assertEquals(expectedScore, score.getRoundScore(), "Scoring additional multiples should add correctly calculated score");

        // Add another three to the dice
        diceMap.put(3, 6);
        dice.setDiceSetMap(diceMap);

        scoreManager.scoreMultiple(3);
        expectedScore = (3 * 100) * (int)Math.pow(2, 3); // 300 * 8 = 2400
        assertEquals(expectedScore, score.getRoundScore(), "Scoring additional multiples should add correctly calculated score");
    }

    @Test
    @DisplayName("Negative: Score Multiple")
    void scoreMultipleFail() {
        // Set up dice for multiple scoring
        Map<Integer, Integer> diceMap = new HashMap<>();
        diceMap.put(3, 4); // 4 threes
        dice.setDiceSetMap(diceMap);

        scoreManager.scoreMultiple(3);
        int wrongScore = (3 * 100) * (int)Math.pow(2, 2); // 300 * 4 = 1200
        assertNotEquals(wrongScore, score.getRoundScore(), "Scoring multiples should not add incorrect score");

        // Add another three to the dice
        diceMap.put(3, 5);
        dice.setDiceSetMap(diceMap);

        scoreManager.scoreMultiple(3);
        wrongScore = (3 * 100) * (int)Math.pow(2, 3); // 300 * 8 = 2400
        assertNotEquals(wrongScore, score.getRoundScore(), "Scoring additional multiples should not add incorrect score");
    }
}

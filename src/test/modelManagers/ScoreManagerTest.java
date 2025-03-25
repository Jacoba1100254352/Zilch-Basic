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


class ScoreManagerTest
{
    
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
        score.setScoreFromMultiples(0);
        score.setRoundScore(0);
        
        scoreManager.scoreMultiple(3);
        int expectedScore = (3 * 100) * (int) Math.pow(2, 1); // 300 * 2 = 600
        assertEquals(expectedScore, score.getRoundScore(), "Scoring multiples should add correctly calculated score");
        
        // Add another three to the dice
        diceMap.put(3, 5);
        dice.setDiceSetMap(diceMap);
        score.setScoreFromMultiples(0);
        score.setRoundScore(0);
        
        scoreManager.scoreMultiple(3);
        expectedScore = (3 * 100) * (int) Math.pow(2, 2); // 300 * 4 = 1200
        assertEquals(expectedScore, score.getRoundScore(), "Scoring additional multiples should add correctly calculated score");
        
        // Add another three to the dice
        diceMap.put(3, 6);
        dice.setDiceSetMap(diceMap);
        score.setScoreFromMultiples(0);
        score.setRoundScore(0);
        
        scoreManager.scoreMultiple(3);
        expectedScore = (3 * 100) * (int) Math.pow(2, 3); // 300 * 8 = 2400
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
        int wrongScore = (3 * 100) * (int) Math.pow(2, 2); // 300 * 4 = 1200
        assertNotEquals(wrongScore, score.getRoundScore(), "Scoring multiples should not add incorrect score");
        
        // Add another three to the dice
        diceMap.put(3, 5);
        dice.setDiceSetMap(diceMap);
        
        scoreManager.scoreMultiple(3);
        wrongScore = (3 * 100) * (int) Math.pow(2, 3); // 300 * 8 = 2400
        assertNotEquals(wrongScore, score.getRoundScore(), "Scoring additional multiples should not add incorrect score");
    }
    
    @Test
    @DisplayName("Positive: Score Add Multiple")
    void scoreAddMultiplePass() {
        // 1) First, set up 3 of a kind for "3" => triggers the "Multiple" path
        Map<Integer, Integer> diceMap = new HashMap<>();
        diceMap.put(3, 3); // 3 threes
        dice.setDiceSetMap(diceMap);
        score.setRoundScore(0);
        score.setScoreFromMultiples(0);
        
        // Score the initial multiple
        scoreManager.scoreMultiple(3);
        // For 3×3, the base score is 3*100 = 300, so roundScore = 300
        assertEquals(300, score.getRoundScore(), "Initial multiple of three 3s should add 300");
        assertEquals(300, score.getScoreFromMultiples(), "scoreFromMultiples should be 300 after first multiple");
        
        // 2) Now add a fourth "3" => triggers the "Add Multiple" path
        diceMap.put(3, 4); // 4 threes
        dice.setDiceSetMap(diceMap);
        
        // Score again with the same die value (3)
        scoreManager.scoreMultiple(3);
        
        // The formula for Add Multiple is:
        // mScore = (2^(countOfDice)) * previousMultiplesValue
        // countOfDice = 4, previousMultiplesValue = 300
        // => mScore = (2^4) * 300 = 16 * 300 = 4800
        // Then the *additional* points added to roundScore = (4800 - 300) = 4500
        // So total roundScore = 300 (previous) + 4500 (new) = 4800
        int expectedScore = 4800;
        assertEquals(expectedScore, score.getRoundScore(), "Adding a fourth '3' should bring total roundScore to 4800");
        assertEquals(expectedScore, score.getScoreFromMultiples(), "scoreFromMultiples should update to the new total (4800)");
        
        // Adding another 3
        diceMap.put(3, 1); // 1 three
        dice.setDiceSetMap(diceMap);
        
        // Score again with the same die value (3)
        scoreManager.scoreMultiple(3);
        
        // The formula for Add Multiple is:
        // mScore = (2^(countOfDice)) * previousMultiplesValue
        // countOfDice = 4, previousMultiplesValue = 300
        // => mScore = (2^4) * 300 = 16 * 300 = 4800
        // Then the *additional* points added to roundScore = (4800 - 300) = 4500
        // So total roundScore = 300 (previous) + 4500 (new) = 4800
        expectedScore = 9600;
        assertEquals(expectedScore, score.getRoundScore(), "Adding a fifth '3' should bring total roundScore to 9600");
        assertEquals(expectedScore, score.getScoreFromMultiples(), "scoreFromMultiples should update to the new total (9600)");
        
        // Adding 1's
        diceMap.put(1, 3); // 3 ones
        dice.setDiceSetMap(diceMap);
        score.setRoundScore(0);
        score.setScoreFromMultiples(0);
        
        // Score the initial multiple
        scoreManager.scoreMultiple(1);
        // For 1×3, the base score is 1*1000 = 300, so roundScore = 1000
        assertEquals(1000, score.getRoundScore(), "Initial multiple of three 3s should add 300");
        assertEquals(1000, score.getScoreFromMultiples(), "scoreFromMultiples should be 300 after first multiple");
        
        // Adding a 1
        diceMap.put(1, 2); // 2 ones
        dice.setDiceSetMap(diceMap);
        
        // Score again with the same die value (1)
        scoreManager.scoreMultiple(1);
        
        // 1000 * 2 * 2 = 4000
        expectedScore = 4000;
        assertEquals(expectedScore, score.getRoundScore(), "Adding two more '1's should bring total roundScore to 4000");
        assertEquals(expectedScore, score.getScoreFromMultiples(), "scoreFromMultiples should update to the new total (4000)");
    }
    
    @Test
    @DisplayName("Negative: Score Add Multiple")
    void scoreAddMultipleFail() {
        // Similar setup as above
        Map<Integer, Integer> diceMap = new HashMap<>();
        diceMap.put(3, 3); // 3 threes
        dice.setDiceSetMap(diceMap);
        score.setRoundScore(0);
        score.setScoreFromMultiples(0);
        
        // Score the initial multiple
        scoreManager.scoreMultiple(3);
        assertEquals(300, score.getRoundScore(), "Should initially be 300");
        assertEquals(300, score.getScoreFromMultiples(), "scoreFromMultiples should initially be 300");
        
        // Now add a fourth "3" => triggers the Add Multiple path
        diceMap.put(3, 4);
        dice.setDiceSetMap(diceMap);
        
        // Score again
        scoreManager.scoreMultiple(3);
        
        // We know the correct total is 4800, so let's confirm it doesn't equal some wrong value
        assertNotEquals(600, score.getRoundScore(), "Should not be 600 after adding a fourth '3' (should be 4800)");
        assertNotEquals(300, score.getScoreFromMultiples(), "scoreFromMultiples should no longer be 300");
    }
}

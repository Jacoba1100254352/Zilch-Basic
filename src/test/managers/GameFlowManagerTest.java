package managers;

import modelManagers.PlayerManager;
import models.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class GameFlowManagerTest {
    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    private GameCoordinator gameCoordinator;
    private GameFlowManager gameFlowManager;
    private Player player;
    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        // Redirect all System.out to outputStreamCaptor
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(outputStreamCaptor));

        // Set up game coordinator and player
        final int scoreLimit = 5000;
        gameCoordinator = new GameCoordinator();
        gameFlowManager = new GameFlowManager(gameCoordinator);
        List<String> playerNames = List.of("TestPlayer");

        // Initialize player manager with players
        gameCoordinator.setPlayerManager(new PlayerManager(playerNames, scoreLimit));
        player = gameCoordinator.getPlayerManager().getCurrentPlayer();

        // Set up executor for testing
        executor = Executors.newSingleThreadExecutor();
    }

    @AfterEach
    public void tearDown() {
        // Reset System.out and System.err
        System.setOut(standardOut);
        System.setErr(standardErr);

        // Shut down executor
        executor.shutdownNow();
    }

    @Test
    @DisplayName("End Turn Successfully")
    void endTurnSuccessfully() {
        // Set up player's round score to trigger the end of the turn logic
        player.score().setRoundScore(1200);

        // Set game state to end turn
        gameCoordinator.getGameStateManager().setBust(false);
        gameCoordinator.getGameStateManager().setContinueTurn(false);
        gameCoordinator.getGameStateManager().setReroll(false);

        gameFlowManager.playTurn(player, null, true); // true for isTest

        // Check if the permanent score was updated correctly
        assertEquals(1200, player.score().getPermanentScore(), "Permanent score should be updated correctly");
        assertEquals(0, player.score().getRoundScore(), "Round score should be reset after updating permanent score");
    }

    @Test
    @DisplayName("Handle Bust Scenario")
    void handleBustScenario() {
        // Simulate a round with points
        player.score().setRoundScore(500);

        // Simulate a bust scenario
        gameCoordinator.getGameStateManager().handleBust();

        // Check if continueTurn is false after a bust
        assertFalse(gameCoordinator.getGameStateManager().getContinueTurn(), "Continue turn should be false after a bust");

        // Only call playTurn if continueTurn is false
        if (!gameCoordinator.getGameStateManager().getContinueTurn()) {
            gameFlowManager.playTurn(player, null, true); // true for isTest
        } else {
            fail("Continue turn should be false after a bust, but it was true");
        }

        // Check if the round score is reset
        assertEquals(0, player.score().getRoundScore(), "Round score should reset to 0 on bust");
    }

    @Test
    @DisplayName("Handle First Roll Bust Scenario")
    void handleFirstRollBustScenario() {
        // The Round starts with zero score
        player.score().setRoundScore(0);

        // Simulate a bust scenario on the first roll
        gameCoordinator.getGameStateManager().handleFirstRollBust();

        // Continue the turn without marking bust
        gameCoordinator.getGameStateManager().setContinueTurn(true);
        gameCoordinator.getGameStateManager().setReroll(false);

        Future<?> future = executor.submit(() -> gameFlowManager.playTurn(player, null, true)); // true for isTest

        try {
            future.get(2, TimeUnit.SECONDS); // Adjust the time based on expected execution time
            fail("Method did not hang as expected.");
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            assertTrue(true, "Method hanged as expected.");
        } finally {
            future.cancel(true); // Interrupt if still running
        }

        // Check if the round score is increased by 50
        assertEquals(50, player.score().getRoundScore(), "Round score should increase by 50 on first roll bust");
    }

    @Test
    @DisplayName("Continue Turn with Reroll")
    void continueTurnWithReroll() {
        // Simulate a situation to continue turn with reroll
        player.score().setRoundScore(800);

        gameCoordinator.getGameStateManager().setBust(false);
        gameCoordinator.getGameStateManager().setContinueTurn(true);
        gameCoordinator.getGameStateManager().setReroll(true);

        Future<?> future = executor.submit(() -> gameFlowManager.playTurn(player, null, true)); // false for isTest

        try {
            future.get(2, TimeUnit.SECONDS); // Adjust the time based on expected execution time
            fail("Method did not hang as expected.");
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            assertTrue(true, "Method hanged as expected.");
        } finally {
            future.cancel(true); // Interrupt if still running
        }

        // Check if the round score remains unchanged (as we're not processing any score changes)
        assertEquals(800, player.score().getRoundScore(), "Round score should remain unchanged after reroll");
    }
}

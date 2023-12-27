package managers;

import models.Player;
import models.Score;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static models.Dice.FULL_SET_OF_DICE;

public class GameFlowManager {
    private final GameCoordinator gameCoordinator;

    /**
     * Constructs a GameFlowManager with a reference to the GameCoordinator.
     *
     * @param gameCoordinator The GameCoordinator instance managing the game's flow.
     */
    public GameFlowManager(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
    }


    ///   Main Functions   ///

    /**
     * Manages a player's turn in the game.
     *
     * @param player               The player whose turn is being managed.
     * @param gameEndingPlayerName Name of the player who triggered the game's ending.
     * @param isTest               Flag to indicate if it's a test run.
     */
    public void playTurn(Player player, String gameEndingPlayerName, boolean isTest) {
        while (true) {
            // Handle end-game scenario display
            if (gameEndingPlayerName != null) {
                gameCoordinator.getGameplayUI().displayHighScoreInfo(player, gameEndingPlayerName);
            }

            // Check for end-of-turn conditions
            if (gameCoordinator.getGameStateManager().isBust() || !gameCoordinator.getGameStateManager().getContinueTurn()) {
                break;
            }

            // Handle dice rolling logic
            if (gameCoordinator.getGameStateManager().getReroll()) {
                handleDiceRoll(player);
            }

            // Display current score and dice
            gameCoordinator.getGameplayUI().displayCurrentScore(gameCoordinator.getPlayerManager().getCurrentPlayer());
            gameCoordinator.getGameplayUI().displayDice();

            // Evaluate and handle available game options
            handleGameOptions();

            // User interaction for option selection
            if (!isTest) {
                gameCoordinator.getUserInteractionManager().inputGameOption(new Scanner(System.in));
            }
        }

        // Update player's permanent score at the end of their turn
        if (!gameCoordinator.getGameStateManager().isBust()) {
            updatePermanentScore(player);
        }
    }

    /**
     * Updates the permanent score of the player based on the round score.
     *
     * @param player The player whose score needs to be updated.
     */
    private void updatePermanentScore(Player player) {
        Score score = player.score();
        if (score.getRoundScore() >= 1000) {
            score.increasePermanentScore(score.getRoundScore());
            score.setRoundScore(0);
        }
    }

    /**
     * Checks if the game's end condition has been met by a player.
     *
     * @param player The player to check for end condition.
     * @return true if the end condition is met, false otherwise.
     */
    public boolean checkGameEndCondition(Player player) {
        return player.score().getPermanentScore() >= player.score().getScoreLimit();
    }

    /**
     * Handles the end of the game.
     *
     * @param isTest Flag to indicate if it's a test run.
     */
    public void handleGameEnd(boolean isTest) {
        Player gameEndingPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        gameCoordinator.getGameplayUI().displayLastRoundMessage(gameEndingPlayer);
        gameCoordinator.getPlayerManager().switchToNextPlayer();

        handleLastTurns(gameEndingPlayer, isTest);

        manageTiedEnding();
    }


    ///   Helper Functions   ///

    // Handles the tied ending scenario, determining if there's a tie and announcing the outcome.
    private void manageTiedEnding() {
        // Find the player with the highest score
        Player playerWithHighestScore = gameCoordinator.getPlayerManager().findHighestScoringPlayer();
        int highestScore = playerWithHighestScore.score().getPermanentScore();

        // Get the list of players who are tied for the highest score
        List<Player> tiedPlayers = gameCoordinator.getPlayerManager().getPlayers().stream()
                .filter(player -> player.score().getPermanentScore() == highestScore)
                .collect(Collectors.toList());

        // Announce the outcome of the game based on whether there is a tie or a clear winner
        announceGameOutcome(tiedPlayers, highestScore);
    }

    // Handles the dice rolling logic during a player's turn.
    private void handleDiceRoll(Player player) {
        // Reset the number of dice to 6 if all had been used in previous rolls
        if (player.dice().getNumDiceInPlay() == 0) {
            player.dice().setNumDiceInPlay(FULL_SET_OF_DICE);
        }

        // Roll dice and update game state
        gameCoordinator.getPlayerManager().rollDice();
        gameCoordinator.getGameOptionManager().setOptionSelectedForCurrentRoll(false);
        gameCoordinator.getGameStateManager().setReroll(false);
    }

    // Evaluates and handles available game options based on the current game state.
    private void handleGameOptions() {
        // Evaluate the available options for the current game state
        gameCoordinator.getGameOptionManager().evaluateGameOptions();

        // Handle bust scenarios if no options are available and no option has been selected yet
        if (!gameCoordinator.getRuleManager().isOptionAvailable() && !gameCoordinator.getGameOptionManager().isOptionSelectedForCurrentRoll()) {
            handleBustScenarios();
        }
    }

    // Handles bust scenarios based on the number of dice in play and the current round score.
    private void handleBustScenarios() {
        // Check for a bust scenario where all dice are in play and no score has been achieved
        if (gameCoordinator.getPlayerManager().getCurrentPlayer().dice().getNumDiceInPlay() == FULL_SET_OF_DICE && gameCoordinator.getPlayerManager().getCurrentPlayer().score().getRoundScore() == 0) {
            gameCoordinator.getGameStateManager().handleFirstRollBust();
        }
        // Handle a regular bust scenario where at least one die is in play
        else if (gameCoordinator.getPlayerManager().getCurrentPlayer().dice().getNumDiceInPlay() >= 1) {
            gameCoordinator.getGameStateManager().handleBust();
        }
        // Otherwise, prepare for a reroll by resetting dice and continuing the game
    }

    // Handles the last turns of each player after the game-ending condition is triggered.
    private void handleLastTurns(Player gameEndingPlayer, boolean isTest) {
        do {
            // Display a message indicating whose turn it is
            String playerName = gameCoordinator.getPlayerManager().getCurrentPlayer().name();
            gameCoordinator.getGameplayUI().displayMessage("It is " + playerName + "'s last turn");

            // Initialize the game state for the new turn
            gameCoordinator.getGameStateManager().initializeRollCycle();

            // Play the turn for the current player
            playTurn(gameCoordinator.getPlayerManager().getCurrentPlayer(), gameEndingPlayer.name(), isTest);

            // Switch to the next player in the rotation
            gameCoordinator.getPlayerManager().switchToNextPlayer();
        } while (gameCoordinator.getPlayerManager().getCurrentPlayer() != gameEndingPlayer);
    }

    // Announces the outcome of the game, either a tie or a winner.
    private void announceGameOutcome(List<Player> tiedPlayers, int highestScore) {
        // Check for a tie and announce accordingly
        if (tiedPlayers.size() > 1) {
            gameCoordinator.getGameplayUI().announceTie(tiedPlayers, highestScore);
        }
        // Otherwise, announce the winner
        else {
            gameCoordinator.getGameplayUI().announceWinner(tiedPlayers.getFirst(), highestScore);
        }
    }
}

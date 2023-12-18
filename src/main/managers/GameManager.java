package managers;

import models.Player;
import ui.GameplayUI;

import java.util.Scanner;
import java.util.StringJoiner;
import java.util.Vector;

import static models.Dice.FULL_SET_OF_DICE;

public class GameManager {
    private final GameplayUI gameplayUI;
    private final Vector<Player> players;
    private Player currentPlayer;
    private int valueOfChosenMultiple;
    private boolean selectedOptionStatus;
    private boolean turnContinuationStatus;
    private boolean selectionContinuationStatus;

    public GameManager() {
        this.gameplayUI = new GameplayUI();
        this.players = new Vector<>();
        this.currentPlayer = null;
        this.valueOfChosenMultiple = 0;
        this.selectedOptionStatus = false;
        this.turnContinuationStatus = true;
        this.selectionContinuationStatus = true;
    }

    /*
     * Initialize each segment of 6-dice re-rolls
     */
    public void initializeRollCycle() {
        turnContinuationStatus = true;
        selectedOptionStatus = false; // No option has yet be selected
        selectionContinuationStatus = true; // Can continue/start selecting // TODO: May need to be set to false

        valueOfChosenMultiple = 0;

        // Initialize Score
        currentPlayer.getScore().setScoreFromMultiples(0);
    }

    public void playGame() {
        gameplayUI.displayGameInfo();
        gameplayUI.pauseAndContinue();

        gameplayUI.clear();

        // Setup Players
        enterAndAddPlayers();
        enterAndSetScoreLimit();

        while (true) {
            for (Player player : players) {

                // Check and manage game-end condition
                if (player.getScore().getPermanentScore() >= player.getScore().getScoreLimit()) {
                    manageLastTurnOpportunity();
                    manageTiedEnding();
                    return;
                }

                // Play Turn
                currentPlayer = player;
                manageDiceCount(FULL_SET_OF_DICE);
                while (turnContinuationStatus) {
                    currentPlayer.getDice().rollDice();
                    new MoveValidator(this).check();
                }
            }
        }
    }

    public void manageLastTurnOpportunity() {
        Player gameEndingPlayer = currentPlayer;

        System.out.println(gameEndingPlayer.getName() + " is over " + gameEndingPlayer.getScore().getScoreLimit());
        System.out.println("Everyone else has one more chance to win");
        gameplayUI.pauseAndContinue();
        System.out.println();

        switchToNextPlayer();

        do {
            System.out.println("It is " + currentPlayer.getName() + "'s last turn");
            manageDiceCount(FULL_SET_OF_DICE);

            while (turnContinuationStatus) {
                gameEndingPlayer.getScore().displayHighScoreInfo(currentPlayer.getName(), gameEndingPlayer.getName());
                currentPlayer.getDice().rollDice();
                new MoveValidator(this).check();
            }

            switchToNextPlayer();
        } while (currentPlayer != gameEndingPlayer);
    }

    public void manageTiedEnding() {
        // Find the player with the highest score
        Player playerWithHighestScore = findHighestScoringPlayer();
        int highestScore = playerWithHighestScore.getScore().getPermanentScore();

        // Create a vector to track players who are tied
        Vector<Player> tie = new Vector<>();

        // Add players with the highest score to the tie vector
        for (Player player : players) {
            if (player.getScore().getPermanentScore() == highestScore) {
                tie.add(player);
            }
        }

        // Check if there is more than one player with the highest score
        if (tie.size() > 1) {
            StringJoiner joiner = new StringJoiner(", ", "", " have tied with " + highestScore + " Points!");
            tie.forEach(player -> joiner.add(player.getName()));

            // Print the names of the players who are tied
            System.out.println(joiner);
        } else {
            // Announce the winner if there is no tie
            System.out.println(playerWithHighestScore.getName() + " won with " + highestScore + " Points!");
        }
    }

    /*******************
     *   DICE HELPERS   *
     *******************/

    public void manageDiceCount(int numOfDice) {
        if (numOfDice == 0)
            numOfDice = FULL_SET_OF_DICE;

        currentPlayer.getDice().setNumDiceInPlay(numOfDice);

        if (numOfDice == FULL_SET_OF_DICE)
            initializeRollCycle();
    }

    /*****************************
     *   PLAYER SETUP FUNCTIONS   *
     *****************************/

    ///   Player Setup   ///
    private int getNumberOfPlayers() {
        Scanner scanner = new Scanner(System.in);
        int numPlayers;
        while (true) {
            System.out.print("Enter the number of players (1-6): ");
            numPlayers = scanner.nextInt();

            if (numPlayers >= 1 && numPlayers <= 6) {
                break;
            }

            System.out.println("Invalid number of players. Please try again.");
            scanner.nextLine(); // Clear buffer
        }
        return numPlayers;
    }

    private void enterAndAddPlayers() {
        int numPlayers = getNumberOfPlayers();

        players.clear(); // Clears any previous players
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter the name of player " + (i + 1) + ": ");
            String playerName = scanner.next();
            addPlayer(playerName);
        }

        // Initialize currentPlayer to be the first player
        currentPlayer = players.getFirst();
    }

    private void addPlayer(String playerName) {
        players.add(new Player(playerName)); // Pass this GameManager instance to the player
    }

    ///   Player Sequencing   ///
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void switchToNextPlayer() {
        int currentPlayerIndex = players.indexOf(currentPlayer);
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);
    }

    ///   Score Limit and Highest Score   ///
    private void enterAndSetScoreLimit() {
        Scanner scanner = new Scanner(System.in);
        int limit;
        final int MIN_SCORE_LIMIT = 1000;

        while (true) {
            System.out.print("\nEnter the score limit (minimum " + MIN_SCORE_LIMIT + "): ");
            limit = scanner.nextInt();

            if (limit >= MIN_SCORE_LIMIT) {
                break;
            }

            System.out.println("Invalid score limit. Please try again.");
            scanner.nextLine();
        }

        for (Player player : players)
            player.getScore().setScoreLimit(limit);
    }

    public Player findHighestScoringPlayer() {
        Player highestScoringPlayer = currentPlayer;
        for (Player player : players) {
            if (player.getScore().getPermanentScore() > highestScoringPlayer.getScore().getPermanentScore()) {
                highestScoringPlayer = player;
            }
        }
        return highestScoringPlayer;
    }

    /****************************
     *   GET AND SET FUNCTIONS   *
     ****************************/

    ///   Chosen Multiple   ///
    public int getValueOfChosenMultiple() {
        return valueOfChosenMultiple;
    }

    public void setValueOfChosenMultiple(int chosenMultipleValue) {
        valueOfChosenMultiple = chosenMultipleValue;
    }

    ///   Option Status   ///
    public boolean getSelectedOptionStatus() {
        return selectedOptionStatus;
    }

    public void setSelectedOptionStatus(boolean isOptionSelected) {
        selectedOptionStatus = isOptionSelected;
    }

    ///   Turn Continuation Status   ///
    public boolean getTurnContinuationStatus() {
        return turnContinuationStatus;
    }

    public void setTurnContinuationStatus(boolean continueTurn, boolean isBust) {
        if (isBust || currentPlayer.getScore().getRoundScore() >= 1000) {
            turnContinuationStatus = continueTurn;
        } else {
            System.out.println("You are not allowed to end without a permanent or running score higher than 1000");
            turnContinuationStatus = true;
        }
    }

    ///   Selection Status   ///
    public boolean getSelectionContinuationStatus() {
        return selectionContinuationStatus;
    }

    public void setSelectionContinuationStatus(boolean continueSelecting) {
        if (!continueSelecting && !selectedOptionStatus) {
            System.out.println("You must select at least one option");
            continueSelecting = true;
        }
        selectionContinuationStatus = continueSelecting;
    }

    public GameplayUI getGameplayUI() {
        return gameplayUI;
    }

    /************************
     *   ENUM FOR PRINTING   *
     ************************/
    public enum printOptions {
        ENTER, NEXT, REENTER
    }
}

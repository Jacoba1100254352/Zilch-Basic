package ui;

import managers.GameCoordinator;
import models.*;
import rules.RuleManager;

import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;

import static managers.GameCoordinator.printOptions.*;

public class GameplayUI {
    private final GameCoordinator gameCoordinator;
    private final RuleManager ruleManager;

    public GameplayUI(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
        this.ruleManager = gameCoordinator.getRuleManager();
    }

    // Display game information
    public void displayWelcomeMessage() {
        clear();
        System.out.println("""
                Welcome to Zilch!

                Here are the basic rules:
                1. You must score an initial 1000 points to start logging your points.
                2. Sets (three sets of two) and straits (1, 2, 3, 4, 5, 6) give 1000 points.
                3. A group of 3 identical dice give you 100 points times the value of that die. For example:
                \t- A roll of 3 3 3 will give you 300 points.
                \t- An exception is made for a roll of 1 1 1, which will give you 1000 points.
                4. Each additional number added to this group of three doubles the points received from it. For example:
                \t- A roll of 3 3 3 3 or a roll of 3 3 3 and a later roll of 3 in the same turn would give you 600 points.
                \t- If in a group of 3 1's, 2 more 1's are rolled, the score would be 1000*2*2 = 4000.
                5. Finally, only single 1's or 5's are worth points with a single 1 being 100 points and a 5 being 50.
                """);
    }

    // Print instructions based on the game state
    public void printInstructions(GameCoordinator.printOptions options) {
        displayCurrentScore(gameCoordinator.getPlayerManager().getCurrentPlayer());
        displayDice();

        String message = generateMessageBasedOnGameState();
        message = modifyMessageBasedOnOptions(message, options);
        System.out.print(generateInstructions() + message);
    }

    public void displayCurrentScore(Player currentPlayer) {
        System.out.println(currentPlayer.name() + "'s current score: " + currentPlayer.score().getRoundScore());
    }

    public void displayHighScoreInfo(Player currentPlayer, String highestScoringPlayerName) {
        String currentPlayerName = currentPlayer.name();
        Score score = currentPlayer.score();

        ///   if: High permanent score is below the limit: Ask to end or keep playing   ///
        int highestScore = score.getPermanentScore();
        if (highestScore < score.getScoreLimit())
            displayCurrentScore(currentPlayer);
            ///   else if: Someone has surpassed the limit: try to beat them   ///
        else if (highestScore > score.getRoundScore()) {
            System.out.print("\n\nYour current score of " + score.getRoundScore() + " is " + (highestScore - score.getRoundScore()));
            System.out.println(" less than " + currentPlayerName + "'s High Score of " + highestScore + " so keep going! :)");
            ///   else if: Tied for the highest   ///
        } else if (!highestScoringPlayerName.equals(currentPlayerName))
            System.out.print("You are currently tied with the highest scoring player!");
            ///   else: You are the highest! Ask to end or keep playing   ///
        else System.out.print("You are currently the highest scoring player");
    }

    private String generateInstructions() {
        return "\tSingles -- s#, \n\tMultiples -- m#, \n\tSet -- se, \n\tStrait -- st;\n";
    }

    private String generateMessageBasedOnGameState() {
        Player currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        boolean optionSelected = gameCoordinator.getGameStateManager().getSelectedOptionStatus();
        int roundScore = currentPlayer.score().getRoundScore();

        String message = "Enter the option you wish to take";
        if (optionSelected && roundScore >= 1000) {
            message += ", or type 0 to end your turn: ";
        } else if (optionSelected) {
            message += ", or type 0 to roll again: ";
        } else {
            message += ": ";
        }
        return message;
    }

    private String modifyMessageBasedOnOptions(String message, GameCoordinator.printOptions options) {
        return switch (options) {
            case ENTER -> message;
            case NEXT -> message.replaceFirst("Enter", "Please enter your next choice");
            case REENTER -> message.replaceFirst("Enter", "Please re-enter the option you wish to take");
        };
    }

    // Clear the console
    public void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Pause and wait for the user to continue
    public void pauseAndContinue() {
        System.out.print("\nPress enter to continue... ");
        new Scanner(System.in).nextLine(); // Wait for the user to press Enter
        clear();
    }

    // Handles '?' command
    public void displayPossibleOptions() {
        System.out.println("Possible options: ");
        if (ruleManager.isStrait())
            System.out.println("\tStrait");
        if (ruleManager.isSet())
            System.out.println("\tSets");
        if (ruleManager.isMultiple()) {
            for (int fst : gameCoordinator.getPlayerManager().getCurrentPlayer().dice().diceSetMap().keySet()) {
                if (ruleManager.isDesiredMultipleAvailable(fst))
                    System.out.println("\tMultiples, value: " + fst);
            }
        }
        if (ruleManager.canAddMultiples())
            System.out.println("\tAddons, value: " + gameCoordinator.getGameOptionManager().getCurrentGameOption().value());
        if (ruleManager.isSingle(1) || ruleManager.isSingle(5))
            System.out.println("\tSingles");
        pauseAndContinue();
    }

    public void displayUIElements() {
        clear(); // Clear screen.
        if (ruleManager.canProcessMultiple(gameCoordinator.getGameOptionManager().getCurrentGameOption().value())) {
            printPossibleMultipleAddition();
        }

        if (!gameCoordinator.getGameStateManager().getSelectedOptionStatus()) {
            printInstructions(ENTER);
        } else if (ruleManager.isOptionAvailable()) {
            printInstructions(NEXT);
        }
    }

    public void displayDice() {
        Dice dice = gameCoordinator.getPlayerManager().getCurrentPlayer().dice();
        System.out.println("\nYou have " + dice.getNumDiceInPlay() + " dice left.");

        ///   Build and Print Dice List String   ///
        StringBuilder diceList = new StringBuilder();
        dice.diceSetMap().forEach((key, value) -> {
            for (int i = 0; i < value; i++) {
                diceList.append(key).append(", ");
            }
        });

        // Remove the trailing comma and space
        if (!diceList.isEmpty()) {
            diceList.setLength(diceList.length() - 2);
        }

        System.out.println(diceList);
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    // Display the common impossible option message
    public void displayImpossibleOptionMessage() {
        clear();
        System.out.println("You have selected an impossible option");
    }

    public void displayFailedMultipleMessage(int chosenMultiple) {
        System.out.println("You have selected an impossible option");

        if (chosenMultiple != 0) {
            for (int key : gameCoordinator.getPlayerManager().getCurrentPlayer().dice().diceSetMap().keySet()) {
                if (ruleManager.isDesiredMultipleAvailable(key))
                    System.out.println("\tMultiples, value: " + key);
                else if (gameCoordinator.getGameOptionManager().getCurrentGameOption().value() == key)
                    System.out.println("Add to Multiple, value: " + key);
            }
        } else {
            clear();
            System.out.println("There is no available multiple");
        }
    }

    public void printPossibleMultipleAddition() {
        if (ruleManager.canAddMultiples() && (gameCoordinator.getPlayerManager().getCurrentPlayer().score().getScoreFromMultiples() >= 200))
            System.out.println("You can add to your multiple of " + gameCoordinator.getGameOptionManager().getCurrentGameOption().value() + "!");
    }

    public void displayLastRoundMessage(Player gameEndingPlayer) {
        System.out.println(gameEndingPlayer.name() + " is over " + gameEndingPlayer.score().getScoreLimit());
        System.out.println("Everyone else has one more chance to win");
        pauseAndContinue();
        System.out.println();
    }

    public void displayLastTurnMessage(String playerName) {
        System.out.println("It is " + playerName + "'s last turn");
    }

    public void announceTie(List<Player> tiedPlayers, int score) {
        StringJoiner joiner = new StringJoiner(", ", "", " have tied with " + score + " Points!");
        tiedPlayers.forEach(player -> joiner.add(player.name()));
        System.out.println(joiner);
    }

    public void announceWinner(Player winner, int score) {
        System.out.println(winner.name() + " won with " + score + " Points!");
    }
}

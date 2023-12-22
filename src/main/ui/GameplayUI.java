package ui;

import managers.GameCoordinator;
import models.Dice;
import models.GameOption;
import models.Player;
import models.Score;

import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;

public class GameplayUI {
    private final GameCoordinator gameCoordinator;

    public GameplayUI(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
    }

    public void displayWelcomeMessage() {
        clear();
        System.out.println(getWelcomeMessage());
    }


    ///   Main Functions   ///

    public void displayCurrentScore(Player currentPlayer) {
        System.out.println(currentPlayer.name() + "'s current score: " + currentPlayer.score().getRoundScore());
    }

    public void displayHighScoreInfo(Player currentPlayer, String highestScoringPlayerName) {
        System.out.print(generateHighScoreMessage(currentPlayer, highestScoringPlayerName));
    }

    public void displayPossibleOptions() {
        System.out.println("Possible options: ");
        printOptionsList();
        pauseAndContinue();
    }

    public void displayUIElements() {
        clear();
        printPossibleMultipleAddition();
        printInstructions();
    }

    public void displayDice() {
        Dice dice = gameCoordinator.getPlayerManager().getCurrentPlayer().dice();
        System.out.println("\nYou have " + dice.getNumDiceInPlay() + " dice left.");
        System.out.println(buildDiceListString(dice));
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayImpossibleOptionMessage() {
        clear();
        System.out.println("You have selected an impossible option");
    }

    public void displayLastRoundMessage(Player gameEndingPlayer) {
        System.out.println(gameEndingPlayer.name() + " is over " + gameEndingPlayer.score().getScoreLimit());
        System.out.println("Everyone else has one more chance to win");
        pauseAndContinue();
        System.out.println();
    }

    public void displayFailedMultipleMessage() {
        System.out.println("You have selected an impossible option");

        List<GameOption> gameOptions = gameCoordinator.getGameOptionManager().getGameOptions();

        gameOptions.stream().filter(gameOption -> gameOption.type() == GameOption.Type.MULTIPLE).map(gameOption -> "You can choose the multiple " + gameOption.value()).forEach(System.out::println);
        printPossibleMultipleAddition();
    }

    public void displayLastTurnMessage(String playerName) {
        System.out.println("It is " + playerName + "'s last turn");
    }

    public void announceTie(List<Player> tiedPlayers, int score) {
        System.out.println(buildTieAnnouncement(tiedPlayers, score));
    }

    public void announceWinner(Player winner, int score) {
        System.out.println(winner.name() + " won with " + score + " Points!");
    }

    public void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void pauseAndContinue() {
        System.out.print("\nPress enter to continue... ");
        new Scanner(System.in).nextLine();
        clear();
    }

    private String getWelcomeMessage() {
        return """
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
                """;
    }


    ///   Helper Functions   ///

    private void printInstructions() {
        displayCurrentScore(gameCoordinator.getPlayerManager().getCurrentPlayer());
        displayDice();
        System.out.print(generateInstructions());
    }

    private String generateInstructions() {
        PrintOptions options = determinePrintOption();
        String message = generateMessageBasedOnGameState();
        return modifyMessageBasedOnOptions(message, options) + generateCommandOptions();
    }

    private String generateCommandOptions() {
        return "\tSingles -- s#, \n\tMultiples -- m#, \n\tSet -- se, \n\tStrait -- st;\n";
    }

    private String buildDiceListString(Dice dice) {
        StringBuilder diceList = new StringBuilder();
        dice.diceSetMap().forEach((key, value) -> diceList.append(key).append(" (").append(value).append("), "));
        return !diceList.isEmpty() ? diceList.substring(0, diceList.length() - 2) : "";
    }

    private String generateHighScoreMessage(Player currentPlayer, String highestScoringPlayerName) {
        StringBuilder message = new StringBuilder();
        Score score = currentPlayer.score();

        if (score.getPermanentScore() < score.getScoreLimit()) {
            message.append(currentPlayer.name()).append("'s current score: ").append(score.getRoundScore());
        } else if (!highestScoringPlayerName.equals(currentPlayer.name()) && score.getPermanentScore() > score.getRoundScore()) {
            message.append("\n\nYour current score of ").append(score.getRoundScore())
                    .append(" is ").append(score.getPermanentScore() - score.getRoundScore())
                    .append(" less than ").append(highestScoringPlayerName)
                    .append("'s High Score of ").append(score.getPermanentScore())
                    .append(". Keep going! :)");
        } else if (!highestScoringPlayerName.equals(currentPlayer.name())) {
            message.append("You are currently tied with the highest scoring player!");
        } else {
            message.append("You are currently the highest scoring player.");
        }

        return message.toString();
    }

    private PrintOptions determinePrintOption() {
        return gameCoordinator.getGameStateManager().isOptionSelected() ? PrintOptions.NEXT : PrintOptions.ENTER;
    }

    private void printOptionsList() {
        List<GameOption> gameOptions = gameCoordinator.getGameOptionManager().getGameOptions();
        gameOptions.forEach(gameOption ->
                System.out.println(switch (gameOption.type()) {
                    case STRAIT -> "\tStrait";
                    case SET -> "\tSet";
                    case SINGLE -> "\tSingle " + gameOption.value();
                    case MULTIPLE ->
                            (gameCoordinator.getRuleManager().canAddMultiples()) ? "\tAdd to Multiple" : "\tMultiples";
                }));
        System.out.println("\t0 to end turn or roll again");
        System.out.println("\t? to see options");
    }

    private void printPossibleMultipleAddition() {
        if (gameCoordinator.getRuleManager().canAddMultiples()) {
            gameCoordinator.getGameOptionManager().getGameOptions().stream().filter(gameOption -> gameOption.type() == GameOption.Type.MULTIPLE).map(gameOption -> "You can add to your multiple of " + gameOption.value()).forEach(System.out::println);
        }
    }

    private String buildTieAnnouncement(List<Player> tiedPlayers, int score) {
        StringJoiner joiner = new StringJoiner(", ", "", " have tied with " + score + " Points!");
        tiedPlayers.forEach(player -> joiner.add(player.name()));
        return joiner.toString();
    }

    private String generateMessageBasedOnGameState() {
        Player currentPlayer = gameCoordinator.getPlayerManager().getCurrentPlayer();
        boolean optionSelected = gameCoordinator.getGameStateManager().isOptionSelected();
        int roundScore = currentPlayer.score().getRoundScore();
        StringBuilder message = new StringBuilder("Enter the option you wish to take");

        if (optionSelected && roundScore >= 1000) {
            message.append(", or type 0 to end your turn: ");
        } else if (optionSelected) {
            message.append(", or type 0 to roll again: ");
        } else {
            message.append(": ");
        }

        return message.toString();
    }

    private String modifyMessageBasedOnOptions(String message, PrintOptions options) {
        return switch (options) {
            case ENTER -> message;
            case NEXT -> message.replaceFirst("Enter", "Please enter your next choice");
            case REENTER -> message.replaceFirst("Enter", "Please re-enter the option you wish to take");
        };
    }

    private enum PrintOptions {
        ENTER, NEXT, REENTER
    }
}

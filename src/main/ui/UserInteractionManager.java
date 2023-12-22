package ui;

import managers.GameCoordinator;
import models.Score;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserInteractionManager {
    private final GameCoordinator gameCoordinator;

    public UserInteractionManager(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
    }

    public static void ignoreRemainingInput(Scanner scanner) {
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }


    ///   Main Functions   ///

    public int getNumberOfPlayers(Scanner scanner) {
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

    public List<String> getPlayerNames() {
        List<String> playerNames = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        int numPlayers = getNumberOfPlayers(scanner);

        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter the name of player " + (i + 1) + ": ");
            String playerName = scanner.next();
            playerNames.add(playerName);
        }

        return playerNames;
    }

    public int getValidScoreLimit() {
        Scanner scanner = new Scanner(System.in);
        final int MIN_SCORE_LIMIT = 1000;
        int limit;

        while (true) {
            System.out.print("\nEnter the score limit (minimum " + MIN_SCORE_LIMIT + "): ");
            limit = scanner.nextInt();

            if (limit >= MIN_SCORE_LIMIT) {
                break;
            }

            System.out.println("Invalid score limit. Please try again.");
            scanner.nextLine(); // Clears the buffer.
        }
        return limit;
    }

    public boolean enterEndTurnOption(Scanner scanner, Score playerScore) {
        // Variables
        int playOrEndTurn;

        // Enter Decision
        System.out.print("Type 2 to end turn, 1 to continue selecting or 0 to roll again: ");
        while (!scanner.hasNextInt() || (playOrEndTurn = scanner.nextInt()) < 0 || playOrEndTurn > 2) {
            // Handle incorrect input
            System.out.print("Invalid input. Type 2 to end turn, 1 to continue selecting or 0 to roll again: ");
            ignoreRemainingInput(scanner); // Clear the buffer
        }

        return playOrEndTurn == 2 && playerScore.getRoundScore() >= 1000;
    }

    public void inputGameOption(Scanner scanner) {
        ignoreRemainingInput(scanner); // Clear remaining input.

        char ch = readValidCommand(scanner);

        switch (ch) {
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
                gameCoordinator.getGameOptionManager().processOptionInt(Character.getNumericValue(ch));
                break;
            case 's':
                gameCoordinator.getGameOptionManager().processOptionS(scanner.next().charAt(0));
                break;
            case 'a':
                gameCoordinator.getGameOptionManager().processOptionA(scanner);
                // Falls through to case 'm' intentionally
            case 'm':
                gameCoordinator.getGameOptionManager().processOptionM(scanner.nextInt());
                break;
            case '0':
                gameCoordinator.getGameStateManager().processZeroCommand();
                break;
            case '?':
                gameCoordinator.getGameplayUI().displayPossibleOptions();
                break;
            default:
                gameCoordinator.getGameplayUI().displayImpossibleOptionMessage();
                break;
        }
    }


    ///   Helper Functions   ///

    private char readValidCommand(Scanner input) {
        String validCommands = "stel123456am0?";
        String pattern = "[" + validCommands + "]";

        String next;
        do {
            next = input.next();
        } while (!next.matches(pattern));

        return next.charAt(0);
    }
}

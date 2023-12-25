package ui;

import managers.GameCoordinator;
import managers.GameStateManager;
import models.GameOption;
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
            gameCoordinator.getGameplayUI().displayMessage("Enter the number of players (1-6): ");
            numPlayers = scanner.nextInt();

            if (numPlayers >= 1 && numPlayers <= 6) {
                break;
            }

            gameCoordinator.getGameplayUI().displayMessage("Invalid number of players. Please try again.");
            scanner.nextLine(); // Clear buffer
        }
        return numPlayers;
    }

    public List<String> getPlayerNames() {
        List<String> playerNames = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        int numPlayers = getNumberOfPlayers(scanner);

        for (int i = 0; i < numPlayers; i++) {
            gameCoordinator.getGameplayUI().displayMessage("Enter the name of player " + (i + 1) + ": ");
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
            gameCoordinator.getGameplayUI().displayMessage("\nEnter the score limit (minimum " + MIN_SCORE_LIMIT + "): ");
            limit = scanner.nextInt();

            if (limit >= MIN_SCORE_LIMIT) {
                break;
            }

            gameCoordinator.getGameplayUI().displayMessage("Invalid score limit. Please try again.");
            scanner.nextLine(); // Clears the buffer.
        }
        return limit;
    }

    public void inputGameOption(Scanner scanner) {
        List<GameOption> gameOptions = gameCoordinator.getGameOptionManager().getGameOptions();
        GameStateManager gameStateManager = gameCoordinator.getGameStateManager();

        gameCoordinator.getGameplayUI().displayGameOptions(gameOptions,
                gameStateManager.getContinueTurn() && gameStateManager.getContinueSelecting(),
                !gameStateManager.getContinueTurn());

        gameCoordinator.getGameplayUI().displayMessage("Enter choice: ");
        int choice = scanner.nextInt();

        if (choice == 0) {
            // Handle help option
            gameCoordinator.getGameplayUI().displayPossibleOptions();
        } else if (choice == gameOptions.size() + 1 && !gameStateManager.getContinueTurn()) {
            // Handle roll again
            gameStateManager.setContinueTurn(true, false);
        } else if (choice == gameOptions.size() + 2 && gameStateManager.getContinueTurn()) {
            // Handle end turn
            gameStateManager.setContinueTurn(false, true);
        } else if (choice > 0 && choice <= gameOptions.size()) {
            GameOption selectedOption = gameOptions.get(choice - 1);
            gameCoordinator.getGameOptionManager().setSelectedGameOption(selectedOption);
            gameCoordinator.getGameOptionManager().processMove();
        } else {
            gameCoordinator.getGameplayUI().displayMessage("Invalid choice. Please try again.");
        }
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
}

package ui;

import managers.GameCoordinator;
import models.GameOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserInteractionManager {
    private final GameCoordinator gameCoordinator;

    public UserInteractionManager(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
    }


    ///   Main Functions   ///

    public int getNumberOfPlayers(Scanner scanner) {
        int numPlayers;
        while (true) {
            try {
                gameCoordinator.getGameplayUI().displayMessage("Enter the number of players (1-6): ");
                numPlayers = scanner.nextInt();
            } catch (Exception e) {
                gameCoordinator.getGameplayUI().clear();
                gameCoordinator.getGameplayUI().displayMessage("Invalid number of players. Please try again.\n");
                scanner.nextLine(); // Clear buffer
                continue;
            }

            if (numPlayers >= 1 && numPlayers <= 6) {
                break;
            }

            gameCoordinator.getGameplayUI().clear();
            gameCoordinator.getGameplayUI().displayMessage("Invalid number of players. Please try again.\n");
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
            while (true) {
                try {
                    gameCoordinator.getGameplayUI().displayMessage("\nEnter the score limit (minimum " + MIN_SCORE_LIMIT + "): ");
                    limit = scanner.nextInt();
                    break;
                } catch (Exception e) {
                    gameCoordinator.getGameplayUI().clear();
                    gameCoordinator.getGameplayUI().displayMessage("Invalid score limit. Please try again.");
                    scanner.nextLine(); // Clears the buffer.
                }
            }

            if (limit >= MIN_SCORE_LIMIT) {
                break;
            }

            gameCoordinator.getGameplayUI().displayMessage("Invalid score limit. Please try again.");
            scanner.nextLine(); // Clears the buffer.
        }
        return limit;
    }

    public void inputGameOption(Scanner scanner) {
        List<GameOption> gameOptions;
        int choice;

        do {
            try {
                // Retrieve current game options and display them
                gameOptions = gameCoordinator.getGameOptionManager().getGameOptions();
                gameCoordinator.getGameplayUI().displayGameOptions(gameOptions);

                // Prompt for and read the player's choice
                gameCoordinator.getGameplayUI().displayMessage("Enter choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); // Clear the buffer to handle any residual input

                // Process the player's choice based on the game options
                if (0 < choice && choice <= gameOptions.size()) {
                    // Process a valid game option selection
                    GameOption selectedOption = gameOptions.get(choice - 1);
                    gameCoordinator.getGameOptionManager().setSelectedGameOption(selectedOption);
                    gameCoordinator.getGameOptionManager().setOptionSelectedForCurrentRoll(true);
                    gameCoordinator.getGameOptionManager().processMove();
                    break; // Exit the loop on valid choice
                } else if (choice == gameOptions.size() + 1 && gameCoordinator.getGameOptionManager().isOptionSelectedForCurrentRoll()) {
                    // Handle the decision to roll again
                    gameCoordinator.getGameStateManager().setReroll(true);
                    break;
                } else if (choice == gameOptions.size() + 2) {
                    // Handle the decision to end the turn
                    gameCoordinator.getGameStateManager().setReroll(false);
                    gameCoordinator.getGameStateManager().setContinueTurn(false);
                    break;
                } else {
                    // Handle invalid choice input
                    throw new IllegalArgumentException("Invalid choice. Please try again.");
                }
            } catch (IllegalArgumentException e) {
                // Display message for invalid game option choice
                gameCoordinator.getGameplayUI().displayMessage(e.getMessage());
            } catch (Exception e) {
                // Handle and display message for general input errors
                gameCoordinator.getGameplayUI().displayMessage("Error: Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the buffer to reset for next input
            }
        } while (true); // Loop until a valid choice is made or an action is taken
    }
}

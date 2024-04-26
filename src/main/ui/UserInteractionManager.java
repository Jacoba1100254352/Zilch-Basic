package ui;


import modelManagers.GameOptionManager;
import models.GameOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Manages user interactions during the game, handling inputs for player details and game decisions.
 */
public class UserInteractionManager
{
	private final ConsoleGameplayUI consoleGameplayUI;
	private final GameOptionManager gameOptionManager; // Added to manage game options
	
	public UserInteractionManager(ConsoleGameplayUI consoleGameplayUI, GameOptionManager gameOptionManager) {
		this.consoleGameplayUI = consoleGameplayUI;
		this.gameOptionManager = gameOptionManager; // Initialize with GameEngine
	}
	
	public int getNumberOfPlayers(Scanner scanner) {
		int numPlayers;
		while (true) {
			try {
				consoleGameplayUI.displayMessage("Enter the number of players (1-6): ");
				numPlayers = scanner.nextInt();
			} catch (Exception e) {
				consoleGameplayUI.clear();
				consoleGameplayUI.displayMessage("Invalid number of players. Please try again.\n");
				scanner.nextLine(); // Clear buffer
				continue;
			}
			
			if (numPlayers >= 1 && numPlayers <= 6) {
				break;
			}
			
			consoleGameplayUI.clear();
			consoleGameplayUI.displayMessage("Invalid number of players. Please try again.\n");
			scanner.nextLine(); // Clear buffer
		}
		return numPlayers;
	}
	
	public List<String> getPlayerNames(Scanner scanner) {
		List<String> playerNames = new ArrayList<>();
		int numPlayers = getNumberOfPlayers(scanner);
		
		for (int i = 0; i < numPlayers; i++) {
			consoleGameplayUI.displayMessage("Enter the name of player " + (i + 1) + ": ");
			String playerName = scanner.next();
			playerNames.add(playerName);
		}
		
		return playerNames;
	}
	
	public int getValidScoreLimit(Scanner scanner) {
		final int MIN_SCORE_LIMIT = 1000;
		int limit;
		
		while (true) {
			try {
				consoleGameplayUI.displayMessage("\nEnter the score limit (minimum " + MIN_SCORE_LIMIT + "): ");
				limit = scanner.nextInt();
				if (limit >= MIN_SCORE_LIMIT) {
					break;
				} else {
					consoleGameplayUI.displayMessage("Invalid score limit. Please try again.");
				}
			} catch (Exception e) {
				consoleGameplayUI.clear();
				consoleGameplayUI.displayMessage("Invalid score limit. Please try again.");
				scanner.nextLine(); // Clears the buffer.
			}
		}
		return limit;
	}
	
	public void inputGameOption(Scanner scanner) {
		List<GameOption> gameOptions;
		int choice;
		
		do {
			// Retrieve current game options and display them
			
			gameOptions = gameOptionManager.getGameOptions();
			consoleGameplayUI.displayGameOptions(gameOptions, gameOptionManager.isOptionSelected());
			
			// Prompt for and read the player's choice
			consoleGameplayUI.displayMessage("Enter choice: ");
			choice = scanner.nextInt();
			scanner.nextLine(); // Clear the buffer to handle any residual input
			
			if (0 < choice && choice <= gameOptions.size()) {
				// Process a valid game option selection
				GameOption selectedOption = gameOptions.get(choice - 1);
				gameOptionManager.setSelectedGameOption(selectedOption);
				gameOptionManager.applyGameOption(selectedOption); // Updated to process within GameEngine
				break; // Exit the loop on valid choice
			} else {
				// Handle invalid choice input
				consoleGameplayUI.displayMessage("Invalid choice. Please try again.");
			}
		} while (true); // Loop until a valid choice is made
	}
}

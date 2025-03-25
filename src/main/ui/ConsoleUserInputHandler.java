package ui;


import managers.GameCoordinator;
import models.GameOption;
import models.Score;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * A real user-input handler that uses the console (System.in).
 */
public class ConsoleUserInputHandler implements UserInputHandler
{
	
	private final GameCoordinator gameCoordinator;
	private final Scanner scanner;
	
	public ConsoleUserInputHandler(GameCoordinator gameCoordinator) {
		this.gameCoordinator = gameCoordinator;
		// You can share one Scanner or make a new one as needed
		this.scanner = new Scanner(System.in);
	}
	
	@Override
	public int getValidScoreLimit() {
		final int MIN_SCORE_LIMIT = 1000;
		int limit;
		while (true) {
			try {
				gameCoordinator.getGameplayUI().displayMessage(
						"\nEnter the score limit (minimum " + MIN_SCORE_LIMIT + "): ");
				limit = scanner.nextInt();
				scanner.nextLine(); // clear buffer
				
				if (limit >= MIN_SCORE_LIMIT) {
					System.out.println();
					break;
				} else {
					gameCoordinator.getGameplayUI().displayMessage(
							"Invalid score limit. Must be at least " + MIN_SCORE_LIMIT + ".\n");
				}
			} catch (Exception e) {
				gameCoordinator.getGameplayUI().clear();
				gameCoordinator.getGameplayUI().displayMessage("Invalid score limit. Please try again.\n");
				scanner.nextLine(); // clear buffer
			}
		}
		return limit;
	}
	
	@Override
	public List<String> getPlayerNames() {
		List<String> playerNames = new ArrayList<>();
		int numPlayers;
		
		// Example: getNumberOfPlayers
		while (true) {
			try {
				gameCoordinator.getGameplayUI().displayMessage(
						"Enter the number of players (1-6): ");
				numPlayers = scanner.nextInt();
				scanner.nextLine(); // clear buffer
				
				if (numPlayers >= 1 && numPlayers <= 6) {
					break;
				} else {
					gameCoordinator.getGameplayUI().displayMessage(
							"Invalid number of players. Please try again.\n");
				}
			} catch (Exception e) {
				gameCoordinator.getGameplayUI().clear();
				gameCoordinator.getGameplayUI().displayMessage(
						"Invalid number of players. Please try again.\n");
				scanner.nextLine(); // clear buffer
			}
		}
		
		// Now prompt for each name
		for (int i = 0; i < numPlayers; i++) {
			gameCoordinator.getGameplayUI().displayMessage("Enter the name of player " + (i + 1) + ": ");
			String playerName = scanner.next();
			playerNames.add(playerName);
		}
		return playerNames;
	}
	
	@Override
	public void inputGameOption() {
		// This is the logic from your original inputGameOption() method,
		// except you no longer pass in a Scanner argument, because we have
		// a 'scanner' field right here.
		while (true) {
			try {
				// Retrieve current game options
				List<GameOption> gameOptions = gameCoordinator
						.getGameOptionManager()
						.getGameOptions();
				
				// Display them
				gameCoordinator.getGameplayUI().displayGameOptions(gameOptions);
				
				// Prompt for choice
				gameCoordinator.getGameplayUI().displayMessage("Enter choice: ");
				int choice = scanner.nextInt();
				scanner.nextLine(); // Clear buffer
				
				// Process choice
				if (0 < choice && choice <= gameOptions.size()) {
					// Valid game-option selection
					GameOption selectedOption = gameOptions.get(choice - 1);
					gameCoordinator.getGameOptionManager().setSelectedGameOption(selectedOption);
					gameCoordinator.getGameOptionManager().setOptionSelectedForCurrentRoll(true);
					gameCoordinator.getGameOptionManager().processMove();
					
					// Clear after user entry
					gameCoordinator.getGameplayUI().clear();
					break; // done
				} else if (choice == gameOptions.size() + 1
						&& gameCoordinator.getGameOptionManager().isOptionSelectedForCurrentRoll()) {
					// "Roll again"
					gameCoordinator.getGameStateManager().setReroll(true);
					
					// Clear after user entry
					gameCoordinator.getGameplayUI().clear();
					break; // done
				} else if (choice == gameOptions.size() + 2) {
					// "End turn"
					gameCoordinator.getGameStateManager().setReroll(false);
					gameCoordinator.getGameStateManager().setContinueTurn(false);
					
					// Update player's permanent score at the end of their turn
					updatePermanentScore();
					
					// Clear after user entry
					gameCoordinator.getGameplayUI().clear();
					
					gameCoordinator.getGameplayUI().displayMessage("Your score is now " + gameCoordinator
							.getPlayerManager()
							.getCurrentPlayer()
							.score()
							.getPermanentScore() + "\n");
					pauseAndContinue();
					break; // done
				} else {
					throw new IllegalArgumentException("Invalid choice. Please try again.");
				}
			} catch (Exception e) {
				gameCoordinator.getGameplayUI().displayMessage(
						"\nError: " + e.getMessage() + "\nPlease enter a valid number.\n");
				scanner.nextLine(); // Clear buffer
			}
		}
	}
	
	@Override
	public void pauseAndContinue() {
		// This can replace your original "pauseAndContinue()" from GameplayUI
		gameCoordinator.getGameplayUI().displayMessage("\nPress enter to continue... ");
		scanner.nextLine();
		gameCoordinator.getGameplayUI().clear();
	}
	
	/**
	 * Updates the permanent score of the player based on the round score.
	 */
	private void updatePermanentScore() {
		Score score = gameCoordinator.getPlayerManager().getCurrentPlayer().score();
		if (score.getRoundScore() + score.getPermanentScore() >= 1000) {
			score.increasePermanentScore(score.getRoundScore());
			score.setRoundScore(0);
		}
	}
}

package ui;


import model.entities.Dice;
import model.entities.GameOption;
import model.entities.Player;
import model.entities.Score;
import model.managers.GameOptionManager;
import rules.managers.IRuleManager;
import rules.managers.RuleType;

import java.util.*;


/**
 * Manages user interactions during the game, handling inputs for player details and game decisions,
 * and delegating display responsibilities to ConsoleMessage.
 */
public class UserInteractionManager implements IMessage, IUserInteraction
{
	private final IMessage gameplayUI;
	private final IInputManager inputManager;
	
	@SuppressWarnings("unused")
	public UserInteractionManager() {
		this(new ConsoleMessage(), new ConsoleInputManager());
	}
	
	public UserInteractionManager(IMessage gameplayUI, IInputManager inputManager) {
		this.gameplayUI = gameplayUI;
		this.inputManager = inputManager;
	}
	
	public void runGameSetup() {
		gameplayUI.displayWelcomeMessage();
		int numPlayers = getNumberOfPlayers();
		List<String> playerNames = getPlayerNames(numPlayers);
		// Additional setup steps can be added here if needed
	}
	
	@Override
	public void selectRules(IRuleManager ruleManager) {
		Map<RuleType, Boolean> selectedRules = new EnumMap<>(RuleType.class);
		
		System.out.println("Please select the rules you want to enable (yes/no):");
		
		gameplayUI.displayRulesMenu();
		
		// Iterate over the RuleType enum to display options
		for (RuleType ruleType : RuleType.values()) {
			boolean isEnabled = ConsoleHelper.readYesNo("Enable " + ruleType + "?");
			selectedRules.put(ruleType, isEnabled);
		}
		
		Set<Integer> singleValues = ruleSelections.get(RuleType.SINGLE) ? Set.of(1, 5) : Set.of();
		int minMultiple = ruleSelections.get(RuleType.MULTIPLE) ? 2 : 0; // Example settings
		int minSet = ruleSelections.get(RuleType.SET) ? 3 : 0;
		int numStraitValues = ruleSelections.get(RuleType.STRAIT) ? 5 : 0;
		
		// Assuming ruleManager is already initialized and available
		ruleManager.initializeRules("TestGameID", minMultiple, minMultiple, singleValues, minSet, numStraitValues);
		
		System.out.println("Rules have been initialized. Starting the game...");
		// Continue with the game setup or start the gameplay loop
	}
	
	
	@Override
	public int getNumberOfPlayers() {
		gameplayUI.displayMessage("Enter the number of players (1-6): ");
		int numPlayers = inputManager.getInputInt();
		// Validate the number of players
		while (numPlayers < 1 || numPlayers > 6) {
			gameplayUI.displayMessage("Invalid number. Please enter a number between 1 and 6: ");
			numPlayers = inputManager.getInputInt();
		}
		return numPlayers;
	}
	
	@Override
	public List<String> getPlayerNames(int numPlayers) {
		List<String> names = new ArrayList<>();
		for (int i = 0; i < numPlayers; i++) {
			gameplayUI.displayMessage("Enter the name of player " + (i + 1) + ": ");
			names.add(inputManager.getInputString());
		}
		return names;
	}
	
	@Override
	public int getValidScoreLimit() {
		final int MIN_SCORE_LIMIT = 1000;
		int limit;
		
		while (true) {
			try {
				displayMessage("\nEnter the score limit (minimum " + MIN_SCORE_LIMIT + "): ");
				limit = inputManager.getInputInt();
				if (limit < MIN_SCORE_LIMIT) {
					displayMessage("Invalid score limit. Please try again.");
				} else {
					break;
				}
			} catch (Exception e) {
				clear();
				displayMessage("Invalid score limit. Please try again.");
				inputManager.getInputString(); // Clears the buffer.
			}
		}
		return limit;
	}
	
	public void handleGameOptions(Player currentPlayer, GameOptionManager gameOptionManager) {
		Score score = currentPlayer.score();
		List<GameOption> gameOptions = gameOptionManager.getGameOptions();
		
		gameplayUI.displayGameOptions(score, gameOptions, gameOptionManager.isOptionSelected());
		gameplayUI.displayMessage("Select an option: ");
		int choice = inputManager.getInputInt();
		
		while (choice < 1 || choice > gameOptions.size()) {
			gameplayUI.displayMessage("Invalid choice. Please select a valid option: ");
			choice = inputManager.getInputInt();
		}
		
		GameOption selectedOption = gameOptions.get(choice - 1);
		gameOptionManager.setSelectedGameOption(selectedOption);
		gameOptionManager.applyGameOption(selectedOption);
	}
	
	// Facade methods for the ConsoleMessage
	@Override
	public void displayWelcomeMessage() {
		gameplayUI.displayWelcomeMessage();
	}
	
	@Override
	public void displayGameOptions(Score score, List<GameOption> gameOptions, int numOptionsSelected) {
		gameplayUI.displayGameOptions(score, gameOptions, numOptionsSelected);
	}
	
	@Override
	public void displayCurrentScore(String playerName, int roundScore) {
		gameplayUI.displayCurrentScore(playerName, roundScore);
	}
	
	@Override
	public void displayDice(Dice dice) {
		gameplayUI.displayDice(dice);
	}
	
	@Override
	public void displayHighScoreInfo(Player currentPlayer, String highestScoringPlayerName) {
		gameplayUI.displayHighScoreInfo(currentPlayer, highestScoringPlayerName);
	}
	
	@Override
	public void displayMessage(String message) {
		gameplayUI.displayMessage(message);
	}
	
	@Override
	public void displayAndWait(String message) {
		gameplayUI.displayMessage(message);
		gameplayUI.pauseAndContinue(inputManager.waitForEnterKey());
	}
	
	@Override
	public void displayLastRoundMessage(Player gameEndingPlayer, Runnable waitFunction) {
		gameplayUI.displayLastRoundMessage(gameEndingPlayer, waitFunction);
	}
	
	@Override
	public void announceTie(List<Player> tiedPlayers, int score) {
		gameplayUI.announceTie(tiedPlayers, score);
	}
	
	@Override
	public void announceWinner(Player winner, int score) {
		gameplayUI.announceWinner(winner, score);
	}
	
	@Override
	public void displayRulesMenu() {
		gameplayUI.displayRulesMenu();
	}
	
	@Override
	public void clear() {
		gameplayUI.clear();
	}
	
	@Override
	public void pauseAndContinue(Runnable waitFunction) {
		gameplayUI.pauseAndContinue(waitFunction);
	}
}

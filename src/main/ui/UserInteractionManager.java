package ui;


import model.entities.ComputerDifficulty;
import model.entities.Dice;
import model.entities.GameOption;
import model.entities.Player;
import model.entities.PlayerConfiguration;
import model.entities.Score;
import model.entities.TurnContinuation;
import rules.managers.RuleRegistry;
import rules.managers.RuleType;
import rules.variable.IRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;


/**
 * Coordinates setup and gameplay input while delegating all actual rendering
 * to the configured message UI implementation.
 */
public class UserInteractionManager implements IMessage, IUserInteraction
{
	private final IMessage gameplayUI;
	private final IInputManager inputManager;

	/**
	 * Creates a user interaction manager with the default console UI and input
	 * implementations.
	 */
	@SuppressWarnings("unused")
	public UserInteractionManager() throws IOException {
		this(new Scanner(System.in));
	}

	/**
	 * Creates a user interaction manager with a shared scanner.
	 */
	public UserInteractionManager(Scanner scanner) throws IOException {
		this(new ConsoleMessage(scanner), new ConsoleInputManager(scanner));
	}

	/**
	 * Creates a user interaction manager that delegates rendering and input to
	 * the supplied collaborators.
	 */
	public UserInteractionManager(IMessage gameplayUI, IInputManager inputManager) {
		this.gameplayUI = gameplayUI;
		this.inputManager = inputManager;
	}

	/**
	 * Runs the initial setup prompts needed before the game is created.
	 */
	public void runGameSetup() {
		gameplayUI.displayWelcomeMessage();
		int numPlayers = getNumberOfPlayers();
		getPlayerNames(numPlayers);
	}

	/**
	 * Shared yes/no prompt helper used during setup and turn decisions.
	 */
	private boolean readYesNo(String message) {
		gameplayUI.displayMessage(message);
		String input = inputManager.getInputString().trim().toLowerCase();
		while (!input.equals("yes") && !input.equals("no") && !input.equals("y") && !input.equals("n")) {
			gameplayUI.displayMessage("Invalid input. Please enter 'yes' or 'no' [y/n]: ");
			input = inputManager.getInputString().trim().toLowerCase();
		}
		return input.equals("yes") || input.equals("y");
	}

	private boolean readYesNo(String message, boolean defaultValue) {
		gameplayUI.displayMessage(message);
		String input = inputManager.getInputString().trim().toLowerCase();
		if (input.isEmpty()) {
			return defaultValue;
		}
		while (!input.equals("yes") && !input.equals("no") && !input.equals("y") && !input.equals("n")) {
			gameplayUI.displayMessage("Invalid input. Please enter 'yes' or 'no' [y/n]: ");
			input = inputManager.getInputString().trim().toLowerCase();
			if (input.isEmpty()) {
				return defaultValue;
			}
		}
		return input.equals("yes") || input.equals("y");
	}

	/**
	 * Presents the discovered selectable rules and returns the subset enabled by
	 * the user along with each rule's default configuration.
	 */
	@Override
	public Map<RuleType, Object> selectRules() {
		RuleRegistry ruleRegistry = new RuleRegistry();
		List<IRule> selectableRules = ruleRegistry.getAvailableRules()
		                                          .stream()
		                                          .filter(IRule::isSelectableAtSetup)
		                                          .toList();
		Map<RuleType, Object> selectedConfig = new LinkedHashMap<>();

		gameplayUI.displayRulesMenu();

		while (!hasScoringRuleSelected(selectedConfig, selectableRules)) {
			selectedConfig.clear();

			for (IRule rule : selectableRules) {
				boolean isEnabled = readYesNo(
						"Enable " + rule.getDisplayName() + " (" + rule.getDescription() + ")? " +
								(rule.isEnabledByDefault() ? "[Y/n] " : "[y/N] "),
						rule.isEnabledByDefault()
				);
				if (isEnabled) {
					selectedConfig.put(rule.getRuleType(), rule.getDefaultConfig());
				}
			}

			if (!hasScoringRuleSelected(selectedConfig, selectableRules)) {
				gameplayUI.displayMessage(
						"At least one scoring rule must be enabled. Please choose again.\n"
				);
			}
		}

		return selectedConfig;
	}

	private boolean hasScoringRuleSelected(Map<RuleType, Object> selectedConfig, List<IRule> selectableRules) {
		return selectableRules.stream()
		                      .filter(rule -> selectedConfig.containsKey(rule.getRuleType()))
		                      .anyMatch(IRule::isScoringRule);
	}

	/**
	 * Prompts for the player count and enforces the supported range.
	 */
	@Override
	public int getNumberOfPlayers() {
		gameplayUI.displayMessage("Enter the number of players (1-6): ");
		int numPlayers = inputManager.getInputInt();
		// Validate the number of players.
		while (numPlayers < 1 || numPlayers > 6) {
			gameplayUI.displayMessage("Invalid number. Please enter a number between 1 and 6: ");
			numPlayers = inputManager.getInputInt();
		}
		return numPlayers;
	}

	/**
	 * Collects each player's display name in turn order.
	 */
	@Override
	public List<String> getPlayerNames(int numPlayers) {
		List<String> names = new ArrayList<>();
		for (int i = 0; i < numPlayers; i++) {
			gameplayUI.displayMessage("Enter the name of player " + (i + 1) + ": ");
			names.add(inputManager.getInputString());
		}
		return names;
	}

	/**
	 * Collects optional computer-player metadata after the existing name prompts.
	 */
	@Override
	public List<PlayerConfiguration> getPlayerConfigurations(int numPlayers) {
		List<String> names = getPlayerNames(numPlayers);
		List<PlayerConfiguration> configurations = new ArrayList<>();
		for (String name : names) {
			boolean computer = readYesNo("Make " + name + " a computer player? [y/N] ", false);
			configurations.add(computer
					? PlayerConfiguration.computer(name, readComputerDifficulty(name))
					: PlayerConfiguration.human(name));
		}
		return configurations;
	}

	private ComputerDifficulty readComputerDifficulty(String playerName) {
		gameplayUI.displayMessage(
				"Choose " + PlayerText.possessive(playerName) +
						" difficulty [easy/medium/hard] (default medium): "
		);
		while (true) {
			String input = inputManager.getInputString().trim();
			if (input.isEmpty()) {
				return ComputerDifficulty.MEDIUM;
			}
			try {
				return ComputerDifficulty.valueOf(input.toUpperCase(Locale.ROOT));
			} catch (IllegalArgumentException exception) {
				gameplayUI.displayMessage("Invalid difficulty. Enter easy, medium, or hard: ");
			}
		}
	}

	/**
	 * Prompts for the Winning Score and keeps asking until a valid minimum is met.
	 */
	@Override
	public int getValidScoreLimit() {
		final int minScoreLimit = 1000;
		int limit;

		while (true) {
			try {
				displayMessage("\nEnter the Winning Score (minimum " + minScoreLimit + "): ");
				limit = inputManager.getInputInt();
				if (limit < minScoreLimit) {
					displayMessage("Invalid Winning Score. Winning Score must be at least 1000. Please try again.");
				} else {
					break;
				}
			} catch (Exception e) {
				clear();
				displayMessage("Invalid Winning Score. Please try again.");
				inputManager.getInputString(); // Clears the buffer.
			}
		}
		return limit;
	}

	/**
	 * Prompts for the score a player must bank before they are considered on.
	 */
	@Override
	public int getValidOpeningScoreLimit(int scoreLimit) {
		while (true) {
			displayMessage(
					"\nEnter the Opening Score from 0 to " + scoreLimit + " (usually 1000): "
			);
			int limit = inputManager.getInputInt();
			if (limit >= 0 && limit <= scoreLimit) {
				return limit;
			}
			displayMessage("Invalid Opening Score. Enter a value from 0 to " + scoreLimit + ".");
		}
	}

	/**
	 * Lets the user choose one of the rule-generated options for the current roll.
	 */
	@Override
	public GameOption chooseGameOption(Player currentPlayer, List<GameOption> gameOptions) {
		Score score = currentPlayer.score();
		// Display the list of scoring options.
		gameplayUI.displayGameOptions(score, gameOptions);
		// Prompt the user to choose one.
		gameplayUI.displayMessage("Select an option (enter the option number): ");
		int choice = inputManager.getInputInt();

		while (choice < 1 || choice > gameOptions.size()) {
			gameplayUI.displayMessage("Invalid choice. Please select a valid option: ");
			choice = inputManager.getInputInt();
		}

		return gameOptions.get(choice - 1);
	}

	@Override
	public boolean shouldScoreMore(Player currentPlayer, List<GameOption> remainingOptions) {
		return readYesNo(
				"Score another option from this roll (" + remainingOptions.size() + " available)? "
		);
	}

	/**
	 * Asks whether the user wants to continue the turn or bank the current round
	 * score. If the player has not opened yet, the turn must continue.
	 */
	@Override
	public boolean shouldRollAgain(Player currentPlayer, boolean canBankPoints, int openingScoreLimit) {
		if (!canBankPoints) {
			gameplayUI.displayMessage(
					currentPlayer.name() + " cannot bank points yet. You need " +
							openingScoreLimit + " points to open.\n"
			);
			return true;
		}
		return readYesNo("Roll again? Enter 'yes' to continue or 'no' to bank this turn: ");
	}

	/**
	 * Offers an eligible player the prior turn's score and remaining dice.
	 */
	@Override
	public boolean shouldSteal(Player currentPlayer, TurnContinuation continuation) {
		return readYesNo(
				currentPlayer.name() + " may continue " +
						PlayerText.possessive(continuation.sourcePlayerName()) + " " +
						continuation.inheritedScore() + "-point turn by rolling " +
						continuation.diceInPlay() + " remaining dice. Continue that turn? "
		);
	}

	/**
	 * Facade method that forwards the welcome message to the underlying UI.
	 */
	@Override
	public void displayWelcomeMessage() {
		gameplayUI.displayWelcomeMessage();
	}

	/**
	 * Facade method that forwards option rendering to the underlying UI.
	 */
	@Override
	public void displayGameOptions(Score score, List<GameOption> gameOptions) {
		gameplayUI.displayGameOptions(score, gameOptions);
	}

	/**
	 * Facade method that forwards the current score display to the underlying UI.
	 */
	@Override
	public void displayCurrentScore(String playerName, int roundScore) {
		gameplayUI.displayCurrentScore(playerName, roundScore);
	}

	/**
	 * Facade method that forwards dice rendering to the underlying UI.
	 */
	@Override
	public void displayDice(Dice dice) {
		gameplayUI.displayDice(dice);
	}

	/**
	 * Facade method that forwards high-score messaging to the underlying UI.
	 */
	@Override
	public void displayHighScoreInfo(Player currentPlayer, String highestScoringPlayerName) {
		gameplayUI.displayHighScoreInfo(currentPlayer, highestScoringPlayerName);
	}

	/**
	 * Facade method that forwards plain text output to the underlying UI.
	 */
	@Override
	public void displayMessage(String message) {
		gameplayUI.displayMessage(message);
	}

	/**
	 * Facade method that shows a message and waits for acknowledgement.
	 */
	@Override
	public void displayAndWait(String message) {
		gameplayUI.displayMessage(message);
		gameplayUI.pauseAndContinue(inputManager.waitForEnterKey());
	}

	/**
	 * Facade method that forwards the last-round message to the underlying UI.
	 */
	@Override
	public void displayLastRoundMessage(Player gameEndingPlayer, Runnable waitFunction) {
		gameplayUI.displayLastRoundMessage(gameEndingPlayer, waitFunction);
	}

	/**
	 * Facade method that forwards tie announcements to the underlying UI.
	 */
	@Override
	public void announceTie(List<Player> tiedPlayers, int score) {
		gameplayUI.announceTie(tiedPlayers, score);
	}

	/**
	 * Facade method that forwards winner announcements to the underlying UI.
	 */
	@Override
	public void announceWinner(Player winner, int score) {
		gameplayUI.announceWinner(winner, score);
	}

	/**
	 * Facade method that forwards the rules menu display to the underlying UI.
	 */
	@Override
	public void displayRulesMenu() {
		gameplayUI.displayRulesMenu();
	}

	/**
	 * Facade method that clears the underlying UI when supported.
	 */
	@Override
	public void clear() {
		gameplayUI.clear();
	}

	/**
	 * Facade method that pauses through the underlying UI implementation.
	 */
	@Override
	public void pauseAndContinue(Runnable waitFunction) {
		gameplayUI.pauseAndContinue(waitFunction);
	}
}
